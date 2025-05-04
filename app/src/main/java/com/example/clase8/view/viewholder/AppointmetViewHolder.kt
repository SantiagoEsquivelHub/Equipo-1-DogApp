package com.example.clase8.view.viewholder

import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clase8.R
import com.example.clase8.databinding.ItemDogBinding
import com.example.clase8.model.Appointment


class AppointmetViewHolder(biding: ItemDogBinding, navController: NavController) :RecyclerView.ViewHolder(biding.root) {
    val bindingItem = biding
    val navController = navController
    fun setItemAppointment(appointment: Appointment) {
        bindingItem.tvId.text = appointment.id.toString()
//      bindingItem.ivMascota.setImageResource(appointment.imageUrl)
        bindingItem.tvPetName.text = appointment.petName
        bindingItem.tvSymptom.text = appointment.symptom

        bindingItem.cvAppointment.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("clave", appointment)
            navController.navigate(R.id.action_homeInventoryFragment_to_itemDetailsFragment, bundle)
        }
    }
}