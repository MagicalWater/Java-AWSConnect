package com.mgwater.mgbaseproject.util

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by 志朋 on 2017/12/9
 * 現在線程的使用很方便, 因此此處只是封裝了多線程併發等待的情形.
 */
class MGThreadUtils {

    companion object {

        //轉子線程, 雖然kotlin很方便了, 但還是習慣在這統一控制
        fun inSub(handler: () -> Unit): Thread {
            val t = Thread { handler() }
            t.start()
            return t
        }


        //多線程併發, 傳入線程數量
        //handler回傳開始執行的線程
        fun inSubMultiple(count: Int, handler: (Int) -> Unit) {

            val threads: MutableList<Thread> = mutableListOf()

            for (i in 0 until count) {
                println("顯示")
                println("顯示2 i = $i")
                val thread =  Thread { handler(i) }
                threads.add(thread)
                thread.start()
            }

            for (thread in threads) {
                thread.join()
            }

        }

        fun delayResponse(delay: Long, handler: () -> Unit) {
            doAsync {
                Thread.sleep(delay)
                uiThread { handler() }
            }
        }


        //檢查是否為主線程
        fun isMain(): Boolean {
            return true
//            return Thread.currentThread() == Looper.getMainLooper().thread
        }
    }

}