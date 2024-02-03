package com.anvipus.library.model

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val msg: String?
) {

    companion object {
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data,null)
        }
        fun <T> success(data: T?,): Resource<T> {
            return Resource(Status.SUCCESS, data,null)
        }
        fun <T> error(error: String): Resource<T> {
            return Resource(Status.ERROR, null,error)
        }
        fun <T> timeout(msg: String? = null): Resource<T> {
            return Resource(Status.TIMEOUT, null, msg)
        }
    }
}

enum class Status{
    LOADING,
    SUCCESS,
    ERROR,
    TIMEOUT
}