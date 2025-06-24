package com.example.journeysapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyContextMenuOption
import com.example.journeysapp.ui.common.ConfirmDialog
import com.example.journeysapp.ui.details.DetailsActivity
import com.example.journeysapp.ui.main.composables.JourneysLazyColumn
import com.example.journeysapp.ui.main.composables.MainBottomBar
import com.example.journeysapp.ui.main.composables.MainTopBar
import com.example.journeysapp.ui.main.composables.bottomSheets.AddJourneyBottomSheet
import com.example.journeysapp.ui.main.composables.bottomSheets.EditJourneyBottomSheet
import com.example.journeysapp.ui.main.composables.bottomSheets.JourneyContextMenuBottomSheet
import com.example.journeysapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                Scaffold(
                    topBar = { MainTopBar() }, bottomBar = {
                        MainBottomBar(onAddClick = {
                            mainViewModel.onEvent(UIEvent.OnJourneyAddClick)
                        })
                    }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    JourneysLazyColumn(
                        journeyList = mainViewModel.journeyList.toList(),
                        onLongPress = {
                            mainViewModel.onEvent(UIEvent.OnJourneyContextMenuClick(it))
                        },
                        onClick = {
                            mainViewModel.onEvent(UIEvent.OnJourneyDetailsClick(it))
                        },
                        onIncrementClicked = {
                            mainViewModel.onEvent(UIEvent.OnGoalIncremented(it))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                    ShowDialogsAndBottomSheets()
                    ObserveNavigationEvents()
                }
            }
        }
    }

    @Composable
    private fun ObserveNavigationEvents() {
        LaunchedEffect(key1 = true) {
            mainViewModel.navEvent.collect { event ->
                when (event) {
                    is NavEvent.ToJourneyDetails -> startActivity(
                        DetailsActivity.newIntent(
                            this@MainActivity,
                            event.journeyId
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun ShowDialogsAndBottomSheets() {
        val uiState = mainViewModel.uiState.collectAsState().value

        if (uiState.addBottomSheetOpen) {
            ShowAddJourneyBottomSheet()
        }

        val journeyToEdit = mainViewModel.contextSelectedJourney

        if (uiState.editBottomSheetOpen && journeyToEdit != null) {
            ShowEditJourneyBottomSheet(journeyToEdit)
        }

        if (uiState.contextMenuSheetOpen) {
            ShowContextMenuBottomSheet()
        }

        if (uiState.confirmDeleteDialogShowing) {
            ShowDeleteConfirmDialog()
        }

        if (uiState.confirmResetDialogShowing) {
            ShowResetProgressConfirmDialog()
        }
    }

    @Composable
    private fun ShowAddJourneyBottomSheet() {
        AddJourneyBottomSheet(
            onDismissRequest = { mainViewModel.onEvent(UIEvent.OnAddSheetDismiss) },
            onJourneyCreatedRequest = {
                mainViewModel.onEvent(UIEvent.OnJourneyCreated(it))
                mainViewModel.onEvent(UIEvent.OnAddSheetDismiss)
            })
    }

    @Composable
    private fun ShowEditJourneyBottomSheet(journeyToEdit: Journey) {
        EditJourneyBottomSheet(
            journey = journeyToEdit,
            onDismissRequest = { mainViewModel.onEvent(UIEvent.OnEditSheetDismiss) },
            onJourneyEditedRequest = {
                mainViewModel.onEvent(UIEvent.OnJourneyEdited(it))
                mainViewModel.onEvent(UIEvent.OnEditSheetDismiss)
            }
        )
    }

    @Composable
    private fun ShowContextMenuBottomSheet() {
        mainViewModel.contextSelectedJourney?.let {
            JourneyContextMenuBottomSheet(
                journey = it,
                onDismissRequest = { mainViewModel.onEvent(UIEvent.OnContextMenuSheetDismiss) },
                onMenuOptionClick = { journey: Journey, option: JourneyContextMenuOption ->
                    when (option) {
                        JourneyContextMenuOption.DELETE ->
                            mainViewModel.onEvent(UIEvent.OnJourneyDeleteClick)

                        JourneyContextMenuOption.EDIT -> {
                            mainViewModel.onEvent(UIEvent.OnJourneyEditClick(journey))
                            mainViewModel.onEvent(UIEvent.OnContextMenuSheetDismiss)
                        }

                        JourneyContextMenuOption.RESET ->
                            mainViewModel.onEvent(UIEvent.OnGoalResetClick)
                    }
                })
        }
    }

    @Composable
    private fun ShowDeleteConfirmDialog() {
        val journeyToDelete = mainViewModel.contextSelectedJourney ?: return

        ConfirmDialog(
            onConfirmListener = {
                mainViewModel.onEvent(UIEvent.OnJourneyDeleted(journeyToDelete))
            },
            onDismissListener = {
                mainViewModel.onEvent(UIEvent.OnDeleteJourneyDialogDismiss)
            },
            title = stringResource(R.string.delete_journey_dialog_title),
            text = stringResource(R.string.delete_journey_dialog_text) + " ${journeyToDelete.name}?",
            icon = R.drawable.ic_delete_24,
            iconTint = colorResource(R.color.color_error),
        )
    }

    @Composable
    private fun ShowResetProgressConfirmDialog() {
        val journeyToReset = mainViewModel.contextSelectedJourney ?: return

        ConfirmDialog(
            onConfirmListener = {
                mainViewModel.onEvent(UIEvent.OnGoalReset(journeyToReset))
            },
            onDismissListener = {
                mainViewModel.onEvent(UIEvent.OnResetJourneyDialogDismiss)
            },
            title = stringResource(R.string.goal_reset_progress),
            text = stringResource(R.string.reset_journey_goal_dialog_text) + " ${journeyToReset.name}?",
            icon = R.drawable.ic_refresh_24
        )
    }
}