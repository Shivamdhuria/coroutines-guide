package com.example.coroutines.main

import androidx.lifecycle.*
import com.example.coroutines.main.data.Dog
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository) : ViewModel() {
    private companion object {
        private const val DELAY_BETWEEN_DOGS_IN_MS = 10000L
    }

    private val parentJob = SupervisorJob()
    private val _dogList = MutableLiveData<List<Dog>>()
    private val _snackbar = MutableLiveData<String?>()
    private val _status = MutableLiveData<String?>()
    private val _inputLiveData = MutableLiveData<Int>()
    private val _spinner = MutableLiveData(false)
    private val _topDogsAsync = MutableLiveData<List<Dog>>()
    private val atomicInteger = AtomicInteger()

    init {
//        loadTopTwoDogsAsync()
    }

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

    @ExperimentalCoroutinesApi
    val dogListLiveData = mainActivityRepository.episodesFlow.asLiveData()

//    fun loadMoreDogs(){
//        loadData { mainActivityRepository.tryUpdateDogCache() }
//    }


    val liveDateFetch = _inputLiveData.switchMap {
        liveData {
            emit(mainActivityRepository.tryFetchAndUpdate())
        }
    }

    private fun loadData(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                _snackbar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }


//    private fun loadTopTwoDogsAsync() {
//        viewModelScope.launch {
//            logCoroutine("loadTopTwoDogsAsync", coroutineContext)
//            val list = runCatching { mainActivityRepository.getTopTwoDogsAsync() }
//            list.onSuccess {
//                _topDogsAsync.value = it.value
//            }.onFailure {
//                _snackbar.value = it.message.toString()
//                _snackbar.value = "loadTopTwoDogsAsync() " + it.message.toString()
//            }
//        }
//    }


//    private fun getTopTwoDogsLiveData(): LiveData<GeneralResult> = liveData {
//        while (true) {
//            delay(DELAY_BETWEEN_DOGS_IN_MS)
//            val topTwoDogsResult = mainActivityRepository.getTopTwoDogsAsync()
//            emit(topTwoDogsResult)
//        }
//    }

//    private fun getTopTwoDogsLiveData(): LiveData<GeneralResult> = liveData {
//        while (true) {
//            delay(DELAY_BETWEEN_DOGS_IN_MS)
//            val topTwoDogsResult = mainActivityRepository.getTopTwoDogsAsync()
//            emit(topTwoDogsResult)
//        }
//    }


//    val liveDataResult = liveData(Dispatchers.IO) {
//        logCoroutineThreadNameOnly("liveDataResult")
//        emit(GeneralResult.Progress(true))
//        //This below function is a suspend function
//        val topTwoDogsResult = mainActivityRepository.getTopTwoDogsAsync()
//        emit(topTwoDogsResult)
//    }

//consumeing live data source

    @ExperimentalCoroutinesApi
//    val liveDataResult = _inputLiveData.switchMap {
//        mainActivityRepository
//            .getRandomDogImage()
//            .onStart { emit(GenericApiResponse.Loading()) }
//            .catch { emit(handleException(it)) }
//            .asLiveData()
//    }


//use this to update pic evevry 2 sec
//    val liveDataResult = _inputLiveData.switchMap {
////        mainActivityRepository
////            .getRandomDog()
////            .onStart { emit(ResultWrapper.Loading()) }
////            .map {
////                logCoroutineThreadNameOnly("live data result map")
////                delay(10000)
////                it
////            }
////            .flowOn(Dispatchers.IO)
////            .catch { emit(handleException(it)) }
////            .asLiveData()
////    }

//    fun loadDogListSynchronously() {
//        parentJob.cancelChildren()
//        _dogList.value = emptyList()
//        launch {
//            _status.value = "Loading..."
//            val start = System.currentTimeMillis()
//            val result = runCatching { mainActivityRepository.getListOfDogsSync() }
//            result.onSuccess {
//                _status.value = getTimeDifference(start)
//                _dogList.value = it.value
//            }.onFailure {
//                _status.value = "Failed."
//                _snackbar.value = "loadDogListSynchronously() " + it.message.toString()
//            }
//        }
//    }
//
//    fun loadDogListAsynchronously() {
//        parentJob.cancelChildren()
//        _dogList.value = emptyList()
//        launch {
//            _status.value = "Loading..."
//            val start = System.currentTimeMillis()
//            val result = runCatching { mainActivityRepository.getListOfDogsAsync() }
//            result.onSuccess {
//                _status.value = getTimeDifference(start)
//                _dogList.value = it.value
//            }.onFailure {
//                _status.value = "Failed."
//                _snackbar.value = "loadDogListAsynchronously() " + it.message.toString()
//            }
//        }
//    }

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

    fun fetchDogsFlow() {
        _inputLiveData.value = atomicInteger.incrementAndGet()
    }


}



