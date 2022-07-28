package com.certified.autopaper.ui.auth.signup

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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
                        uploadDetails(auth.currentUser!!)
                    } else {
                        uiState.set(UIState.FAILURE)
                        _message.value = it.exception?.localizedMessage
                    }
                }
            } catch (e: Exception) {
                uiState.set(UIState.FAILURE)
                _message.value = "Registration failed: ${e.localizedMessage}"
                _success.value = false
            }
        }
    }

    private fun uploadDetails(user: FirebaseUser) {
        Firebase.firestore.collection("users").document(user.uid)
            .set(Agent(id = user.uid, authType = "email"))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sendVerificationEmail(user)
                } else {
                    uiState.set(UIState.FAILURE)
                    _success.value = false
                    _message.value =
                        "An error occurred: ${it.exception?.localizedMessage}"
                }
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            _success.value = task.isSuccessful
            if (task.isSuccessful) {
                uiState.set(UIState.SUCCESS)
                _message.value = "Check your email for a verification link"
                Firebase.auth.signOut()
            } else {
                uiState.set(UIState.FAILURE)
                _message.value = task.exception?.localizedMessage
            }
        }
    }
}