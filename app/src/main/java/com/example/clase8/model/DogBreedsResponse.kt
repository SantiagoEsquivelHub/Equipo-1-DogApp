package com.example.clase8.model

import com.google.gson.annotations.SerializedName

class DogBreedsResponse (

    @SerializedName("message")
    val message: Map<String, List<String>>,

    @SerializedName("status")
    val status:String
)