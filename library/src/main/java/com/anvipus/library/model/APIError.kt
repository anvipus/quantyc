package com.anvipus.library.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class APIError (
    @Json(name = "message")
    val message: String?
)