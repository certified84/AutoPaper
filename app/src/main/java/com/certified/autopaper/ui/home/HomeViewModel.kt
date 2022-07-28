package com.certified.autopaper.ui.home

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.certified.autopaper.util.UIState

class HomeViewModel : ViewModel() {
    val uiState = ObservableField(UIState.EMPTY)
}