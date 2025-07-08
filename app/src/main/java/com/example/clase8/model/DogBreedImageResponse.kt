package com.example.clase8.model

import com.google.gson.annotations.SerializedName

class DogBreedImageResponse (

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: String
)