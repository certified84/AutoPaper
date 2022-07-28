package com.certified.autopaper.ui.auth.otp

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class OTPViewModel : ViewModel() {

    val uiState = ObservableField(UIState.EMPTY)

    val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    fun signInWithCredentials(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            val auth = Firebase.auth
            auth.signInWithCredential(credential).addOnCompleteListener {
                Log.d("TAG", "signInWithCredentials: ${it.result}")
                Log.d("TAG", "signInWithCredentials: ${it.exception?.localizedMessage}")
                if (it.isSuccessful) {
                    uploadDetails(auth.currentUser!!.uid)
                } else {
                    uiState.set(UIState.FAILURE)
                    _success.value = false
                    _message.value = "An error occurred: ${it.exception?.localizedMessage}"
                }
            }
        }
    }

    private fun uploadDetails(uid: String) {
        viewModelScope.launch {
            val userRef = Firebase.firestore.collection("users").document(uid)
            userRef.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (!document.exists())
                        upload(userRef, uid)
                    else {
                        uiState.set(UIState.SUCCESS)
                        _success.value = true
                    }
                } else {
                    uiState.set(UIState.FAILURE)
                    _success.value = false
                    _message.value = "An error occurred: ${it.exception?.localizedMessage}"
                }
            }
        }
    }

    private fun upload(userRef: DocumentReference, uid: String) {
        userRef.set(Agent(id = uid, authType = "phone"))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    uiState.set(UIState.SUCCESS)
                    _success.value = true
                } else {
                    uiState.set(UIState.FAILURE)
                    _success.value = false
                    _message.value =
                        "An error occurred: ${it.exception?.localizedMessage}"
                }
            }
    }
}