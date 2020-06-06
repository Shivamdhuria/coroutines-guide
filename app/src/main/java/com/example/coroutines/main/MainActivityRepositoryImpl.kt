package com.example.coroutines.main

import com.example.coroutines.extensions.asyncAll
import com.example.coroutines.extensions.logCoroutine
import com.example.coroutines.main.data.Dog
import com.example.coroutines.main.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MainActivityRepositoryImpl @Inject constructor(private val api: MainActivityApi) : MainActivityRepository {

    override suspend fun getTopTwoDogsAsync(): Result<List<Dog>> = withContext(Dispatchers.IO) {
        //We need to move to background thread, "Dispatchers.IO" in this case as Network requests must always operate on
        // background thread.
        val list = mutableListOf<Dog>()
        logCoroutine("getTopTwoDogsAsync", coroutineContext)

        //The async{} builder immediately spawns the Coroutine inside the blocks.
        val dogBreedListDeferred = async { api.getBreedsListAsync().execute() }
        //The .await() pauses the function until the deferred val returns a result.
        val dogBreedListResponse = dogBreedListDeferred.await()

        //Selecting two dog breeds by Random
        val dogBreedOneName = dogBreedListResponse.body()?.message?.keys?.toList()?.random()
        val dogBreedTwoName = dogBreedListResponse.body()?.message?.keys?.toList()?.random()

        //Spawning two Coroutines by using async{} again.
        val dogBreedOneImageDeferred = async {
            logCoroutine("dogBreedOneImageDeferred", coroutineContext)
            dogBreedOneName?.let { api.getImageByUrlAsync(it).execute() }
        }
        val dogBreedTwoImageDeferred = async {
            logCoroutine("dogBreedTwoImageDeferred", coroutineContext)
            dogBreedTwoName?.let { api.getImageByUrlAsync(it).execute() }
        }

        //Await for both the started coroutines above by using await on the deferred val .
        val dogBreedOne = dogBreedOneImageDeferred.await()
        val dogBreedTwo = dogBreedTwoImageDeferred.await()

        if (dogBreedTwo?.isSuccessful!!) list.add(Dog(dogBreedTwoName, dogBreedTwo.body()?.message))
        if (dogBreedOne?.isSuccessful!!) list.add(Dog(dogBreedOneName, dogBreedOne.body()?.message))
        Result(list, null)
    }

    override suspend fun getListOfDogsSync(): Result<List<Dog>> {
        //No need to change context to Dispatchers.IO as Retrofit handles that automatically.
        val list = mutableListOf<Dog>()
        //Remove async{} and .await()
        val dogBreedList = api.getBreedsList().message.keys.toList()
        //This function is paused until the above api returns results.
        dogBreedList.forEach {
            val dogImage = api.getImageByUrl(it).message
            list.add(Dog(it, dogImage))
        }
        return Result(list, null)
    }

    override suspend fun getListOfDogsAsync(): Result<List<Dog>> {
        //No need to change context to Dispatchers.IO as Retrofit handles that automatically.
        val list = mutableListOf<Dog>()
        //Remove async{} and .await()
        val dogBreedList = api.getBreedsList().message.keys.toList()
        //This function is paused until the above api returns results.

        withContext(Dispatchers.IO) {

            dogBreedList.map { async { api.getImageByUrl(it) } }.awaitAll().forEach {
                list.add(Dog(extractBreedName(it.message), it.message))
            }

            //Using extension function for nicer code
//            asyncAll(dogBreedList) { api.getImageByUrl(it) }.awaitAll().forEach { list.add(Dog(extractBreedName(it.message), it.message)) }
        }
        return Result(list, null)
    }

    private fun extractBreedName(message: String): String? {
        val breedName = message.substringAfter("breeds/").substringBefore("/")
        return breedName.replace(Regex("-"), " ").capitalize()
    }
}