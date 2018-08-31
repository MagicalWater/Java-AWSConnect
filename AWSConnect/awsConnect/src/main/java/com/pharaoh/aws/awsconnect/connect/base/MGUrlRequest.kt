package com.magicalwater.mgbaseproject.connect

import com.mgwater.mgbaseproject.connect.MGRequestContent

/**
 * Created by magicalwater on 2017/11/30.
 * 網路 request 需要裝入的 class
 */
class MGUrlRequest private constructor(
        content         : Array<MGRequestContent>,
        sort            : Array<Array<Int>>?,
        executeType     : MGExecuteType,
        requestTag      : String?
//        responseType    : Array<MGResponseDataType>?
) {

    //所要連接的 url 相關參數, 反序列化相關
    var content: Array<MGRequestContent> = content

    //request 回來的 response
    var response: Array<MGResponse> = emptyArray()

    //request 執行順序
    var runSort: Array<Array<Int>>

    //整個 Request 執行的方式
    var executeType: MGExecuteType = executeType

    //資料是否過期, 給 MGFgtManager 統一設置是否過期的標籤, 當過期代表資料不可重用
    var isExpired: Boolean = false

    //將此request加上標記
    var requestTag: String? = requestTag

//    //返回的資料格式, 例如圖片(二位元組), 字串 之類
//    var responseType: Array<MGResponseDataType>
    
    init {

        //如果沒有自訂順序, 則執行順序是串連
        when (sort) {
            null -> runSort = Array(content.size) { index -> arrayOf(index) }
            else -> runSort = sort
        }

        response = Array(content.size) { MGResponse() }

        //如果沒有自訂返回資料格式, 則默認是Text
//        when (responseType) {
//            null -> this.responseType = Array(url.size) { index -> MGResponseDataType.TEXT }
//            else -> this.responseType = responseType
//        }
    }

    //返回的資料形式
    enum class MGResponseDataType {
        IMAGE, TEXT
    }


    data class MGResponse(private val responseCode: String) {

        constructor() : this("-1")

        var instance: Any? = null
        var code: String = responseCode
        var message: String? = null
        var isSuccess: Boolean = false
        var httpStatus: Int? = null

        fun <T> getIns(): T = instance as T

    }

    //request執行的類型
    enum class MGExecuteType {
        SUCCESS_BACK,   //只要遇到成功即回傳, 換句話說直到成功為止
        ALL,            //即使中間發生錯誤也不回傳, 依定執行完所有 url 的 request才回傳
        DEFAULT         //預設, 只要發生錯誤就回傳
    }

    //request 每個 url 執行順序的類型
    enum class MGSortType {
        CUSTOM, //自訂順序
        CONCURRENT, //併發
        DEFAULT, //依照 urls 的順序一個一個往下
    }



    /**
     * 構建 UrlRequest
     * */
    class MGRequestBuilder {
        private var content: Array<MGRequestContent> = emptyArray()
        private var runSort: Array<Array<Int>>? = null
        private var runSortType: MGSortType = MGSortType.DEFAULT
        private var executeType: MGExecuteType = MGExecuteType.DEFAULT
        private var responseType: Array<MGResponseDataType>? = null

        //api request的標記, 方便確認request是哪個
        private var requestTag: String? = null

        fun setTag(tag: String): MGRequestBuilder {
            this.requestTag = tag
            return this
        }

        fun setUrlContent(vararg content: MGRequestContent): MGRequestBuilder {
            this.content = content as Array<MGRequestContent>
            return this
        }

        fun setRunSort(runSort: Array<Array<Int>>? = null, type: MGSortType = MGSortType.DEFAULT): MGRequestBuilder {
            this.runSort = runSort
            this.runSortType = type
            return this
        }

        fun setExecuteType(type: MGExecuteType): MGRequestBuilder {
            this.executeType = type
            return this
        }

        fun setResponseDataType(vararg type: MGResponseDataType): MGRequestBuilder {
            this.responseType = type as Array<MGResponseDataType>
            return this
        }

        fun build(): MGUrlRequest {
            val ins = MGUrlRequest(
                    content,
                    runSort,
                    executeType,
                    requestTag
            )
            return ins
        }
    }
}


