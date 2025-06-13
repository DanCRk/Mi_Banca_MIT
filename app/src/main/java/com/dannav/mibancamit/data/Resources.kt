package com.dannav.mibancamit.data

import java.lang.Exception

sealed class Resource<out R> {
    data class Success<out R>(val result:R, val message:String): Resource<R>()
    data class Failure(val exception: Exception, val message:String): Resource<Nothing>()
    object Loading : Resource<Nothing>()
}