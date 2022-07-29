package com.certified.autopaper.ui.home

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val uiState = ObservableField(UIState.LOADING)

    private val _userDetails = MutableLiveData<Agent>()
    val userDetails: LiveData<Agent> get() = _userDetails

    val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    init {
        getUserDetails()
    }

    private fun getUserDetails() {
        viewModelScope.launch {
            val auth = Firebase.auth
            Firebase.firestore.collection("users").document(auth.currentUser!!.uid)
                .addSnapshotListener { value, error ->
                    if (value == null || error != null) {
                        uiState.set(UIState.FAILURE)
                        _message.value = error?.localizedMessage
                    } else {
                        uiState.set(UIState.SUCCESS)
                        _userDetails.value = value.toObject(Agent::class.java)
                    }
                }
        }
    }
}