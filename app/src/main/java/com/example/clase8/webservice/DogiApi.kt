package com.example.clase8.network

import retrofit2.Response
import retrofit2.http.GET

data class DogBreedsResponse(
    val message: Map<String, List<String>>,
    val status: String
)

interface DogApi {
    @GET("breeds/list/all")
    suspend fun getBreeds(): Response<DogBreedsResponse>
}
