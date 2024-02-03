package com.anvipus.explore.repo

import androidx.lifecycle.LiveData
import com.anvipus.explore.api.AuthApi
import com.anvipus.library.api.ApiCall
import com.anvipus.library.model.Album
import retrofit2.Call
import com.anvipus.library.model.Resource
import javax.inject.Inject

class MainRepo @Inject constructor(
    private val api: AuthApi
){
    fun getListUsers(): LiveData<Resource<List<Album>>> = object : ApiCall<List<Album>, List<Album>>(){
        override fun createCall(): Call<List<Album>> {
            return api.getListAlbum()
        }
    }.asLiveData()
}