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

    private val context = getApplication<Application>()
    private val appointmentRepository = AppointmentRepository(context) // 👈 usa minúscula (convención)

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    private val _listAppointments = MutableLiveData<MutableList<Appointment>>()
    val listAppointments: LiveData<MutableList<Appointment>> get() = _listAppointments

    fun saveAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                appointmentRepository.saveAppointment(appointment)
                _listAppointments.value = appointmentRepository.getListAppointment() // 👈 actualiza después de guardar
            } catch (e: Exception) {
                // Log error si quieres
            } finally {
                _progresState.value = false
            }
        }
    }

    fun getListAppointment() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listAppointments.value = appointmentRepository.getListAppointment()
            } catch (e: Exception) {
                // Log error si quieres
            } finally {
                _progresState.value = false
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                appointmentRepository.updateAppointment(appointment)
                _listAppointments.value = appointmentRepository.getListAppointment()
            } catch (e: Exception) {
                // Log error si lo deseas
            } finally {
                _progresState.value = false
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                appointmentRepository.deleteAppointment(appointment)
                _listAppointments.value = appointmentRepository.getListAppointment()
            } catch (e: Exception) {
                // Log error si lo deseas
            } finally {
                _progresState.value = false
            }
        }
    }
}
