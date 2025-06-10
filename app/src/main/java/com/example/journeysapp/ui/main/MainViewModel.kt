package com.example.journeysapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.journeysapp.data.repositories.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(repository: JourneyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(event: UIEvent) {
        when (event) {
            UIEvent.OnAddJourneyClick -> _uiState.value =
                _uiState.value.copy(addBottomSheetOpen = true)

            UIEvent.OnAddSheetDismiss -> _uiState.value =
                _uiState.value.copy(addBottomSheetOpen = false)
        }
    }
}

data class UiState(
    val addBottomSheetOpen: Boolean = false
)

sealed class UIEvent {
    data object OnAddJourneyClick : UIEvent()

    data object OnAddSheetDismiss : UIEvent()
}