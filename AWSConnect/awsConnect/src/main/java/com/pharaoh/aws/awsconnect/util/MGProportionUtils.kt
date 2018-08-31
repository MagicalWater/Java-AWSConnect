package com.mgwater.mgbaseproject.util

/**
 * Created by magicalwater on 2018/1/24.
 */
class MGProportionUtils {

    companion object {
        //得到相同比例的高
        fun getHeight(oriW: Double, oriH: Double, newW: Double): Double {
            val wScale = newW / oriW
            val h = oriH * wScale
            return h
        }

        //得到相同比例的寬
        fun getWidth(oriW: Double, oriH: Double, newH: Double): Double {
            val hScale = newH / oriH
            val w = oriW * hScale
            return w
        }
    }

}