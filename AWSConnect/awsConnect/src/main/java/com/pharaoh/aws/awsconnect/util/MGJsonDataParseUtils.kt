package com.mgwater.mgbaseproject.util

import com.google.gson.Gson

/**
 * Created by magicalwater on 2018/1/13.
 * 解析所有有關json的東西, 無論是序列化或者反序列化
 */
class MGJsonDataParseUtils {

    companion object {

        //反序列化, 將json變成物件
        fun <T>deserialize(json: String, deserialize: Class<T>): T? {
            val gson = Gson()

            //這邊使用 try catch 以免發生例外
            var ins: Any? = null
            try {
                ins = gson.fromJson(json, deserialize)
            } catch (e: Exception) {
                println("解析時發生錯誤, 原因: ${e.message}")
                e.printStackTrace()
            }

            println("解析成功")

            return ins as? T?
        }

        //序列化, 將物件變成json字串
        fun serialize(data: Any): String {
            val gson = Gson()
            return gson.toJson(data)
        }
    }
}