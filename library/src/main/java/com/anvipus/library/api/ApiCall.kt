package com.anvipus.library.api

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.anvipus.library.model.*
import com.google.gson.Gson

abstract class ApiCall<ResultType, RequestType>() {

    private val result = MediatorLiveData<Resource<ResultType>>()

    companion object {
        const val TAG: String = "ApiCall"
    }

    init {

        result.value = Resource.loading(null)
        networkCall()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.postValue(newValue)
        }
    }

    private fun networkCall() {

        val call = createCall()

        val apiCall = GlobalScope.async {
            try {

                return@async call.execute()
            } catch (io: IOException) {
                Timber.e("networkCall")
                Timber.e(io)
                null
            }
        }

        GlobalScope.launch(Dispatchers.Main) {

            try {
                val apiResponse = apiCall.await()
                val apiBody = apiResponse?.body() as RequestType
                val errorBody = apiResponse?.errorBody()
                var apiError: APIError? = null
                if(apiResponse != null && apiResponse.isSuccessful.not()){
                    apiError= Gson().fromJson(
                        errorBody!!.charStream(),
                        APIError::class.java
                    )
                }

                if (apiResponse != null && apiResponse.isSuccessful) {
                    val data = processResponse(apiBody)
                    setValue(Resource.success(data))
                }else if(apiResponse == null) {
                    setValue(Resource.timeout("Terdapat gangguan koneksi, mohon periksa koneksi dan mencoba kembali"))
                }
                else setValue(
                    Resource.error(apiError?.message
                        ?: "Terdapat gangguan koneksi, mohon periksa koneksi dan mencoba kembali")
                )
            }catch (e:Exception){
                setValue(
                    Resource.error(
                        "Permintaan tidak bisa diproses. Mohon tunggu beberapa waktu sebelum mencoba lagi. Jika Masih mengalami kendala hubungi CS 1500899"
                    ))
            }


        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @Suppress("UNCHECKED_CAST")
    @WorkerThread
    protected open fun processResponse(body: RequestType) = body as ResultType?

    @WorkerThread
    protected abstract fun createCall(): Call<RequestType>
}