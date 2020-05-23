package com.example.coroutines.main

import com.example.coroutines.main.data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET

internal interface MainActivityApi {

    @GET("list/all")
    fun getAllBreeds(): Call<ApiResponse>
}