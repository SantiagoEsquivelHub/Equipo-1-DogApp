package com.example.clase8.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.clase8.R
import com.example.clase8.databinding.FragmentAppointmentDetailBinding
import com.example.clase8.model.Appointment
import com.example.clase8.viewmodel.AppointmentViewModel

class AppointmentDetailFragment : Fragment() {

    private lateinit var binding: FragmentAppointmentDetailBinding
    private val appointmentViewModel: AppointmentViewModel by viewModels()
    private lateinit var receivedAppointment: Appointment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarDatosCita()
        configurarControladores()
    }

    private fun configurarControladores() {
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar cita?")
                .setMessage("¿Deseas eliminar esta cita definitivamente?")
                .setPositiveButton("Sí") { _, _ ->
                    eliminarCita()
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.btnEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("clave", receivedAppointment)
            findNavController().navigate(R.id.action_appoinmentDetailsFragment_to_editarCitarFragment, bundle)
        }
    }

    @Suppress("DEPRECATION")
    private fun cargarDatosCita() {
        val receivedBundle = arguments
        receivedAppointment = receivedBundle?.getSerializable("clave") as Appointment

        binding.tvPetName.text = receivedAppointment.petName
        binding.tvOwnerName.text = getString(R.string.owner_label, receivedAppointment.ownerName)
        binding.tvPhone.text = getString(R.string.phone_label, receivedAppointment.phoneNumber)
        binding.tvBreed.text = receivedAppointment.breed
        binding.tvSymptom.text = receivedAppointment.symptom

        Glide.with(requireContext())
            .load(receivedAppointment.imageUrl)
            .into(binding.ivPet)
    }

    private fun eliminarCita() {
        appointmentViewModel.deleteAppointment(receivedAppointment)
        appointmentViewModel.getListAppointment()
        findNavController().popBackStack()
    }
}
