package com.saki.aidl

/**
 * Created by Hoshino Tented on 2017/12/3.
 */
enum class Type
{
    MSG, XML, JSON, AT,
    IMAGE
    {
        override fun toString(): String = "img"
    }
}