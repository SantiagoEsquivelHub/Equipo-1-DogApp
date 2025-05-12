    package com.example.clase8.view.fragment
    import android.content.res.ColorStateList
    import androidx.core.widget.doAfterTextChanged
    import android.graphics.Color
    import android.os.Bundle
    import android.util.Log
    import android.view.*
    import android.widget.ArrayAdapter
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.viewModels
    import androidx.navigation.fragment.findNavController
    import com.example.clase8.R
    import com.example.clase8.databinding.FragmentEditarCitaBinding
    import com.example.clase8.model.Appointment
    import com.example.clase8.viewmodel.AppointmentViewModel
    import com.example.clase8.webservice.DogBreedApiRetrofitClient
    import kotlinx.coroutines.*
    import androidx.lifecycle.lifecycleScope

    class EditarCitaFragment : Fragment() {

        private lateinit var binding: FragmentEditarCitaBinding
        private val appointmentViewModel: AppointmentViewModel by viewModels()
        private lateinit var receivedAppointment: Appointment

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentEditarCitaBinding.inflate(inflater, container, false)
            binding.lifecycleOwner = viewLifecycleOwner
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            dataAppointment()
            setupRazaAutocomplete()
            controladores()
            setupValidaciones()
        }

        private fun dataAppointment() {
            val receivedBundle = arguments
            receivedAppointment = receivedBundle?.getSerializable("clave") as Appointment

            binding.etPetName.setText(receivedAppointment.petName)
            binding.actBreed.setText(receivedAppointment.breed)
            binding.etOwnerName.setText(receivedAppointment.ownerName)
            binding.etPhone.setText(receivedAppointment.phoneNumber)
        }

        private fun controladores() {
            binding.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            binding.btnEditAppointment.setOnClickListener {
                if (!validarCampos()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                binding.loadingOverlay.visibility = View.VISIBLE
                binding.progressBar.visibility = View.VISIBLE
                binding.btnEditAppointment.isEnabled = false

                mostrarDialogoConfirmacionEdicion()
            }
        }
        private fun mostrarDialogoConfirmacionEdicion() {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmar cambios")
            builder.setMessage("¿Deseas guardar los cambios realizados en la cita?")

            builder.setPositiveButton("Sí") { dialog, _ ->
                updateAppointment()
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(resources.getColor(android.R.color.holo_green_dark))

            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }

        private fun updateAppointment() {
            val petName = binding.etPetName.text.toString()
            val breed = binding.actBreed.text.toString().lowercase()
            val owner = binding.etOwnerName.text.toString()
            val phone = binding.etPhone.text.toString()

            lifecycleScope.launch {
                var imageUrl = receivedAppointment.imageUrl // valor por defecto
                try {
                    val response = withContext(Dispatchers.IO) {
                        DogBreedApiRetrofitClient.getDogApiService().getDogBreedImage(breed)
                    }
                    if (response.status == "success") {
                        imageUrl = response.message
                    }
                } catch (e: Exception) {
                    Log.e("EditarCita", "Error obteniendo imagen", e)
                }

                val citaEditada = Appointment(
                    id = receivedAppointment.id,
                    petName = petName,
                    breed = breed.replaceFirstChar { it.uppercaseChar() },
                    imageUrl = imageUrl,
                    ownerName = owner,
                    phoneNumber = phone,
                    symptom = receivedAppointment.symptom // no se edita
                )

                appointmentViewModel.updateAppointment(citaEditada)

                withContext(Dispatchers.Main) {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Cita editada correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homeAppointmentFragment)
                }
            }
        }

        private fun validarCampos(): Boolean {
            return !binding.etPetName.text.isNullOrBlank()
                    &&
                    !binding.actBreed.text.isNullOrBlank() &&
                    !binding.etOwnerName.text.isNullOrBlank() &&
                    !binding.etPhone.text.isNullOrBlank()
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
                    }
                } catch (e: Exception) {
                    Log.e("setupRazaAutocomplete", "Error al obtener razas", e)
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

            fun camposEditados(): Boolean {
                val nombreEditado = binding.etPetName.text.toString() != receivedAppointment.petName
                val razaEditada = binding.actBreed.text.toString() != receivedAppointment.breed
                val propietarioEditado = binding.etOwnerName.text.toString() != receivedAppointment.ownerName
                val telefonoEditado = binding.etPhone.text.toString() != receivedAppointment.phoneNumber

                return nombreEditado || razaEditada || propietarioEditado || telefonoEditado
            }

            fun camposLlenos(): Boolean {
                return campos.all { !it.text.isNullOrBlank() }
            }

            fun actualizarEstadoBoton() {
                val habilitar = camposLlenos() && camposEditados()
                binding.btnEditAppointment.apply {
                    isEnabled = habilitar
                    setTextColor(if (habilitar) Color.WHITE else Color.DKGRAY)
                    iconTint = ColorStateList.valueOf(if (habilitar) Color.WHITE else Color.DKGRAY)
                }
            }

            campos.forEach { campo ->
                campo.doAfterTextChanged {
                    actualizarEstadoBoton()
                }
            }

            actualizarEstadoBoton()
        }


    }
