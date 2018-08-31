package com.pharaoh.aws.awsconnect.manager

import com.mgwater.mgbaseproject.util.MGJsonDataParseUtils
import com.pharaoh.aws.awsconnect.connect.model.s3Config.APIS3Config
import com.pharaoh.aws.awsconnect.util.ResourceUtils

/**
 * Created by magicalwater on 2018/6/5.
 */
class S3Configure {

    private val mConfigPath = "/s3Config.json"
    private var mConfigDeserializeClass: APIS3Config

    init {
        //初始化時反序列化
        val configText = ResourceUtils.getString(mConfigPath)!!
        mConfigDeserializeClass = MGJsonDataParseUtils.deserialize(configText, APIS3Config::class.java)!!
    }

    fun getTargetInfo(type: String, flavor: String): S3TargetInfo? {
        val typeInfo = mConfigDeserializeClass.betTypeMap[type] ?: return null
        val flavorInfo = typeInfo[flavor] ?: return null
        return S3TargetInfo(type, flavorInfo.flavor, flavorInfo.ipa_from, flavorInfo.ipa_to)
    }

}


data class S3TargetInfo(val betType: String, val flavorName: String, val from: String, val to: String)