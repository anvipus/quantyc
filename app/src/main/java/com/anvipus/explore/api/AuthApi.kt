package com.anvipus.explore.api

import com.anvipus.library.model.Album
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface AuthApi {
    @GET("photos")
    fun getListAlbum(): Call<List<Album>>
}