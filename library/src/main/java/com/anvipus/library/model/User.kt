package com.anvipus.library.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class User (
    @Json(name = "id")
    val id: Int?,
    @Json(name = "username")
    val username: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "password")
    val password: String?,
    @Json(name = "isAdmin")
    val isAdmin: Boolean = false
) : Parcelable{

    val viewId get() = "ID : "+id
    val viewUsername get() = "Username : "+username
    val viewEmail get() = "Email : "+email

    val viewAdmin get() = "isAdmin : "+isAdmin
}

@JsonClass(generateAdapter = true)
@Parcelize
data class Album (
    @Json(name = "albumId")
    val albumId: Int?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "thumbnailUrl")
    val thumbnailUrl: String?
) : Parcelable{
    val viewId get() = "ID : "+id
    val viewTitle get() = "Title : "+title
}