package com.example.coroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coroutines.main.data.ApiResponse
import com.example.coroutines.main.data.GeneralResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

internal class MainActivityRemoteRepository @Inject constructor(private val api: MainActivityApi) :
    MainActivityRepository {

    private val resultLiveData = MutableLiveData<GeneralResult>()

    override fun getAllBreeds(): LiveData<GeneralResult> {
        resultLiveData.value = GeneralResult.Progress(true)

        api.getAllBreeds().enqueue(object : Callback<ApiResponse> {
            override fun onFailure(call: Call<ApiResponse>?, t: Throwable?) {
                resultLiveData.value = GeneralResult.Error(t?.message.toString())
            }

            override fun onResponse(
                call: Call<ApiResponse>?,
                response: Response<ApiResponse>?
            ) {
                resultLiveData.value = GeneralResult.Success(response?.body()?.message)
            }
        })
        return resultLiveData
    }
}