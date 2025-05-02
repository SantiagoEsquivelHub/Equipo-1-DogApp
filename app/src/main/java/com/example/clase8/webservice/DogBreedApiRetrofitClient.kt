package com.example.clase8.webservice

import com.example.clase8.utils.Constants.DOG_API
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DogBreedApiRetrofitClient {
    fun getDogApiService(): DogBreedApiService {
        return Retrofit.Builder()
            .baseUrl(DOG_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogBreedApiService::class.java)
    }
}