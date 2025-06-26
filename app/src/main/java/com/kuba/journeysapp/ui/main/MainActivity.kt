package com.kuba.journeysapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuba.journeysapp.R
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.data.model.internal.JourneyContextMenuOption
import com.kuba.journeysapp.ui.bottomSheets.AddJourneyBottomSheet
import com.kuba.journeysapp.ui.bottomSheets.EditJourneyBottomSheet
import com.kuba.journeysapp.ui.bottomSheets.JourneyContextMenuBottomSheet
import com.kuba.journeysapp.ui.common.ConfirmDialog
import com.kuba.journeysapp.ui.details.DetailsActivity
import com.kuba.journeysapp.ui.theme.AppTheme
import com.kuba.journeysapp.ui.theme.standardPadding
import com.kuba.journeysapp.util.askNotificationPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        askNotificationPermission(
            this,
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                // We don't do anything for now
            }
        )

        setContent {
            AppTheme {
                Scaffold(
                    topBar = { MainTopBar() },
                    bottomBar = {
                        MainBottomBar(
                            onAddClick = {
                                viewModel.onEvent(UIEvent.OnJourneyAddClick)
                            },
                            onSortModeChanged = {
                                viewModel.onEvent(UIEvent.SortModeChanged(it))
                            },
                            initialSortMode = viewModel.uiState.collectAsState().value.sortMode
                        )
                    }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AnimatedVisibility(
                        viewModel.journeyList.isEmpty(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        EmptyStateScreen()
                    }

                    JourneysLazyColumn(
                        journeyList = viewModel.journeyList.toList(),
                        onLongPress = {
                            viewModel.onEvent(UIEvent.OnJourneyContextMenuClick(it))
                        },
                        onClick = {
                            viewModel.onEvent(UIEvent.OnJourneyDetailsClick(it))
                        },
                        onIncrementClicked = {
                            viewModel.onEvent(UIEvent.OnGoalIncremented(it))
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
            viewModel.navEvent.collect { event ->
                when (event) {
                    is NavEvent.ToJourneyDetails -> {
                        startActivity(
                            DetailsActivity.newIntent(this@MainActivity, event.journeyId)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ShowDialogsAndBottomSheets() {
        val uiState = viewModel.uiState.collectAsState().value

        if (uiState.addBottomSheetOpen) {
            ShowAddJourneyBottomSheet()
        }

        val journeyToEdit = viewModel.contextSelectedJourney

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
            onDismissRequest = { viewModel.onEvent(UIEvent.OnAddSheetDismiss) },
            onJourneyCreatedRequest = {
                viewModel.onEvent(UIEvent.OnJourneyCreated(it))
                viewModel.onEvent(UIEvent.OnAddSheetDismiss)
            })
    }

    @Composable
    private fun ShowEditJourneyBottomSheet(journeyToEdit: Journey) {
        EditJourneyBottomSheet(
            journey = journeyToEdit,
            onDismissRequest = { viewModel.onEvent(UIEvent.OnEditSheetDismiss) },
            onJourneyEditedRequest = {
                viewModel.onEvent(UIEvent.OnJourneyEdited(it))
                viewModel.onEvent(UIEvent.OnEditSheetDismiss)
            }
        )
    }

    @Composable
    private fun ShowContextMenuBottomSheet() {
        viewModel.contextSelectedJourney?.let {
            JourneyContextMenuBottomSheet(
                journey = it,
                onDismissRequest = { viewModel.onEvent(UIEvent.OnContextMenuSheetDismiss) },
                onMenuOptionClick = { journey: Journey, option: JourneyContextMenuOption ->
                    when (option) {
                        JourneyContextMenuOption.DELETE ->
                            viewModel.onEvent(UIEvent.OnJourneyDeleteClick)

                        JourneyContextMenuOption.EDIT -> {
                            viewModel.onEvent(UIEvent.OnJourneyEditClick(journey))
                            viewModel.onEvent(UIEvent.OnContextMenuSheetDismiss)
                        }

                        JourneyContextMenuOption.RESET ->
                            viewModel.onEvent(UIEvent.OnGoalResetClick)
                    }
                })
        }
    }

    @Composable
    private fun ShowDeleteConfirmDialog() {
        val journeyToDelete = viewModel.contextSelectedJourney ?: return

        ConfirmDialog(
            onConfirmListener = {
                viewModel.onEvent(UIEvent.OnJourneyDeleted(journeyToDelete))
            },
            onDismissListener = {
                viewModel.onEvent(UIEvent.OnDeleteJourneyDialogDismiss)
            },
            title = stringResource(R.string.delete_journey_dialog_title),
            text = stringResource(R.string.delete_journey_dialog_text) + " ${journeyToDelete.name}?",
            icon = R.drawable.ic_delete_24,
            iconTint = colorResource(R.color.color_error),
        )
    }

    @Composable
    private fun ShowResetProgressConfirmDialog() {
        val journeyToReset = viewModel.contextSelectedJourney ?: return

        ConfirmDialog(
            onConfirmListener = {
                viewModel.onEvent(UIEvent.OnGoalReset(journeyToReset))
            },
            onDismissListener = {
                viewModel.onEvent(UIEvent.OnResetJourneyDialogDismiss)
            },
            title = stringResource(R.string.goal_reset_progress),
            text = stringResource(R.string.reset_journey_goal_dialog_text) + " ${journeyToReset.name}?",
            icon = R.drawable.ic_refresh_24
        )
    }

    @Composable
    private fun EmptyStateScreen(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = standardPadding)
            ) {
                Text(
                    text = stringResource(R.string.empty_state_label),
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Icon(
                    painterResource(R.drawable.ic_arrow_curved_150),
                    "Decoration arrow",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(64.dp)
                        .scale(scaleX = -1f, scaleY = 1f)
                        .rotate(180f)
                )
            }
        }

    }
}