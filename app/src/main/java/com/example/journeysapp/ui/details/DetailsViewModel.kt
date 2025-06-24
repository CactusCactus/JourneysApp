package com.example.journeysapp.ui.details

import androidx.lifecycle.SavedStateHandle
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
class DetailsViewModel @Inject constructor(
    private val repository: JourneyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)

    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val journeyId: Long? = savedStateHandle[DetailsActivity.EXTRA_JOURNEY_ID]
            _uiState.value = journeyId?.let {
                UIState.JourneyFetched(repository.getJourney(it))
            } ?: UIState.Error
        }
    }

    sealed interface UIState {
        data class JourneyFetched(val journey: Journey) : UIState

        object Error : UIState

        object Loading : UIState
    }
}