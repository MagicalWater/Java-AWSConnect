package com.pharaoh.aws.awsconnect.util

/**
 * Created by magicalwater on 2018/6/5.
 */
class ResourceUtils {

    companion object {
        fun getString(fullPath: String): String? {
            val ins = ResourceUtils::class.java.getResourceAsStream(fullPath)
            return if (ins != null) {
                val buffer = ins.readBytes()
                String(buffer)
            } else {
                null
            }
        }
    }
}