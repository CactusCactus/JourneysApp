package com.example.journeysapp.ui.details

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.repositories.JourneyRepository
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
                _navEvent.emit(NavEvent.Finish(Activity.RESULT_CANCELED))
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

            UIEvent.OnContextMenuDeleteClicked -> viewModelScope.launch {
                uiState.value.journey?.let {
                    repository.deleteJourney(it)
                }

                _navEvent.emit(NavEvent.Finish(Activity.RESULT_OK))
            }

            UIEvent.OnContextMenuEditClicked -> TODO()
            UIEvent.OnContextMenuResetClicked -> TODO()
            UIEvent.OnJourneyDeleted -> viewModelScope.launch {
                uiState.value.journey?.let {
                    repository.deleteJourney(it)
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
    }

    data class UIState(
        val journey: Journey? = null,

        val contextMenuSheetOpen: Boolean = false,

        val confirmDeleteDialogShowing: Boolean = false,
    )

    sealed interface NavEvent {
        data class Finish(val resultCode: Int = Activity.RESULT_CANCELED) : NavEvent
    }
}
