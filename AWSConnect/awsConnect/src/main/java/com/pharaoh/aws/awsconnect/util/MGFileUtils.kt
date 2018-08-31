package com.mgwater.mgbaseproject.util

import java.io.*

/**
 * Created by 志朋 on 2017/12/30.
 * 讀取, 寫入檔案
 */
class MGFileUtils {

    companion object {

        var dirName: String = "MGFile"

        //預設路徑, fileDir/mgFile
        var defaultDir: File? = null

        //sd卡路徑
        var sdDir: File? = null

        //初始化物件, 只是為了得到預設的 路徑
        fun init() {
//            defaultDir = File(context.filesDir, dirName)
//            //如果MGFil資料夾尚未創建, 須先建立資料夾
//            defaultDir?.mkdirs()
//
//            sdDir = File(Environment.getExternalStorageDirectory(), dirName)
//            sdDir?.mkdirs()
        }

        //將字串寫入檔案, 回傳寫入是否成功, 寫入檔案只能在子現成呼叫
        fun write(text: String, name: String, path: File? = defaultDir, newThread: Boolean): Boolean {

            var success = false
            if (newThread) {
                val thread = MGThreadUtils.inSub {
                    success = writeFile(text, name, path)
                }
                thread.join() //等待寫入完成
            } else {
                writeFile(text, name, path)
                success = true
            }

            return success
        }

        //將字串讀出
        fun read(name: String, path: File? = defaultDir, newThread: Boolean): String? {
            var text: String? = null
            if (newThread) {
                val thread = MGThreadUtils.inSub {
                    text = readFile(name, path)
                }
                thread.join() //等待寫入完成
            } else {
                text = readFile(name, path)
            }
            return text
        }

        private fun writeFile(text: String, name: String, path: File?): Boolean {
            if (!isWriteConditionOK(path)) return false

            val outputFile = File(path, name)
            println("寫入檔案: ${outputFile.absolutePath}")

            try {
                val writer = FileWriter(outputFile)
                writer.write(text)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                println("寫入過程發生錯誤: ${e.message}")
            }
            return true
        }


        private fun readFile(name: String, path: File?): String? {
            if (!isWriteConditionOK(path)) return null

            val inputFile = File(path, name)
            println("讀取檔案: ${inputFile.absolutePath}")
            //先檢查路徑是否有檔案存在
            if (!inputFile.exists() || !inputFile.isFile) {
                println("路徑下無檔案存在: ${inputFile.absolutePath}")
                return null
            }

            val reader = FileReader(inputFile)
            val text = reader.readText()
            reader.close()
            return text
        }

        //檢查可否寫入
        private fun isWriteConditionOK(path: File?): Boolean {
            if (path == null) {
                //尚未初始化, 不能寫入檔案
                println("路徑null, 不能寫入檔案")
                return false
            }

            if (MGThreadUtils.isMain()) {
                //主線程, 不行寫入讀取, 退出
                println("主線程, 不能寫入檔案")
                return false
            }

            return true
        }

    }
}