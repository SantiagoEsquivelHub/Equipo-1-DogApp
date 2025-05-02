package com.example.clase8.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val petName: String,
    val breed: String,
    val imageUrl: String,
    val ownerName: String,
    val phoneNumber: String,
    val symptom: String
): Serializable
