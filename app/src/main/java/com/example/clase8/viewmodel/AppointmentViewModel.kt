package com.example.clase8.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clase8.model.Appointment
import com.example.clase8.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    val context = getApplication<Application>()
    private val AppointmentRepository = AppointmentRepository(context)

    private val _listAppointments = MutableLiveData<MutableList<Appointment>>()
    val listAppointments: LiveData<MutableList<Appointment>> get() = _listAppointments

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    fun getListAppointment() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listAppointments.value = AppointmentRepository.getListAppointment()
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }

}

