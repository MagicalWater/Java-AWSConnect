package com.pharaoh.aws.awsconnect.connect

import com.mgwater.mgbaseproject.connect.MGRequestContent
import java.net.URL
import java.util.*

/**
 * Created by magicalwater on 2018/6/5.
 */
class RequestBodyFactory {

    companion object {

        //類型, 是 http 或者 https
        private val scheme: MGRequestContent.Scheme = MGRequestContent.Scheme.HTTP
        private val host: String = "7eapp.honor-financial.com"

        //商家圖片取得相關
        private val store_image_uri = "/images/"

        //公告圖片取得相關
        private val anno_image_uri = "/images/announcement/"

        //頭像取得相關
        private val header_image_uri = "/images/user/"

        //文章圖片取得相關
        private val article_image_uri = "/images/article/"

        /**
         * 會員登入: /api/1.0/login
         * POST方法 須帶入
         * 1. user - 帳號
         * 2. password - 密碼
         * */
        private val api_login = "/api/1.0/login"

        fun login(user: String, password: String): MGRequestContent {
            var content =
                    MGRequestContent(scheme, host, api_login, MGRequestContent.Method.POST)
                            .addParam("user", user, false)
                            .addParam("password", password, false)
//                            .setDeserialize(Api_L_Login::class)
            return content
        }

    }


}