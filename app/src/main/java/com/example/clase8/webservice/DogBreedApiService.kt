package com.example.clase8.webservice
import retrofit2.Response

import com.example.clase8.model.DogBreedsResponse
import com.example.clase8.model.DogBreedImageResponse

import com.example.clase8.utils.Constants.GET_DOG_BREEDS
import com.example.clase8.utils.Constants.GET_DOG_IMAGE_BREED

import retrofit2.http.GET
import retrofit2.http.Path

interface DogBreedApiService {
    @GET(GET_DOG_BREEDS)
    suspend fun getDogBreeds(): DogBreedsResponse

    @GET(GET_DOG_IMAGE_BREED)
    suspend fun getDogBreedImage(
        @Path("breed") breed: String
    ): DogBreedImageResponse
}