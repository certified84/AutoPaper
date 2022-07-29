package com.certified.autopaper.ui.profile

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.autopaper.data.model.AccountResolveResponse
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.data.model.Banks
import com.certified.autopaper.data.repository.PaystackRepository
import com.certified.autopaper.util.ApiErrorUtil
import com.certified.autopaper.util.UIState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: PaystackRepository,
    private val apiErrorUtil: ApiErrorUtil
) :
    ViewModel() {

    val uiState = ObservableField(UIState.LOADING)
    val personalDetailsUiState = ObservableField(UIState.LOADING)
    val bankDetailsUiState = ObservableField(UIState.LOADING)

    private val _userDetails = MutableLiveData<Agent>()
    val userDetails: LiveData<Agent> get() = _userDetails

    val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _accountDetails = MutableLiveData<AccountResolveResponse>()
    val accountDetails: LiveData<AccountResolveResponse> get() = _accountDetails

    private val _banks = MutableLiveData<Banks>()
    val banks: LiveData<Banks> get() = _banks

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
                        personalDetailsUiState.set(UIState.SUCCESS)
                        _message.value = "Image uploaded successfully"
                    }
                }
            } catch (e: Exception) {
                personalDetailsUiState.set(UIState.FAILURE)
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
                        personalDetailsUiState.set(UIState.FAILURE)
                        bankDetailsUiState.set(UIState.FAILURE)
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
                personalDetailsUiState.set(UIState.SUCCESS)
                bankDetailsUiState.set(UIState.SUCCESS)
                _message.value = "Profile updated successfully"
            } else {
                personalDetailsUiState.set(UIState.FAILURE)
                bankDetailsUiState.set(UIState.FAILURE)
                _message.value = "An error occurred: ${it.exception?.localizedMessage}"
                Log.d("TAG", "updateProfile: ${it.exception?.localizedMessage}")
            }
        }
    }

    fun resolveAccount(token: String, accountNumber: String, bankCode: String) {
        viewModelScope.launch {
            try {
                val response = repository.resolveAccount(token, accountNumber, bankCode)
                Log.d("TAG", "resolveAccount: Code: ${response.code()}")
                if (response.isSuccessful) {
                    bankDetailsUiState.set(UIState.SUCCESS)
                    _accountDetails.value = response.body()
                    Log.d("TAG", "resolveAccount: Response: ${response.body()}")
                } else {
                    bankDetailsUiState.set(UIState.FAILURE)
                    _accountDetails.value = AccountResolveResponse()
                    val error = apiErrorUtil.parseError(response)
                    _message.value = "An error occurred ${error?.error}"
                    Log.d("TAG", "resolveAccount: Error: ${error?.error}")
                }
            } catch (e: Exception) {
                bankDetailsUiState.set(UIState.FAILURE)
                _accountDetails.value = AccountResolveResponse()
                _message.value = "An error occurred: ${e.localizedMessage}"
                Log.d("TAG", "resolveAccount: Exception: ${e.localizedMessage}")
            }
        }
    }

    fun getBanks(token: String) {
        bankDetailsUiState.set(UIState.LOADING)
        viewModelScope.launch {
            try {
                val response = repository.getBanks(token)
                if (response.isSuccessful) {
                    bankDetailsUiState.set(UIState.SUCCESS)
                    _banks.value = response.body()
                    Log.d("TAG", "getBanks: Response: ${response.body()}")
                } else {
                    bankDetailsUiState.set(UIState.FAILURE)
                    val error = apiErrorUtil.parseError(response)
                    _message.value = error?.error
                    Log.d("TAG", "getBanks: Error: ${error?.error}")
                }
            } catch (e: Exception) {
                bankDetailsUiState.set(UIState.FAILURE)
                _message.value = "An error occurred: ${e.localizedMessage}"
                Log.d("TAG", "getBanks: Exception: ${e.localizedMessage}")
            }
        }
    }
}