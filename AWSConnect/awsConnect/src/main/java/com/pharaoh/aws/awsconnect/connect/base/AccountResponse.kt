package com.mgwater.mgbaseproject.connect

import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by 志朋 on 2017/12/9.
 * 網路上的朋友使用 kotlin 對於 okhttp3 的封裝
 * 原封裝作者: ngudream
 * 網址: http://ngudream.com/2017/06/20/kotlin-okhttp/
 * 這個是返回資料的部分
 *
 * 此類 與 AccountHttpClient 一套
 */

class AccountResponse {
    var statusCode: Int = 0
        private set
    private var responseAsString: String? = null
    private var responseAsBytes: ByteArray? = null
    private var headers: MutableMap<String, MutableList<String>>? = null
    /**
     * 创建获取InputStream中数据的结果结构。
     * <br></br>注意，这个构造方法会直接从输入流中读取数据并且关闭输入流，后续不能再对输入流进行操作（同时，输入流可能在其他地方被关闭，所以不要在构造方法之外操作它）。
     */
    @Throws(IOException::class)
    constructor(statusCode: Int, datas: ByteArray?, headers: MutableMap<String, MutableList<String>>?) {
        this.statusCode = statusCode
        // 在这里获取stream的数据，因为该方法之后stream会close掉
        this.responseAsBytes = datas
        this.headers = headers
    }
    constructor(content: String, responseCode: Int) {
        responseAsString = content
        statusCode = responseCode
    }
    fun getResponseAsString(): String? {
        responseAsString = responseAsBytes?.toString(Charset.forName("utf-8"))
        return responseAsString
    }
    fun getHeadersByName(name: String): MutableList<String>? {
        if (headers != null) {
            return headers?.get(name)
        }
        return null
    }

    fun getHeaders(): MutableMap<String, MutableList<String>>? {
        return headers
    }

    override fun toString(): String {
        if (null != getResponseAsString()) {
            return responseAsString?: ""
        }
        return "Response{" +
                "statusCode=" + statusCode +
                ", responseString='" + responseAsString +
                '}'
    }
}