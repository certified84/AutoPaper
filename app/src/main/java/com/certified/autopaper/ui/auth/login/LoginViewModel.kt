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
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            _success.value = it.isSuccessful
            if (it.isSuccessful) {
                uiState.set(UIState.SUCCESS)
            } else {
                uiState.set(UIState.FAILURE)
                _message.value = "An error occurred: ${it.exception?.localizedMessage}"
            }
        }
    }
}