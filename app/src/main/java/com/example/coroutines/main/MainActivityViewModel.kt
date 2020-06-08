package com.example.coroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coroutines.extensions.logCoroutine
import com.example.coroutines.main.data.Dog
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository) : ViewModel(), CoroutineScope {

    private val parentJob = SupervisorJob()
    private val _dogList = MutableLiveData<List<Dog>>()
    private val _snackbar = MutableLiveData<String?>()
    private val _status = MutableLiveData<String?>()
    private val _spinner = MutableLiveData(false)
    private val _topDogsAsync = MutableLiveData<List<Dog>>()

    init {
        loadTopTwoDogsAsync()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    val dogList: LiveData<List<Dog>>
        get() = _dogList
    val snackbar: LiveData<String?>
        get() = _snackbar
    val spinner: LiveData<Boolean>
        get() = _spinner
    val status: LiveData<String?>
        get() = _status
    val topDogsAsync: LiveData<List<Dog>>
        get() = _topDogsAsync

    fun onSnackbarShown() {
        _snackbar.value = null
    }

    private fun loadTopTwoDogsAsync() {
       viewModelScope.launch {
            logCoroutine("loadTopTwoDogsAsync", coroutineContext)
            val list = runCatching { mainActivityRepository.getTopTwoDogsAsync() }
            list.onSuccess {
                _topDogsAsync.value = it.value
            }.onFailure {
                _snackbar.value = it.message.toString()
                _snackbar.value = "loadTopTwoDogsAsync() " + it.message.toString()
            }
        }
    }

    fun loadDogListSynchronously() {
        parentJob.cancelChildren()
        _dogList.value = emptyList()
        launch {
            _status.value = "Loading..."
            val start = System.currentTimeMillis()
            val result = runCatching { mainActivityRepository.getListOfDogsSync() }
            result.onSuccess {
                _status.value = getTimeDifference(start)
                _dogList.value = it.value
            }.onFailure {
                _status.value = "Failed."
                _snackbar.value = "loadDogListSynchronously() " + it.message.toString()
            }
        }
    }

    fun loadDogListAsynchronously() {
        parentJob.cancelChildren()
        _dogList.value = emptyList()
        launch {
            _status.value = "Loading..."
            val start = System.currentTimeMillis()
            val result = runCatching { mainActivityRepository.getListOfDogsAsync() }
            result.onSuccess {
                _status.value = getTimeDifference(start)
                _dogList.value = it.value
            }.onFailure {
                _status.value = "Failed."
                _snackbar.value = "loadDogListAsynchronously() " + it.message.toString()
            }
        }
    }

    private fun getTimeDifference(start: Long): String {
        val difference = System.currentTimeMillis() - start
        val timeTaken = buildString {
            append(difference / 1000)
            append(" seconds")
        }
        return timeTaken.toString()
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancelChildren()
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
}



