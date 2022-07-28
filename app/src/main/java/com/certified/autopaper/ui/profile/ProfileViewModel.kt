package com.certified.autopaper.ui.profile

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.certified.autopaper.util.UIState

class ProfileViewModel : ViewModel() {
    val uiState = ObservableField(UIState.EMPTY)
}