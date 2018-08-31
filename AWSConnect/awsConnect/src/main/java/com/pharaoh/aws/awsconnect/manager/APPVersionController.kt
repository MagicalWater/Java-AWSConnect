package com.pharaoh.aws.awsconnect.manager

import com.mgwater.mgbaseproject.util.MGJsonDataParseUtils
import com.pharaoh.aws.awsconnect.connect.AmazonS3Manager
import com.pharaoh.aws.awsconnect.connect.model.version.APIVersionFile

/**
 * Created by magicalwater on 2018/6/5.
 */

class APPVersionController {

    private val versionFullPath: String = "version2.txt"
    private val appWorkOn: String = "platform"
    private val deviceSystem: String = "ios"

    private var deserializeClass: APIVersionFile

    init {
        //初始化時自動仔入當前s3的配置
        val content = AmazonS3Manager.downloadText(versionFullPath)!!
        deserializeClass = MGJsonDataParseUtils.deserialize(content, APIVersionFile::class.java)!!
    }

    //ios只改平台, 因此這邊固定抓平台
    private fun versionChange(targetInfo: S3TargetInfo, version: Float, message: String) {
        deserializeClass.flavor[targetInfo.flavorName]!![appWorkOn]!![targetInfo.betType]!![deviceSystem]!!.release = version
        deserializeClass.flavor[targetInfo.flavorName]!![appWorkOn]!![targetInfo.betType]!![deviceSystem]!!.update.forEach {

        }
    }
}