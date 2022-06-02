package com.anvipus.library.api

import com.squareup.moshi.Moshi

abstract class Mappable {

    open fun toMap(): Map<String, String> {
        val moshi = Moshi.Builder().build()

        val adapter = moshi.adapter(Any::class.java)

        val jsonStructure = adapter.toJsonValue(this)
        return jsonStructure as Map<String, String>
    }
}