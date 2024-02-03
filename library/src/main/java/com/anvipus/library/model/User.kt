package com.anvipus.library.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class User (
    @Json(name = "username")
    val username: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "password")
    val password: String?,
    @Json(name = "isAdmin")
    val isAdmin: Boolean = false
) : Parcelable