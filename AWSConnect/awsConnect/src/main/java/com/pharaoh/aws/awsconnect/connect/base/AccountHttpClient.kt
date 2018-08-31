package com.mgwater.mgbaseproject.connect

import okhttp3.*
import okhttp3.internal.tls.OkHostnameVerifier
import java.awt.image.BufferedImage
import java.io.IOException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSession
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO




/**
 * Created by 志朋 on 2017/12/9.
 * 原封裝作者: ngudream
 * 網址: http://ngudream.com/2017/06/20/kotlin-okhttp/
 *
 * 此類 與 AccountRespnse 一套
 */
class AccountHttpClient private constructor() {
    private val TAG = "AccountHttpClient"
    /**
     * 只使用一个实例的，以便重用response cache、thread pool、connection re-use 等
     */
    private val mOkHttpCilent: OkHttpClient

    private object Holder {
        val INSTANCE = AccountHttpClient()
    }

    companion object {
        //构造单例
        private var CONNECTION_TIME_OUT = 30 * 1000
        private var READ_TIME_OUT = 30 * 1000
        private var WRITE_TIME_OUT = 30 * 1000
        val instance: AccountHttpClient by lazy { Holder.INSTANCE }
    }

    //由于primary constructor不能包含任何代码，因此使用 init 代码块对其初始化，同时可以在初始化代码块中使用构造函数的参数
    //並且保持 cookie
    init {
        mOkHttpCilent = OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)//注意显示转化toLong
                .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .hostnameVerifier(AccountHostnameVerifier())
                .cookieJar(object : CookieJar {
                    private var cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()
                    override fun loadForRequest(url: HttpUrl?): MutableList<Cookie> {
                        if (url != null) {
                            val cookies: List<Cookie>? = cookieStore[url.host()]
                            return if (cookies != null) cookies.toMutableList() else mutableListOf()
                        }
                        return mutableListOf()
                    }

                    override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
                        if (url != null && cookies != null)
                            cookieStore.put(url.host(), cookies)
                    }

                })
                .build()
    }


    /**
     * 執行請求, 依照 MGRequestContent 進行的封裝
     */
    //阿水自行加入
    fun execute(content: MGRequestContent): AccountResponse? {
        var result: AccountResponse? = null
        try {
            var okRequest = MGOKRequest(content)
            val okResponse = performRequest(okRequest) //進行參數封裝, 發起網路請求
            if (okResponse != null) {
                result = AccountResponse(okResponse.responseCode, okResponse.body, okResponse.headers)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    internal enum class Type {
        GET,
        POST
    }

    private inner class MGOKRequest(content: MGRequestContent) {
        private var content: MGRequestContent = content

        fun getDataRequest(): Request {//此处可以改成request的get()方法
            val request: Request
            val builder = Request.Builder()

            builder.header("Connection", "Close")

            //先加入header
            if (content.headers?.isNotEmpty() ?: false) {
                for (header in content.headers!!) {
                    builder.addHeader(header.first, header.second)
                }
            }

            //創建 url builder 設定相關參數
            var urlBuilder = HttpUrl.Builder()
                    .scheme(content.scheme)
                    .host(content.host)
                    .encodedPath(content.path)

            when (content.method) {
                MGRequestContent.Method.GET -> {
                    //加入query string
                    content.params?.let {
                        for (param in it) {
                            urlBuilder.addQueryParameter(param.first, param.second)
                        }
                    }
                    val httpUrl = urlBuilder.build()
//                    println("連線url: $httpUrl")
                    builder.url(httpUrl)
                    builder.get()
                }
                MGRequestContent.Method.POST -> {

                    //POST需要檢測是否有檔案需要上傳
                    val httpUrl = urlBuilder.build()
                    builder.url(httpUrl)

                    val uploadDatas = content.uploadDatas
                    if (uploadDatas != null) {
                        //有檔案需要上傳
                        val bodyBuilder = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)

                        uploadDatas.forEach {
                            val uploadName = it.first
                            val uploadData = it.second
                            var requestBody: RequestBody? = null

                            //目前只支持Bitmap, String, byte Array的上傳
                            when (uploadData) {
                                is BufferedImage -> { //圖檔直接轉為byte array
                                    val stream = ByteArrayOutputStream()
                                    ImageIO.write(uploadData, "jpg", stream)
                                    stream.flush()
                                    val byteArray = stream.toByteArray()
                                    stream.close()
                                    val type = MediaType.parse("image/png")
                                    requestBody = RequestBody.create(type, byteArray)
                                }
                                is String -> {
                                    requestBody = RequestBody.create(null, uploadData)
                                }
                                is ByteArray -> {
                                    requestBody = RequestBody.create(null, uploadData)
                                }
                            }

                            if (requestBody != null) {
                                bodyBuilder.addFormDataPart(uploadName, "file", requestBody)
                            }
                        }

                        //加入參數
                        content.params?.let {
                            for (param in it) {
                                bodyBuilder.addFormDataPart(param.first, param.second)
                            }
                            builder.post(bodyBuilder.build())
                        }
                        builder.header("Content-Type", "multipart/form-data")
                    } else {
                        //沒有檔案需要上傳
                        content.params?.let {
                            val formBuilder = FormBody.Builder()
                            for (param in it) {
                                formBuilder.add(param.first, param.second)
                            }
                            builder.post(formBuilder.build())
                        }
                    }
                }
            }

            request = builder.build()
            return request
        }
    }

    /**
     * 響應
     */
    private inner class OKResponse(private val response: Response?) {
        val responseCode: Int
            get() = response!!.code()
        val body: ByteArray?
            get() {
                var result: ByteArray? = null
                if (response?.body() != null) {
                    try {
                        result = response.body()!!.bytes()
                    } catch (e: IOException) {
                        println("$TAG - ${e.message}")
                    }
                    headers
                }
                return result
            }
        val headers: MutableMap<String, MutableList<String>>?
            get() {
                var headers: MutableMap<String, MutableList<String>>? = null
                if (response?.headers() != null) {
                    try {
                        headers = response.headers().toMultimap()
                    } catch (e: Exception) { }
                }
                return headers
            }
    }

    @Throws(IOException::class)
    private fun performRequest(request: MGOKRequest): OKResponse? {
        val call = mOkHttpCilent.newCall(request.getDataRequest())//new一个call，通过okhttp发起请求
        return OKResponse(call.execute())
    }


//    @Throws(IOException::class)
//    private fun performRequest(request: OKRequest): OKResponse? {
//        val call = mOkHttpCilent.newCall(request.getDataRequest())//new一个call，通过okhttp发起请求
//        return OKResponse(call.execute())
//    }

    /**
     * 證書驗證
     */
    private inner class AccountHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            var result = false
            try {
                val certs = session.peerCertificates as? Array<X509Certificate> ?: return false
                if (certs.isNotEmpty()) {
                    for (i in certs.indices) {//把证书取出，一个一个验证
                        result = OkHostnameVerifier.INSTANCE.verify(hostname, certs[i])
                        if (result) {
                            break
                        }
                    }
                } else {
                    result = true
                }
            } catch (e: SSLPeerUnverifiedException) {
                e.printStackTrace()
            }
            return result
        }
    }
}