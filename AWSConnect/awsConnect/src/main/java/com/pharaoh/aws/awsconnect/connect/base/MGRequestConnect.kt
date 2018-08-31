package com.mgwater.mgbaseproject.connect

import com.magicalwater.mgbaseproject.connect.MGUrlRequest
import com.mgwater.mgbaseproject.util.MGThreadUtils
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.reflect.KClass

/**
 * Created by 志朋 on 2017/12/3.
 * 網路要求資料的class, 封裝網路要求的 class 是 AccountHttpClient
 * 此類針對 Request Builder 進行封裝
 * 主要處理線程併發
 */
class MGRequestConnect {

    companion object {

        //設置此參數以方便自訂反序列化的處理
        lateinit var responseParserHandler: MGResponseParser

        fun getData(request: MGUrlRequest, requestCode: Int, cbk: MGRequestCallback) {

            //這邊直接使用開一個新的線程執行網路要求
            doAsync {
                loopRequestStep(this, request, requestCode, cbk)
            }
//            Thread {
//                loopRequestStep(request, requestCode, cbk)
//            }.start()
        }


        //開始循環獲取資料
        private fun loopRequestStep(context: AnkoAsyncContext<MGRequestConnect.Companion>, request: MGUrlRequest, requestCode: Int, cbk: MGRequestCallback) {

            for (i in 0 until request.runSort.size) {

                //開始執行並聯需求
                val runSort = request.runSort[i]

                MGThreadUtils.inSubMultiple(runSort.size) { number ->

                    //得到執行的urlIndex
                    val urlIndex = runSort[number]
                    println("執行 urlIndex = $urlIndex")

                    distributionConnect(context, request, cbk, urlIndex)
                }

                //執行完一個階段, 需要根據設置的連線類型檢查錯誤
                val nextStep = checkExecuteStatus(request, runSort)
                if (!nextStep) {

                    //有可能不繼續執行下個步驟的為
                    //DEFAULT, SUCCESS_BACK
                    //若是 DEFAULT 則成功參數返回FALSE 沒有問題
                    //若是 SUCCESS_BACK 則是找到了成功的案例, 則返回true

                    when (request.executeType) {
                        MGUrlRequest.MGExecuteType.SUCCESS_BACK -> {
                            context.uiThread { cbk.response(request, requestCode, true) }
                            return
                        }

                        MGUrlRequest.MGExecuteType.DEFAULT -> {
                            context.uiThread { cbk.response(request, requestCode, false) }
                            return
                        }
                    }

                } else {
                    //代表可以繼續往下執行
                    //同時檢測是否有下個step, 有的話呼叫handler的 multipleRequest
                    //方便特殊處理
                    if (i < request.runSort.size - 1 && request.requestTag != null) {
                        responseParserHandler.multipleRequest(request, request.requestTag!!, i)
                    }
                }

            }

            //執行到最後了
            //DEFAULT, SUCCESS_BACK, ALL
            //若是 ALL          則成功參數返回TRUE 沒有問題
            //若是 DEFAULT      則成功參數返回TRUE 沒有問題
            //若是 SUCCESS_BACK 則是沒有成功, 回傳 FALSE

            when (request.executeType) {
                MGUrlRequest.MGExecuteType.SUCCESS_BACK ->
                    context.uiThread { cbk.response(request, requestCode, false) }
                MGUrlRequest.MGExecuteType.DEFAULT ->
                    context.uiThread { cbk.response(request, requestCode, true) }
                MGUrlRequest.MGExecuteType.ALL ->
                    context.uiThread { cbk.response(request, requestCode, true) }
            }

        }

        /**
         * 檢查某階段的request執行狀態, 根據不同的設置有不同的處理方式
         * @param executeIndex: 代表此階段執行了這些index的url, 所以檢查是針對這些request做檢查
         * 回傳: 代表是否需要繼續執行下個階段, 而非是否發生錯誤
         */
        private fun checkExecuteStatus(request: MGUrlRequest, executeIndex: Array<Int>): Boolean {
            when (request.executeType) {
            //全部執行完畢才返回, 所以不檢查錯誤
                MGUrlRequest.MGExecuteType.ALL -> {
                    return true
                }

            //當某階段全部成功後即返回
                MGUrlRequest.MGExecuteType.SUCCESS_BACK -> {

                    //只要有一個沒有成功(code不為0)就直接跳出並且返回true代表需要繼續往下執行
                    for (run in executeIndex) when (!request.response[run].isSuccess) {
                        true -> return true
                    }

                    return false
                }

            //當出現錯後即刻返回
                MGUrlRequest.MGExecuteType.DEFAULT -> {
                    for (run in executeIndex) when (!request.response[run].isSuccess) {
                        true -> return false
                    }

                    return true
                }
            }
        }


        //開始處理request, 從本地快取撈資料, 或者使用 get, post取資料
        private fun distributionConnect(context: AnkoAsyncContext<MGRequestConnect.Companion>,
                                        request: MGUrlRequest, cbk: MGRequestCallback, urlIndex: Int) {

            //接著判斷是否需要從本地撈取資料, 以及本地有無資料存在
            //資料庫快取部分尚未完成, 因此這部分直接略過
            if (request.content[urlIndex].locale.load) {
                return
            }

            startConnect(context, request, cbk, urlIndex)
        }


        //確定要從網路撈取資料了
        private fun startConnect(context: AnkoAsyncContext<MGRequestConnect.Companion>,
                                 request: MGUrlRequest, cbk: MGRequestCallback, urlIndex: Int) {
            println("開始連線: ${request.content[urlIndex]}")
            val response: AccountResponse? = AccountHttpClient.instance.execute(request.content[urlIndex])

            //首先判斷 連線的狀態, 狀態成功, api才成功
//            var isRequestSuccess = false

//            if (response != null) isRequestSuccess = responseParserHandler.isResponseStatsSuccess(response)

//            var codes = response?.getHeadersByName("code")
//            if (response?.statusCode == 200 && codes != null && codes.size > 0) isRequestSuccess = true

            println("連線完畢: 狀態 - ${response?.statusCode}, header - ${response?.getHeaders()}")

            val result: String? = response?.getResponseAsString()

            if (result == null || response == null) {
                //沒有得到返回字串, 印出 status code
                println("返回 失敗: $result")
            } else {
                println("返回 class - ${request.content[urlIndex].deserialize?.simpleName}: $result")

                //如果沒有設置 反序列化的 handler 的話, 一律發生錯誤
                val response = responseParserHandler.parser(context, response, result, request.content[urlIndex].deserialize)
                request.response[urlIndex] = response
            }

        }
    }


    interface MGRequestCallback {
        fun response(request: MGUrlRequest, requestCode: Int, success: Boolean)
    }

    interface MGResponseParser {

        //傳回的資料狀態是否為成功
        fun isResponseStatsSuccess(response: AccountResponse): Boolean

        //如果有多筆request sort, 則每個step結束後都會呼叫此方法
        //前提是request帶有tag, step為當前執行到第幾個step結束
        fun multipleRequest(request: MGUrlRequest, tag: String, step: Int)

        //解析傳回的字串檔案
        fun parser(context: AnkoAsyncContext<MGRequestConnect.Companion>, response: AccountResponse?,
                   result: String, deserialize: KClass<out Any>?): MGUrlRequest.MGResponse
    }
}