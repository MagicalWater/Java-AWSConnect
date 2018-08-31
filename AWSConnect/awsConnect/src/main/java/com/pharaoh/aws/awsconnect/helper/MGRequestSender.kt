package com.mgwater.mgbaseproject.helper

import com.magicalwater.mgbaseproject.connect.MGUrlRequest
import com.mgwater.mgbaseproject.connect.MGRequestConnect

/**
 * Created by 志朋 on 2017/12/11.
 * 一般呼叫 api(非跳頁, 或者須完全自行處理的)
 */
class MGRequestSender(delegate: MGRequestSenderDelegate): MGRequestConnect.MGRequestCallback {

    /**
     * 一般呼叫 api 都會有統一回傳的接口
     * 為了分辨誰是誰, 我們得給每次的 request 加上一個編號
     * 通常request 只有幾種分類, 未免每次都重新定義, 這邊直接定義幾種
     */
    companion object {
        val REQUEST_DEFAUT = -1
        val REQUEST_LOAD_MORE = -2
        val REQUEST_LOAD_TOP = -3
        val REQUEST_REFRESH = -4
    }

    val delegate: MGRequestSenderDelegate = delegate

    //儲存所有的 request
    private var requests: MutableMap<Int, MGUrlRequest> = mutableMapOf()


    /**************************供外部呼叫 以下********************************/
    //發送 REQUEST, 默認 code 是 REQUEST_DEFAUT
    fun send(request: MGUrlRequest, requestCode: Int = REQUEST_DEFAUT) {
        MGRequestConnect.getData(request, requestCode, this)
    }
    /**************************供外部呼叫 結束********************************/


    //request的回傳
    override fun response(request: MGUrlRequest, requestCode: Int, success: Boolean) {
        //首先要得到返回的 request 的 code 是什麼
        //萬一沒有找到code則不做任何處理
        delegate.response(request, success, requestCode)
    }

}

//處理 MGRequestSender 的回傳
interface MGRequestSenderDelegate {
    fun response(request: MGUrlRequest, success: Boolean, requestCode: Int)
}