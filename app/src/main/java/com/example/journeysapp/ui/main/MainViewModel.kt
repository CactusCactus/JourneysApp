package com.example.journeysapp.ui.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.repositories.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: JourneyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val journeyList = mutableStateListOf<Journey>()

    init {
        viewModelScope.launch {
            journeyList.clear()
            journeyList.addAll(repository.getAllJourneys())
        }
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            UIEvent.OnAddJourneyClick -> _uiState.value =
                _uiState.value.copy(addBottomSheetOpen = true)

            UIEvent.OnAddSheetDismiss -> _uiState.value =
                _uiState.value.copy(addBottomSheetOpen = false)

            is UIEvent.OnJourneyCreated -> viewModelScope.launch {
                repository.insertJourney(event.journey)
                journeyList.add(event.journey)
            }
        }
    }
}

data class UiState(
    val addBottomSheetOpen: Boolean = false
)

sealed class UIEvent {
    data object OnAddJourneyClick : UIEvent()

    data object OnAddSheetDismiss : UIEvent()

    data class OnJourneyCreated(val journey: Journey) : UIEvent()
}