package com.example.coroutines.main

import androidx.lifecycle.LiveData
import com.example.coroutines.main.data.GeneralResult


interface MainActivityRepository {

    fun getAllBreeds(): LiveData<GeneralResult>
}