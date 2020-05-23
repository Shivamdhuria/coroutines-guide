package com.example.coroutines.main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository) : ViewModel() {


    private val _input = MutableLiveData<Int>()
    private val atomicInteger = AtomicInteger()

    internal val status by lazy { Transformations.switchMap(_input) { mainActivityRepository.getAllBreeds() } }

    fun fetchBreeds() {
        _input.value = atomicInteger.incrementAndGet()
    }

}