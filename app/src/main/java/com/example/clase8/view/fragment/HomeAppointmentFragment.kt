package com.example.clase8.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clase8.R
import com.example.clase8.databinding.FragmentHomeDogAppBinding
import com.example.clase8.viewmodel.AppointmentViewModel
import com.example.clase8.view.adapter.AppointmentAdapter

class HomeAppointmentFragment: Fragment() {
    private lateinit var binding: FragmentHomeDogAppBinding
    private val appointmentViewModel: AppointmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDogAppBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladores()
        observadorViewModel()
    }

    private fun controladores() {
        binding.fabNewAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_homeAppointmentFragment_to_nuevaCitaFragment)
        }
    }

    private fun observadorViewModel(){
         observerListInventory()
         observerProgress()
    }

    private fun observerListInventory(){

        appointmentViewModel.getListAppointment()
        appointmentViewModel.listAppointments.observe(viewLifecycleOwner) { listAppointment ->
            val recycler = binding.rvAppointments
            val layoutManager = LinearLayoutManager(context)
            recycler.layoutManager = layoutManager
            val adapter = AppointmentAdapter(listAppointment, findNavController())
            recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }

    private fun observerProgress(){
        appointmentViewModel.progresState.observe(viewLifecycleOwner){status ->
           binding.progress.isVisible = status
        }
    }
}