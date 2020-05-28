package com.example.coroutines.main

import com.example.coroutines.main.data.Dog
import com.example.coroutines.main.data.Result

interface MainActivityRepository {

    suspend fun getListOfDogs(): Result<List<Dog>>

    suspend fun getTopTwoDogsAsync(): Result<List<Dog>>

}