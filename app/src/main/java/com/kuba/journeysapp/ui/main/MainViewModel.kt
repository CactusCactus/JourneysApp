package com.kuba.journeysapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.data.model.internal.SortMode
import com.kuba.journeysapp.data.repositories.JourneyRepository
import com.kuba.journeysapp.data.repositories.UserPreferencesRepository
import com.kuba.journeysapp.ui.main.NavEvent.ToJourneyDetails
import com.kuba.journeysapp.util.GOAL_DEBOUNCE_TIME_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
class MainViewModel @Inject constructor(
    private val repository: JourneyRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())

    private val _navEvent = MutableSharedFlow<NavEvent>()

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    private val _journeyList = MutableStateFlow<List<Journey>>(emptyList())

    val journeyList: StateFlow<List<Journey>> = _journeyList.asStateFlow()

    private var incrementJob: Job = Job()

    private val pendingIncrements = mutableMapOf<Long, Int>()

    var contextSelectedJourney: Journey? = null

    private var editedJourney: Journey? = null

    init {
        observeFlows()
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
            is UIEvent.OnGoalIncremented -> incrementLocallyAndUpdateWithDelay(event.journey)

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

            // Sorting
            is UIEvent.SortModeChanged -> viewModelScope.launch {
                userPreferencesRepository.saveSortMode(event.sortMode)
            }
        }
    }

    /* The goal is to delay update to Goal progress to avoid jumping UI when sorting by progress,
    * and secondarily to reduce number of quick database writes */
    private fun incrementLocallyAndUpdateWithDelay(journey: Journey) {
        val itemIndex = _journeyList.value.indexOfFirst { it.uid == journey.uid }
        val journeyId = journey.uid

        if (itemIndex != -1) {
            val currentJourney = _journeyList.value[itemIndex]

            if (currentJourney.goal.progress < currentJourney.goal.value) {
                val updatedJourney = currentJourney.copy(
                    goal = currentJourney.goal.copy(progress = currentJourney.goal.progress + 1)
                )

                val localUpdatedList = _journeyList.value.toMutableList()
                localUpdatedList[itemIndex] = updatedJourney
                _journeyList.value = localUpdatedList

                pendingIncrements[journeyId] = (pendingIncrements[journeyId] ?: 0) + 1

                incrementJob.cancel()
                incrementJob = viewModelScope.launch {
                    delay(GOAL_DEBOUNCE_TIME_MS)

                    repository.incrementGoalProgressBatch(pendingIncrements)
                    pendingIncrements.clear()
                }
            }
        }
    }

    private fun observeFlows() {
        viewModelScope.launch {
            userPreferencesRepository.getSortModeFlow()
                .catch { Timber.e(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = null
                ).collect {
                    if (it != _uiState.value.sortMode && it != null) {
                        _uiState.value = _uiState.value.copy(sortMode = it)
                    }
                }
        }

        viewModelScope.launch {
            repository.getAllJourneysSorted()
                .catch { exception ->
                    Timber.e(exception, "Error collecting sorted journeys")
                    emit(emptyList())
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = emptyList()
                ).collect { newJourneyList ->
                    val hasPendingIncrements = pendingIncrements.keys.any { pendingId ->
                        journeyList.value.any { it.uid == pendingId }
                    }

                    if (!hasPendingIncrements || newJourneyList.size != _journeyList.value.size) {
                        _journeyList.value = newJourneyList
                    } else {
                        incrementJob.invokeOnCompletion { _journeyList.value = newJourneyList }
                    }
                }
        }
    }
}

data class UiState(
    val addBottomSheetOpen: Boolean = false,

    val editBottomSheetOpen: Boolean = false,

    val contextMenuSheetOpen: Boolean = false,

    val confirmDeleteDialogShowing: Boolean = false,

    val confirmResetDialogShowing: Boolean = false,

    val sortMode: SortMode = SortMode.ALPHABETICALLY_ASC
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

    data class SortModeChanged(val sortMode: SortMode) : UIEvent
}

sealed class NavEvent {
    data class ToJourneyDetails(val journeyId: Long) : NavEvent()
}