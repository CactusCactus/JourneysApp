package com.example.journeysapp.ui.details

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.repositories.JourneyRepository
import com.example.journeysapp.ui.details.DetailsViewModel.NavEvent.Finish
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: JourneyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _navEvent = MutableSharedFlow<NavEvent>()

    private val _uiState = MutableStateFlow<UIState>(UIState())

    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val journeyId: Long? = savedStateHandle[DetailsActivity.EXTRA_JOURNEY_ID]
            journeyId?.let {
                _uiState.value = _uiState.value.copy(journey = repository.getJourney(it))
            } ?: run {
                _navEvent.emit(Finish(Activity.RESULT_CANCELED))
            }
        }
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            UIEvent.OnContextMenuSheetDismiss -> _uiState.value = _uiState.value.copy(
                contextMenuSheetOpen = false
            )

            UIEvent.OnContextMenuSheetOpen -> _uiState.value = _uiState.value.copy(
                contextMenuSheetOpen = true
            )

            UIEvent.OnContextMenuDeleteClicked -> _uiState.value = _uiState.value.copy(
                confirmDeleteDialogShowing = true
            )

            UIEvent.OnContextMenuResetClicked -> _uiState.value = _uiState.value.copy(
                confirmResetDialogShowing = true
            )

            UIEvent.OnGoalResetDialogDismiss -> _uiState.value = _uiState.value.copy(
                confirmResetDialogShowing = false
            )

            UIEvent.OnJourneyDeleteDialogDismiss -> _uiState.value = _uiState.value.copy(
                confirmDeleteDialogShowing = false
            )

            UIEvent.OnContextMenuEditClicked -> TODO()

            UIEvent.OnJourneyDeleted -> viewModelScope.launch {
                uiState.value.journey?.let {
                    repository.deleteJourney(it)
                }

                _navEvent.emit(Finish(Activity.RESULT_OK))
            }

            UIEvent.OnGoalReset -> viewModelScope.launch {
                uiState.value.journey?.let {
                    repository.resetGoalProgress(it.uid)
                    val goal = it.goal.copy(progress = 0)

                    _uiState.value = _uiState.value.copy(
                        journey = it.copy(goal = goal),
                        contextMenuSheetOpen = false,
                        confirmResetDialogShowing = false
                    )
                }

            }
        }
    }

    sealed interface UIEvent {
        object OnContextMenuSheetOpen : UIEvent

        object OnContextMenuSheetDismiss : UIEvent

        object OnContextMenuDeleteClicked : UIEvent

        object OnContextMenuEditClicked : UIEvent

        object OnContextMenuResetClicked : UIEvent

        object OnJourneyDeleted : UIEvent

        object OnGoalReset : UIEvent

        object OnJourneyDeleteDialogDismiss : UIEvent

        object OnGoalResetDialogDismiss : UIEvent
    }

    data class UIState(
        val journey: Journey? = null,

        val contextMenuSheetOpen: Boolean = false,

        val confirmDeleteDialogShowing: Boolean = false,

        val confirmResetDialogShowing: Boolean = false,
    )

    sealed interface NavEvent {
        data class Finish(val resultCode: Int = Activity.RESULT_CANCELED) : NavEvent
    }
}
