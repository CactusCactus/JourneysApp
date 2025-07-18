package com.kuba.journeysapp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuba.journeysapp.data.model.GoalHistory
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.data.repositories.GoalHistoryRepository
import com.kuba.journeysapp.data.repositories.JourneyRepository
import com.kuba.journeysapp.ui.details.DetailsViewModel.NavEvent.Finish
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    private val historyRepository: GoalHistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _navEvent = MutableSharedFlow<NavEvent>()

    private val _uiState = MutableStateFlow<UIState>(UIState())

    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        val journeyId: Long? = savedStateHandle[DetailsActivity.EXTRA_JOURNEY_ID]

        journeyId?.let {
            viewModelScope.launch {
                journeyRepository.getJourneyFlow(it)
                    .catch {
                        Timber.e(it)
                        _navEvent.emit(Finish)
                    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)
                    .collect { _uiState.value = _uiState.value.copy(journey = it) }
            }

            viewModelScope.launch {
                historyRepository.getGoalHistoryFlow(it)
                    .catch { Timber.e(it) }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
                    .collect { _uiState.value = _uiState.value.copy(goalHistory = it) }
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

            is UIEvent.OnContextMenuEditClicked -> _uiState.value = _uiState.value.copy(
                contextMenuSheetOpen = false,
                editSheetIconPickerShowing = event.showIconPicker,
                editSheetShowing = true
            )

            UIEvent.OnEditSheetDismiss -> _uiState.value = _uiState.value.copy(
                editSheetShowing = false,
                editSheetIconPickerShowing = false
            )

            UIEvent.OnJourneyDeleted -> viewModelScope.launch {
                uiState.value.journey?.let {
                    journeyRepository.deleteJourney(it)
                }

                _navEvent.emit(Finish)
            }

            UIEvent.OnGoalReset -> viewModelScope.launch {
                uiState.value.journey?.let {
                    journeyRepository.resetGoalProgress(it.uid)

                    _uiState.value = _uiState.value.copy(
                        contextMenuSheetOpen = false,
                        confirmResetDialogShowing = false
                    )
                }
            }

            is UIEvent.OnJourneyEdited -> viewModelScope.launch {
                journeyRepository.updateJourney(event.journey)

                _uiState.value = _uiState.value.copy(
                    editSheetShowing = false
                )
            }

            UIEvent.OnGoalDecremented -> viewModelScope.launch {
                val journey = uiState.value.journey ?: return@launch

                journeyRepository.decrementGoalProgress(journey.uid)
            }

            UIEvent.OnGoalIncremented -> viewModelScope.launch {
                val journey = uiState.value.journey ?: return@launch

                journeyRepository.incrementGoalProgress(journey.uid)
            }
        }
    }

    sealed interface UIEvent {
        object OnContextMenuSheetOpen : UIEvent

        object OnContextMenuSheetDismiss : UIEvent

        object OnContextMenuDeleteClicked : UIEvent

        object OnJourneyDeleteDialogDismiss : UIEvent

        data class OnContextMenuEditClicked(val showIconPicker: Boolean = false) : UIEvent

        object OnEditSheetDismiss : UIEvent

        object OnContextMenuResetClicked : UIEvent

        object OnGoalResetDialogDismiss : UIEvent

        object OnJourneyDeleted : UIEvent

        data class OnJourneyEdited(val journey: Journey) : UIEvent

        object OnGoalReset : UIEvent

        object OnGoalIncremented : UIEvent

        object OnGoalDecremented : UIEvent
    }

    data class UIState(
        val journey: Journey? = null,
        val goalHistory: List<GoalHistory> = emptyList(),
        val contextMenuSheetOpen: Boolean = false,
        val editSheetShowing: Boolean = false,
        val editSheetIconPickerShowing: Boolean = false,
        val confirmDeleteDialogShowing: Boolean = false,
        val confirmResetDialogShowing: Boolean = false,
    )

    sealed interface NavEvent {
        object Finish : NavEvent
    }
}
