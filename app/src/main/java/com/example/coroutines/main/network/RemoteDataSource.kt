package com.example.coroutines.main.network

import com.example.coroutines.main.data.Dog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val api: MainActivityApi) {

    suspend fun fetchRandomDog(): Dog = withContext(Dispatchers.IO) {
        val result = api.getRandomImageByUrl()
        val breedImageUrl = result.message
        extractBreedName(breedImageUrl)?.let { Dog(it, breedImageUrl) }!!
    }

    private fun extractBreedName(message: String): String? {
        val breedName = message.substringAfter("breeds/").substringBefore("/")
        return breedName.replace(Regex("-"), " ").capitalize()
    }

    suspend fun favoritesSortOrder(): List<String> = withContext(Dispatchers.IO) {

        delay(10000)
        listOf<String>(
            "Setter gordon", "Terrier patterdale", "Germanshepherd", "Ridgeback rhodesian", "Dachshund", "Pomeranian",
            "Pekinese", "Redbone", "Lhasa", "Chow", "Retriever curly", "Kelpie", "Terrier silky", "Spaniel welsh", "Otterhound"
        )
    }
}