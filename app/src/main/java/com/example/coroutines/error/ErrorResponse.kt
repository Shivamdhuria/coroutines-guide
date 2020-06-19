package com.example.coroutines.error

data class ErrorResponse(val code: Int,
                         val message: String,
                         val data: Any)