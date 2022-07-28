package com.certified.autopaper.ui.auth.signup

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    val uiState = ObservableField(UIState.EMPTY)

    val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            val auth = Firebase.auth
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                            _success.value = task.isSuccessful
                            if (task.isSuccessful) {
                                uiState.set(UIState.SUCCESS)
                                _message.value = "Check your email for a verification link"
                                auth.signOut()
                            } else {
                                uiState.set(UIState.FAILURE)
                                _message.value = task.exception?.localizedMessage
                            }
                        }
                    } else
                        _message.value = it.exception?.localizedMessage
                }
            } catch (e: Exception) {
                uiState.set(UIState.FAILURE)
                _message.value = "Registration failed: ${e.localizedMessage}"
                _success.value = false
            }
        }
    }

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