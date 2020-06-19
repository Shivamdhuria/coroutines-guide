package com.example.coroutines.main

import com.example.coroutines.error.ResultWrapper
import com.example.coroutines.extensions.safeApiCall
import com.example.coroutines.main.data.ApiResponse
import com.example.coroutines.main.data.Dog
import com.example.coroutines.main.data.DogDao
import com.example.coroutines.main.network.MainActivityApi
import com.example.coroutines.main.network.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MainActivityRepository @Inject constructor(
    private val dogDao: DogDao,
    private val dogsRDS: RemoteDataSource,
    private val api: MainActivityApi
) {

    val episodesFlow: Flow<List<Dog>>
        get() = dogDao.loadAllEpisodesFlow()
            .combine(topBreedsFlow) { dogs, topDogs ->
                dogs.applyToDog(topDogs)
            }
            .flowOn(Dispatchers.Default)
            .conflate()

    suspend fun tryUpdateDogCache() {
        val dogApiResponse = dogsRDS.fetchRandomDog()
        dogDao.save(dogApiResponse)
    }

    private val topBreedsFlow = flow {

        val topBreedsList = dogsRDS.favoritesSortOrder()
        emit(topBreedsList)
    }


    suspend fun tryFetchAndUpdate(): ResultWrapper {

        val api = safeApiCall(Dispatchers.IO) { api.getRandomImageByUrl() }
        when (api) {
            is ResultWrapper.Success<*> -> {
                val dogResponse = api.value as ApiResponse<String>
                val breedImageUrl = dogResponse.message
                val dog = extractBreedName(breedImageUrl)?.let { Dog(it, breedImageUrl) }
                dog?.run { dogDao.save(this) }
            }
        }
        return api
    }

    private fun extractBreedName(message: String): String? {
        val breedName = message.substringAfter("breeds/").substringBefore("/")
        return breedName.replace(Regex("-"), " ").capitalize()
    }


    private fun List<Dog>.applyToDog(favoritesSortOrder: List<String>): List<Dog> {
        return this.map {
            val isTopDog = favoritesSortOrder.contains(it.breed)
            Dog(it.breed, it.imageUrl, isTopDog)
        }

    }
}
