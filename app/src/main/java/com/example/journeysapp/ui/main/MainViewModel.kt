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
class MainViewModel @Inject constructor(private val repository: JourneyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val journeyList = mutableStateListOf<Journey>()

    var contextSelectedJourney: Journey? = null

    var editedJourney: Journey? = null

    init {
        viewModelScope.launch {
            journeyList.clear()
            journeyList.addAll(repository.getAllJourneys())
        }
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            // Create Journey Events
            UIEvent.OnJourneyAddClick -> _uiState.value =
                _uiState.value.copy(addBottomSheetOpen = true)

            UIEvent.OnAddSheetDismiss -> _uiState.value =
                _uiState.value.copy(addBottomSheetOpen = false)

            is UIEvent.OnJourneyCreated -> viewModelScope.launch {
                repository.insertJourney(event.journey)
                journeyList.add(event.journey)
            }

            // Journey context menu events
            UIEvent.OnContextMenuSheetDismiss -> _uiState.value =
                _uiState.value.copy(contextMenuSheetOpen = false)

            is UIEvent.OnJourneyContextMenuClick -> {
                _uiState.value = _uiState.value.copy(contextMenuSheetOpen = true)
                contextSelectedJourney = event.journey
            }

            // Delete Journey events
            UIEvent.OnJourneyDeleteClick ->
                _uiState.value = _uiState.value.copy(confirmDeleteDialogShowing = true)

            UIEvent.OnDeleteJourneyDialogDismiss ->
                _uiState.value = _uiState.value.copy(confirmDeleteDialogShowing = false)

            is UIEvent.OnJourneyDeleted -> viewModelScope.launch {
                repository.deleteJourney(event.journey)
                journeyList.remove(event.journey)

                _uiState.value = _uiState.value.copy(
                    contextMenuSheetOpen = false,
                    confirmDeleteDialogShowing = false
                )
            }

            // Edit Journey edit
            is UIEvent.OnJourneyEditClick -> {
                editedJourney = event.editedJourney
                _uiState.value = _uiState.value.copy(editBottomSheetOpen = true)
            }

            UIEvent.OnEditSheetDismiss -> _uiState.value = _uiState.value.copy(
                editBottomSheetOpen = false
            )

            is UIEvent.OnJourneyEdited -> viewModelScope.launch {
                repository.updateJourney(event.journey)

                if (editedJourney != null) {
                    journeyList[journeyList.indexOf(editedJourney)] = event.journey
                    editedJourney = null
                }
            }
        }
    }
}

data class UiState(
    val addBottomSheetOpen: Boolean = false,

    val editBottomSheetOpen: Boolean = false,

    val contextMenuSheetOpen: Boolean = false,

    val confirmDeleteDialogShowing: Boolean = false
)

sealed class UIEvent {
    data object OnJourneyAddClick : UIEvent()

    data object OnAddSheetDismiss : UIEvent()

    data class OnJourneyEditClick(val editedJourney: Journey) : UIEvent()

    data class OnJourneyEdited(val journey: Journey) : UIEvent()

    data object OnEditSheetDismiss : UIEvent()

    data object OnJourneyDeleteClick : UIEvent()

    data class OnJourneyContextMenuClick(val journey: Journey) : UIEvent()

    data object OnDeleteJourneyDialogDismiss : UIEvent()

    data object OnContextMenuSheetDismiss : UIEvent()

    data class OnJourneyCreated(val journey: Journey) : UIEvent()

    data class OnJourneyDeleted(val journey: Journey) : UIEvent()
}