package com.example.coroutines.main.data

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: Map<String,List<String>>
)