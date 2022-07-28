package com.certified.autopaper.ui.auth.login

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    val uiState = ObservableField(UIState.EMPTY)

    val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    fun signInWithEmailAndPassword(email: String, password: String) {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                if (auth.currentUser?.isEmailVerified == true) {
                    uiState.set(UIState.SUCCESS)
                    _success.value = true
                } else {
                    _message.value = "Check your email for a verification link"
                    uiState.set(UIState.FAILURE)
                    _success.value = false
                }
            } else {
                uiState.set(UIState.FAILURE)
                _message.value = "An error occurred: ${it.exception?.localizedMessage}"
            }
        }
    }
}