package com.example.clase8.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.clase8.R
import java.util.concurrent.Executor

class LoginFragment : Fragment() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        setupViews(view)
        setupBiometricAuth()
        return view
    }

    private fun setupViews(view: View) {
        view.findViewById<LottieAnimationView>(R.id.fingerprintAnimation).setOnClickListener {
            showBiometricPrompt()
        }
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    showToast("Error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    navigateToHomeAppointments()
                }

                override fun onAuthenticationFailed() {
                    showToast("Huella no reconocida")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_dialog_title))
            .setSubtitle(getString(R.string.fingerprint_dialog_subtitle))
            .setNegativeButtonText(getString(R.string.fingerprint_dialog_negative_button))
            .build()
    }

    private fun showBiometricPrompt() {
        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            showToast("Error en biometría: ${e.message}")
        }
    }

    private fun navigateToHomeAppointments() {
        findNavController().navigate(R.id.action_login_to_homeAppointmentFragment)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}