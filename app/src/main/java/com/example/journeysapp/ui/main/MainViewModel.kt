package com.example.journeysapp.ui.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.repositories.JourneyRepository
import com.example.journeysapp.ui.main.NavEvent.ToJourneyDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: JourneyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())

    private val _navEvent = MutableSharedFlow<NavEvent>()

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    val journeyList = mutableStateListOf<Journey>()

    var contextSelectedJourney: Journey? = null

    private var editedJourney: Journey? = null

    init {
        viewModelScope.launch {
            repository.getAllJourneysFlow()
                .catch { Timber.e(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = emptyList()
                ).collect {
                    journeyList.clear()
                    journeyList.addAll(it)
                }
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

                _uiState.value = _uiState.value.copy(
                    contextMenuSheetOpen = false,
                    confirmDeleteDialogShowing = false
                )
            }

            // Edit Journey
            is UIEvent.OnJourneyEditClick -> {
                editedJourney = event.editedJourney
                _uiState.value = _uiState.value.copy(editBottomSheetOpen = true)
            }

            UIEvent.OnEditSheetDismiss -> _uiState.value = _uiState.value.copy(
                editBottomSheetOpen = false
            )

            is UIEvent.OnJourneyEdited -> viewModelScope.launch {
                repository.updateJourney(event.journey)
                editedJourney = null
            }

            // Handle goals
            is UIEvent.OnGoalIncremented -> viewModelScope.launch {
                repository.incrementGoalProgress(event.journey.uid)
            }

            is UIEvent.OnGoalReset -> viewModelScope.launch {
                repository.resetGoalProgress(event.journey.uid)

                _uiState.value = _uiState.value.copy(
                    contextMenuSheetOpen = false,
                    confirmResetDialogShowing = false
                )
            }

            UIEvent.OnGoalResetClick -> _uiState.value = _uiState.value.copy(
                confirmResetDialogShowing = true
            )

            UIEvent.OnResetJourneyDialogDismiss -> _uiState.value = _uiState.value.copy(
                confirmResetDialogShowing = false
            )

            // Details
            is UIEvent.OnJourneyDetailsClick -> viewModelScope.launch {
                _navEvent.emit(ToJourneyDetails(event.journey.uid))
            }
        }
    }
}

data class UiState(
    val addBottomSheetOpen: Boolean = false,

    val editBottomSheetOpen: Boolean = false,

    val contextMenuSheetOpen: Boolean = false,

    val confirmDeleteDialogShowing: Boolean = false,

    val confirmResetDialogShowing: Boolean = false
)

sealed interface UIEvent {
    data object OnJourneyAddClick : UIEvent

    data object OnAddSheetDismiss : UIEvent

    data class OnJourneyEditClick(val editedJourney: Journey) : UIEvent

    data class OnJourneyEdited(val journey: Journey) : UIEvent

    data object OnEditSheetDismiss : UIEvent

    data object OnJourneyDeleteClick : UIEvent

    data object OnGoalResetClick : UIEvent

    data class OnJourneyContextMenuClick(val journey: Journey) : UIEvent

    data object OnDeleteJourneyDialogDismiss : UIEvent

    data object OnResetJourneyDialogDismiss : UIEvent

    data object OnContextMenuSheetDismiss : UIEvent

    data class OnJourneyCreated(val journey: Journey) : UIEvent

    data class OnJourneyDeleted(val journey: Journey) : UIEvent

    data class OnGoalIncremented(val journey: Journey) : UIEvent

    data class OnGoalReset(val journey: Journey) : UIEvent

    data class OnJourneyDetailsClick(val journey: Journey) : UIEvent
}

sealed class NavEvent {
    data class ToJourneyDetails(val journeyId: Long) : NavEvent()
}