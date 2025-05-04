package com.example.clase8.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clase8.databinding.ItemDogBinding
import com.example.clase8.model.Appointment
import com.example.clase8.view.viewholder.AppointmetViewHolder


class AppointmentAdapter(private val listAppointment: MutableList<Appointment>, private val navController: NavController): RecyclerView.Adapter<AppointmetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmetViewHolder {
        val binding = ItemDogBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return AppointmetViewHolder(binding, navController)
    }

    override fun getItemCount(): Int {
        return listAppointment.size
    }

    override fun onBindViewHolder(holder: AppointmetViewHolder, position: Int) {
        val appointment = listAppointment[position]
        holder.setItemAppointment(appointment)
    }
}