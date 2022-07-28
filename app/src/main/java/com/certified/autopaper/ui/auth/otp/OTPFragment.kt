package com.certified.autopaper.ui.auth.otp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.certified.autopaper.databinding.FragmentOtpBinding
import com.certified.autopaper.util.Extensions.showToast
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OTPFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val args: OTPFragmentArgs by navArgs()
    private val viewModel: OTPViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtpBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            success.observe(viewLifecycleOwner) {
                if (it) {
                    _success.postValue(false)
                    findNavController().navigate(OTPFragmentDirections.actionOTPFragmentToHomeFragment())
                }
            }
            message.observe(viewLifecycleOwner) {
                if (it != null) {
                    showToast(it)
                    _message.postValue(null)
                }
            }
        }

        binding.uiState = viewModel.uiState
        binding.lifecycleOwner = this

        binding.apply {
            btnBack.setOnBackClickedListener {
                when (args.from) {
                    "signup" -> findNavController().navigate(
                        OTPFragmentDirections.actionOTPFragmentToSignupFragment(
                            "onboarding"
                        )
                    )
                    "login" -> findNavController().navigate(
                        OTPFragmentDirections.actionOTPFragmentToLoginFragment(
                            "onboarding"
                        )
                    )
                }
            }

            btnContinue.setOnClickListener {
                val otpCode = etOtp.text.toString()
                if (otpCode.isBlank()) {
                    etOtpLayout.error = "* Required"
                    etOtp.requestFocus()
                    return@setOnClickListener
                }

                etOtpLayout.error = null
                viewModel.uiState.set(UIState.LOADING)
                verifyCode(otpCode)
            }
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(args.verificationID, code)
        viewModel.signInWithCredentials(credential)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}