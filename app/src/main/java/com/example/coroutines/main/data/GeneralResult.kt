package com.example.coroutines.main.data

sealed class GeneralResult {

    internal data class Progress(val loading: Boolean = true) : GeneralResult()

    internal data class Error(val error: String) : GeneralResult()

    internal data class Success<T>(val data: T) : GeneralResult()
}