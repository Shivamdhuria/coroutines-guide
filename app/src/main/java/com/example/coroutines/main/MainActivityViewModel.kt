package com.example.coroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coroutines.extensions.logCoroutine
import com.example.coroutines.main.data.Dog
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository) : ViewModel() {

    val _dogList = MutableLiveData<List<Dog>>()
    val dogList: LiveData<List<Dog>>
        get() = _dogList


    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _snackbar = MutableLiveData<String?>()

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    val _topDogsAsync = MutableLiveData<List<Dog>>()
    val topDogsAsync: LiveData<List<Dog>>
        get() = _topDogsAsync

    fun onSnackbarShown() {
        _snackbar.value = null
    }

    private fun loadData(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                //show error
            } finally {
                _spinner.value = false
            }
        }
    }

    fun loadTopTwoDogsAsync() {
        viewModelScope.launch {
            logCoroutine("loadTopTwoDogsAsync", coroutineContext)
            val list = runCatching { mainActivityRepository.getTopTwoDogsAsync() }
            list.onSuccess {
                _topDogsAsync.value = it.value
            }.onFailure {
                _snackbar.value = it.message.toString()
            }
        }
    }

    fun loadDogListSynchronously() {
        viewModelScope.launch {
            val result = runCatching { mainActivityRepository.getListOfDogs() }
            result.onSuccess {
                _dogList.value = it.value
            }.onFailure {
                _snackbar.value = it.message.toString()
            }
        }
    }
}

/**
 * Executes the given [block] and returns elapsed time in milliseconds.
 */
public inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}
