package com.certified.autopaper.ui.auth.login

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
import com.certified.autopaper.R
import com.certified.autopaper.databinding.FragmentLoginBinding
import com.certified.autopaper.util.Extensions.showToast
import com.certified.autopaper.util.UIState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val args: LoginFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                No need to do anything here since the OTPFragment.kt handles OTP verification.
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
            Log.d("TAG", "onVerificationFailed: ${e.localizedMessage}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            _verificationId = verificationId
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToOTPFragment(
                    "login",
                    "",
                    verificationId
                )
            )
        }
    }

    private val required = "* Required"
    private lateinit var _verificationId: String
    private var loginWith = "email"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            success.observe(viewLifecycleOwner) {
                if (it) {
                    _success.postValue(false)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }
            }
            message.observe(viewLifecycleOwner) {
                if (it != null) {
                    showToast(it)
                    _message.postValue(null)
                }
            }
        }

        binding.apply {
            uiState = viewModel.uiState
            lifecycleOwner = this@LoginFragment

            btnBack.setOnBackClickedListener {
                when (args.from) {
                    "signup" -> findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToSignupFragment(
                            "login"
                        )
                    )
                    "onboarding" -> findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToOnboardingFragment())
                }
            }
            btnChangePassword.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            }
            btnSignup.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToSignupFragment(
                        "login"
                    )
                )
            }
            btnContinue.setOnClickListener {
                when (loginWith) {
                    "phone" -> {
                        val phoneNumber = etPhone.text.toString()

                        if (phoneNumber.isBlank()) {
                            etPhoneLayout.error = required
                            etPhone.requestFocus()
                            return@setOnClickListener
                        }

                        etPhoneLayout.error = null
                        viewModel.uiState.set(UIState.LOADING)
                        loginWithPhone(phoneNumber)
                    }
                    "email" -> {
                        val email = etEmail.text.toString()
                        val password = etPassword.text.toString()

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

                        etEmailLayout.error = null
                        etPasswordLayout.error = null
                        with(viewModel) {
                            uiState.set(UIState.LOADING)
                            signInWithEmailAndPassword(email, password)
                        }
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
                loginWith = "email"
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
                loginWith = "phone"
            }
        }
    }

    private fun loginWithPhone(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+234 $phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}