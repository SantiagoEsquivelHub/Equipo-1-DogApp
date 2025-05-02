package com.example.clase8.repository

import android.content.Context

import com.example.clase8.webservice.DogBreedApiRetrofitClient
import com.example.clase8.webservice.DogBreedApiService

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DogBreedsRepository(val context: Context) {
    private var apiService: DogBreedApiService = DogBreedApiRetrofitClient.getDogApiService()

    suspend fun getBreedsList(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDogBreeds()
                if(response.status != "success"){
                    throw Exception("API response was not successful: ${response.status}")
                }
                response.message.keys.toList()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
        }
    }

    suspend fun getImageBreed(breed: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDogBreedImage(breed)
                if(response.status != "success"){
                    throw Exception("API response was not successful: ${response.status}")
                }
                response.message
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}