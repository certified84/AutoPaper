package com.certified.autopaper.ui.auth.signup

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.certified.autopaper.databinding.FragmentSignupBinding
import com.certified.autopaper.util.Extensions.showToast
import com.certified.autopaper.util.UIState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val args: SignupFragmentArgs by navArgs()
    private val viewModel: SignupViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    private val required = "* Required"
    private lateinit var _verificationId: String
    private var signupWith = "email"

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                No need to do anything here since the OTPFragment.kt handles OTP verification.
            viewModel.uiState.set(UIState.SUCCESS)
        }

        override fun onVerificationFailed(e: FirebaseException) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)
//
//                if (e is FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e is FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }
            viewModel.uiState.set(UIState.FAILURE)
            Log.d("TAG", "onVerificationFailed: ${e.localizedMessage}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            viewModel.uiState.set(UIState.SUCCESS)
            _verificationId = verificationId
            findNavController().navigate(
                SignupFragmentDirections.actionSignupFragmentToOTPFragment(
                    "signup",
                    "",
                    verificationId
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            message.observe(viewLifecycleOwner) {
                if (it != null) {
                    showToast(it)
                    _message.postValue(null)
                }
            }
            success.observe(viewLifecycleOwner) {
                if (it) {
                    _success.postValue(false)
                    findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment("signup"))
                }
            }
        }

        binding.lifecycleOwner = this
        binding.uiState = viewModel.uiState

        binding.apply {
            btnBack.setOnBackClickedListener {
                when (args.from) {
                    "onboarding" -> findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToOnboardingFragment())
                    "login" -> findNavController().navigate(
                        SignupFragmentDirections.actionSignupFragmentToLoginFragment(
                            "signup"
                        )
                    )
                }
            }
            btnLogin.setOnClickListener {
                findNavController().navigate(
                    SignupFragmentDirections.actionSignupFragmentToLoginFragment(
                        "signup"
                    )
                )
            }
            btnContinue.setOnClickListener {
                when (signupWith) {
                    "phone" -> {
                        val phoneNumber = etPhone.text.toString()

                        if (phoneNumber.isBlank()) {
                            etPhoneLayout.error = required
                            etPhone.requestFocus()
                            return@setOnClickListener
                        }

                        etPhoneLayout.error = null
                        viewModel.uiState.set(UIState.LOADING)
                        signupWithPhone(phoneNumber)
                    }
                    "email" -> {
                        val email = etEmail.text.toString()
                        val password = etPassword.text.toString()
                        val confirmPassword = etConfirmPassword.text.toString()

                        if (email.isBlank()) {
                            etEmailLayout.error = required
                            etEmail.requestFocus()
                            return@setOnClickListener
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            binding.etEmailLayout.apply {
                                error = "Enter a valid email"
                                requestFocus()
                                return@setOnClickListener
                            }
                        }

                        if (password.isBlank()) {
                            etPasswordLayout.error = required
                            etPassword.requestFocus()
                            return@setOnClickListener
                        }

                        if (confirmPassword.isBlank()) {
                            etConfirmPasswordLayout.error = required
                            etConfirmPassword.requestFocus()
                            return@setOnClickListener
                        }

                        if (password != confirmPassword) {
                            etConfirmPasswordLayout.error = "* Mismatch"
                            etConfirmPassword.requestFocus()
                            return@setOnClickListener
                        }

                        etEmailLayout.error = null
                        etPasswordLayout.error = null
                        etConfirmPasswordLayout.error = null
                        viewModel.uiState.set(UIState.LOADING)
                        viewModel.createUserWithEmailAndPassword(email, password)
                    }
                }
            }

            cardEmail.setOnClickListener {
//                Change view visibility
                groupPhone.visibility = View.INVISIBLE
                groupEmail.visibility = View.VISIBLE
//                Change view colors
                cardEmail.setCardBackgroundColor(Color.parseColor("#4778EC"))
                cardEmail.strokeColor = Color.parseColor("#4778EC")
                tvEmail.setTextColor(Color.WHITE)
                ivEmail.imageTintList = ColorStateList.valueOf(Color.WHITE)
                cardPhone.setCardBackgroundColor(Color.WHITE)
                cardPhone.strokeColor = Color.parseColor("#E5E5E5")
                tvPhone.setTextColor(Color.parseColor("#9E9E9E"))
                ivPhone.imageTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
//                set login type
                signupWith = "email"
            }
            cardPhone.setOnClickListener {
//                Change view visibility
                groupEmail.visibility = View.INVISIBLE
                groupPhone.visibility = View.VISIBLE
//                Change view colors
                cardPhone.setCardBackgroundColor(Color.parseColor("#4778EC"))
                cardPhone.strokeColor = Color.parseColor("#4778EC")
                tvPhone.setTextColor(Color.WHITE)
                ivPhone.imageTintList = ColorStateList.valueOf(Color.WHITE)
                cardEmail.setCardBackgroundColor(Color.WHITE)
                cardEmail.strokeColor = Color.parseColor("#E5E5E5")
                tvEmail.setTextColor(Color.parseColor("#9E9E9E"))
                ivEmail.imageTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
//                set login type
                signupWith = "phone"
            }
        }
    }

    private fun signupWithPhone(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+234 $phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}