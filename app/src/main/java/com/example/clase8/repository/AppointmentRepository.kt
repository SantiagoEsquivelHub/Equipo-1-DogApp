package com.example.clase8.repository

import android.content.Context
import com.example.clase8.data.AppointmentsDB
import com.example.clase8.data.AppointmentsDao
import com.example.clase8.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository(val context: Context){
    private var appointmentsDao:AppointmentsDao = AppointmentsDB.getDatabase(context).appointmentsDao()

    suspend fun saveAppointment(appointment:Appointment){
        withContext(Dispatchers.IO){
            appointmentsDao.insert(appointment)
        }
    }

    suspend fun getListAppointment():MutableList<Appointment>{
        return withContext(Dispatchers.IO){
            appointmentsDao.getAllAppointments()
        }
    }

    suspend fun deleteAppointment(appointment: Appointment){
        withContext(Dispatchers.IO){
            appointmentsDao.delete(appointment)
        }
    }

    suspend fun updateAppointment(appointment: Appointment){
        withContext(Dispatchers.IO){
            appointmentsDao.update(appointment)
        }
    }
}