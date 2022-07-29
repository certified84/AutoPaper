package com.certified.autopaper.ui.profile

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.util.UIState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
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

    fun uploadImage(uri: Uri?, path: String) {
        viewModelScope.launch {
            try {
                val profileImageRef = Firebase.storage.reference.child(path)
                profileImageRef.putFile(uri!!).await()
                val downloadUrl = profileImageRef.downloadUrl.await()
//                uploadImage(downloadUrl)?.await()
                updateProfileImageUrl(downloadUrl.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        uiState.set(UIState.SUCCESS)
                        _message.value = "Image uploaded successfully"
                    }
                }
            } catch (e: Exception) {
                uiState.set(UIState.FAILURE)
                _message.value = "An error occurred: ${e.localizedMessage}"
                Log.d("TAG", "uploadImage: Error: ${e.localizedMessage}")
            }
        }
    }

    private fun uploadImage(uri: Uri?): Task<Void>? {
        val profileChangeRequest = userProfileChangeRequest { photoUri = uri }
        return Firebase.auth.currentUser?.updateProfile(profileChangeRequest)
    }

    private fun updateProfileImageUrl(imageUrl: String): Task<Void> {
        return Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
            .update("photoUrl", imageUrl)
    }

    fun updateProfile(user: Agent) {
        viewModelScope.launch {
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .set(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateUserName(user.name)
                    } else {
                        uiState.set(UIState.FAILURE)
                        _message.value = "An error occurred: ${it.exception?.localizedMessage}"
                        Log.d("TAG", "updateProfile: ${it.exception?.localizedMessage}")
                    }
                }
        }
    }

    private fun updateUserName(name: String) {
        val profileChangeRequest = userProfileChangeRequest { displayName = name }
        Firebase.auth.currentUser?.updateProfile(profileChangeRequest)?.addOnCompleteListener {
            if (it.isSuccessful) {
                uiState.set(UIState.SUCCESS)
                _message.value = "Profile updated successfully"
            } else {
                uiState.set(UIState.FAILURE)
                _message.value = "An error occurred: ${it.exception?.localizedMessage}"
                Log.d("TAG", "updateProfile: ${it.exception?.localizedMessage}")
            }
        }
    }
}