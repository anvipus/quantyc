package com.anvipus.library.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "message_action")
    val messageAction: String?,
    @Json(name = "message_data")
    val messageData: T?,
    @Json(name = "message_desc")
    val messageDesc: String?,
    @Json(name = "message_status")
    val messageStatus: String?,
    @Json(name = "message_status_code")
    val messageStatusCode: String?,
    @Json(name = "message_request_datetime")
    val message_request_datetime: String?,
    @Json(name = "message_id")
    val message_id: String?,
    @Json(name = "user_token")
    val userToken: String?,
    @Json(name = "message_description")
    val messageDescription: String?,
    @Json(name = "response_message")
    val response_message: String?,
    @Json(name = "subscriber_id")
    val subscriber_id: String?,
)