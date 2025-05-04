package com.example.clase8.view.fragment


import com.example.clase8.R            // <- este import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clase8.databinding.FragmentNuevaCitaBinding
import com.example.clase8.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Color
import android.content.res.ColorStateList
import android.graphics.Typeface
import kotlinx.coroutines.delay



class NuevaCitaFragment : Fragment() {

    private lateinit var binding: FragmentNuevaCitaBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNuevaCitaBinding.inflate(inflater, container, false)
        binding.loadingOverlay.bringToFront()
        binding.loadingOverlay.elevation = 100f
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSintomasDropdown()
        setupRazaAutocomplete()
        setupValidaciones()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setupSintomasDropdown() {
        val sintomas = listOf(
            "Síntomas", // Placeholder (no seleccionable)
            "Solo duerme",
            "No come",
            "Fractura extremidad",
            "Tiene pulgas",
            "Tiene garrapatas",
            "Bota demasiado pelo"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sintomas)
        val autoComplete = binding.actvSymptoms as AutoCompleteTextView
        autoComplete.setAdapter(adapter)
        autoComplete.setText(sintomas[0], false) // Mostrar "Síntomas" al inicio

        autoComplete.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                // Evitar seleccionar el placeholder
                autoComplete.setText(sintomas[0], false)
                Toast.makeText(requireContext(), "Selecciona un síntoma válido", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupRazaAutocomplete() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getBreeds()
                if (response.isSuccessful) {
                    val breeds = response.body()?.message?.keys?.map { it.replaceFirstChar(Char::titlecase) } ?: emptyList()
                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
                        binding.actBreed.setAdapter(adapter)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupValidaciones() {
        val campos = listOf(
            binding.etPetName,
            binding.actBreed,
            binding.etOwnerName,
            binding.etPhone
        )

        fun validarCamposBasicos(): Boolean {
            return campos.all { it.text?.isNotEmpty() == true }
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

        binding.actvSymptoms.setOnItemClickListener { _, _, _, _ ->
            actualizarEstadoBoton()
        }

        binding.btnSaveAppointment.setOnClickListener {
            val camposLlenos = validarCamposBasicos()
            val sintoma = binding.actvSymptoms.text.toString()

            if (!camposLlenos || sintoma == "Síntomas") {
                Toast.makeText(requireContext(), "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar overlay y progress
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility    = View.VISIBLE

            viewLifecycleOwner.lifecycleScope.launch {
                // Simular operación larga
                delay(2000)

                // Ocultar siempre antes de navegar
                binding.loadingOverlay.visibility = View.GONE
                binding.progressBar.visibility    = View.GONE

                // Navegar al Home solo si aún estamos añadidos
                if (isAdded) {
                    findNavController().navigate(
                        R.id.homeInventoryFragment
                    )
                }
            }
        }





    }

}
