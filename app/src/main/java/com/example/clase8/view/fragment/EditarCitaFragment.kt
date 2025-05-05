package com.example.clase8.view.fragment

import com.example.clase8.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clase8.databinding.FragmentEditarCitaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Color
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.Log
import com.example.clase8.webservice.DogBreedApiRetrofitClient

class EditarCitaFragment : Fragment() {

    private var _binding: FragmentEditarCitaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarCitaBinding.inflate(inflater, container, false)
        // Asegurar overlay al frente
        binding.loadingOverlay.bringToFront()
        binding.loadingOverlay.elevation = 100f
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRazaAutocomplete()
        setupValidaciones()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRazaAutocomplete() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    DogBreedApiRetrofitClient.getDogApiService().getDogBreeds()
                }

                if (response.status != "success") {
                    Log.e("setupRazaAutocomplete", "Respuesta no exitosa: ${response.status}")
                    return@launch
                }

                val breeds = response.message.keys
                    .map { it.replaceFirstChar { char -> char.uppercaseChar() } }
                    .sorted()

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    breeds
                )
                binding.actBreed.setAdapter(adapter)

            } catch (e: Exception) {
                Log.e("setupRazaAutocomplete", "Excepción al obtener las razas", e)
            }
        }
    }




    private fun setupValidaciones() {
         // Campos obligatorios (sin síntomas)
         val campos = listOf(
             binding.etPetName,
             binding.actBreed,
             binding.etOwnerName,
             binding.etPhone
         )


        fun validarCamposBasicos(): Boolean {
            return campos.all { it.text?.isNotBlank() == true }
        }

        fun actualizarEstadoBoton() {
            val camposLlenos = validarCamposBasicos()
            if (camposLlenos) {
                binding.btnSaveAppointment.setTextColor(Color.WHITE)
                binding.btnSaveAppointment.setTypeface(null, Typeface.BOLD)
                binding.btnSaveAppointment.iconTint = ColorStateList.valueOf(Color.WHITE)
                binding.btnSaveAppointment.isEnabled = true
            } else {
                binding.btnSaveAppointment.setTextColor(Color.DKGRAY)
                binding.btnSaveAppointment.setTypeface(null, Typeface.NORMAL)
                binding.btnSaveAppointment.iconTint = ColorStateList.valueOf(Color.DKGRAY)
                binding.btnSaveAppointment.isEnabled = false
            }
        }

        campos.forEach { campo ->
            campo.addTextChangedListener { actualizarEstadoBoton() }
        }

        // Listener de botón Guardar
        binding.btnSaveAppointment.setOnClickListener {
            if (!validarCamposBasicos()) {
                Toast.makeText(
                    requireContext(),
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Mostrar overlay + spinner
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSaveAppointment.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                // Ocultar overlay
                withContext(Dispatchers.Main) {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
                // Volver a home
                Toast.makeText(requireContext(), "Cita Editada correctamente", Toast.LENGTH_SHORT).show()

                if (isAdded) {
                    findNavController().popBackStack(
                        R.id.homeAppointmentFragment,
                        false
                    )
                }
            }
        }
    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}

