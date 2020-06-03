package com.example.coroutines.main

import com.example.coroutines.main.data.Dog
import com.example.coroutines.main.data.Result

interface MainActivityRepository {

    suspend fun getListOfDogsAsync(): Result<List<Dog>>

    suspend fun getListOfDogsSync(): Result<List<Dog>>

    suspend fun getTopTwoDogsAsync(): Result<List<Dog>>

}