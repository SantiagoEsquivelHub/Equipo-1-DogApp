package com.example.clase8.view.fragment

import android.graphics.Color
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.bumptech.glide.Glide
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clase8.databinding.FragmentNuevaCitaBinding
import com.example.clase8.model.Appointment
import com.example.clase8.viewmodel.AppointmentViewModel
import com.example.clase8.webservice.DogBreedApiRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NuevaCitaFragment : Fragment() {

    private lateinit var binding: FragmentNuevaCitaBinding
    private val appointmentViewModel: AppointmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNuevaCitaBinding.inflate(inflater)
        binding.lifecycleOwner = this
        // ✅ Asegura que el overlay siempre se vea por encima
        binding.loadingOverlay.bringToFront()
        binding.loadingOverlay.elevation = 100f
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadingOverlay.bringToFront()
        binding.loadingOverlay.elevation = 100f

        controladores()
        validarCampos()
        observerViewModel()
    }

    private fun controladores() {
        setupSintomasDropdown()
        setupRazaAutocomplete()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSaveAppointment.setOnClickListener {
            guardarCita()
        }
    }

    private fun guardarCita() {
        val petName = binding.etPetName.text.toString()
        val breed = binding.actBreed.text.toString().lowercase()
        val owner = binding.etOwnerName.text.toString()
        val phone = binding.etPhone.text.toString()
        val symptom = binding.actvSymptoms.text.toString()

        if (symptom == "Síntomas") {
            Toast.makeText(requireContext(), "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Mostrar overlay y spinner manualmente
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            var imageUrl = ""
            try {
                val response = withContext(Dispatchers.IO) {
                    DogBreedApiRetrofitClient.getDogApiService().getDogBreedImage(breed)
                }
                if (response.status == "success") {
                    imageUrl = response.message
                }
            } catch (e: Exception) {
                Log.e("guardarCita", "Error al obtener imagen", e)
            }

            val appointment = Appointment(
                petName = petName,
                breed = breed.replaceFirstChar { it.uppercaseChar() },
                imageUrl = imageUrl,
                ownerName = owner,
                phoneNumber = phone,
                symptom = symptom
            )

            appointmentViewModel.saveAppointment(appointment)
            Log.d("guardarCita", appointment.toString())

            delay(500) // ✅ Espera para mejor UX

            // ✅ Ocultar overlay y spinner
            binding.loadingOverlay.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            Toast.makeText(requireContext(), "Cita guardada", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }


    private fun validarCampos() {
        val campos = listOf(binding.etPetName, binding.actBreed, binding.etOwnerName, binding.etPhone)

        fun camposBasicosLlenos(): Boolean =
            campos.all { it.text?.isNotEmpty() == true }

        fun actualizarEstadoBoton() {
            val camposOk = camposBasicosLlenos()

            if (camposOk) {
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

        campos.forEach { it.addTextChangedListener { actualizarEstadoBoton() } }
        binding.actvSymptoms.setOnItemClickListener { _, _, _, _ -> actualizarEstadoBoton() }
    }

    private fun observerViewModel() {
        observerLastAppointment()
    }
    private fun observerLastAppointment() {
        appointmentViewModel.getListAppointment()
        appointmentViewModel.listAppointments.observe(viewLifecycleOwner) { lista ->
            if (lista.isNotEmpty()) {
                val cita = lista.last() // O usa lista[0] si quieres la primera
                Glide.with(binding.root.context)
                    .load(cita.imageUrl)
                    .into(binding.ivImagenApi) // Debes tener este ImageView en tu layout

                binding.tvTitleAppointment.text = cita.petName // Y este TextView también
            }
        }
    }


    private fun setupSintomasDropdown() {
        val sintomas = listOf(
            "Síntomas",
            "Solo duerme", "No come", "Fractura extremidad",
            "Tiene pulgas", "Tiene garrapatas", "Bota demasiado pelo"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sintomas)
        val autoComplete = binding.actvSymptoms as AutoCompleteTextView
        autoComplete.setAdapter(adapter)
        autoComplete.setText(sintomas[0], false)

        autoComplete.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                autoComplete.setText(sintomas[0], false)
                Toast.makeText(requireContext(), "Selecciona un síntoma válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRazaAutocomplete() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    DogBreedApiRetrofitClient.getDogApiService().getDogBreeds()
                }

                if (response.status == "success") {
                    val breeds = response.message.keys
                        .map { it.replaceFirstChar { char -> char.uppercaseChar() } }
                        .sorted()

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        breeds
                    )
                    binding.actBreed.setAdapter(adapter)
                } else {
                    Log.e("setupRazaAutocomplete", "Respuesta no exitosa: ${response.status}")
                }
            } catch (e: Exception) {
                Log.e("setupRazaAutocomplete", "Error al obtener razas", e)
            }
        }
    }
}
