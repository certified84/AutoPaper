package com.certified.autopaper.ui.auth.signup

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignupViewModel: ViewModel() {

    val uiState = ObservableField(UIState.EMPTY)

//    fun signupWithPhone(phoneNumber: String) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber("+234 $phoneNumber")       // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(requireActivity())                 // Activity (for callback binding)
//            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }

}