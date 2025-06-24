package com.example.journeysapp.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyContextMenuOption
import com.example.journeysapp.ui.bottomSheets.EditJourneyBottomSheet
import com.example.journeysapp.ui.bottomSheets.JourneyContextMenuBottomSheet
import com.example.journeysapp.ui.common.ConfirmDialog
import com.example.journeysapp.ui.common.StepsProgressIndicator
import com.example.journeysapp.ui.details.DetailsViewModel.UIEvent
import com.example.journeysapp.ui.details.DetailsViewModel.UIEvent.OnEditSheetDismiss
import com.example.journeysapp.ui.details.DetailsViewModel.UIEvent.OnJourneyEdited
import com.example.journeysapp.ui.details.DetailsViewModel.UIState
import com.example.journeysapp.ui.theme.AppTheme
import com.example.journeysapp.ui.theme.StandardHalfSpacer
import com.example.journeysapp.ui.theme.StandardSpacer
import com.example.journeysapp.ui.theme.standardPadding
import com.example.journeysapp.ui.theme.standardQuarterPadding
import com.example.journeysapp.util.goalSummaryString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : ComponentActivity() {
    private val viewModel: DetailsViewModel by viewModels()

    companion object {
        const val EXTRA_JOURNEY_ID = "journeyId"

        fun newIntent(context: Context, journeyId: Long) =
            Intent(context, DetailsActivity::class.java).apply {
                putExtra(EXTRA_JOURNEY_ID, journeyId)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                Scaffold(topBar = { DetailsTopBar() }) { paddingValues ->
                    val uiState = viewModel.uiState.collectAsState().value

                    DetailsScreen(uiState, paddingValues)

                    ShowDialogsAndBottomSheets(uiState)

                    ObserveNavigationEvents()
                }
            }
        }
    }

    @Composable
    private fun ShowDialogsAndBottomSheets(uiState: UIState) {
        val journey = uiState.journey

        if (journey != null) {
            if (uiState.contextMenuSheetOpen) {
                ShowContextMenuBottomSheet(journey)
            }

            if (uiState.confirmDeleteDialogShowing) {
                ShowDeleteConfirmDialog(journey)
            }

            if (uiState.confirmResetDialogShowing) {
                ShowResetProgressConfirmDialog(journey)
            }

            if (uiState.editSheetShowing) {
                ShowEditJourneyBottomSheet(journey)
            }
        }
    }

    @Composable
    private fun ObserveNavigationEvents() {
        LaunchedEffect(key1 = true) {
            viewModel.navEvent.collect { event ->
                when (event) {
                    is DetailsViewModel.NavEvent.Finish -> {
                        setResult(event.resultCode)
                        finish()
                    }
                }
            }
        }
    }

    @Composable
    private fun ShowDeleteConfirmDialog(journey: Journey) {
        ConfirmDialog(
            onConfirmListener = {
                viewModel.onEvent(UIEvent.OnJourneyDeleted)
            },
            onDismissListener = {
                viewModel.onEvent(UIEvent.OnJourneyDeleteDialogDismiss)
            },
            title = stringResource(R.string.delete_journey_dialog_title),
            text = stringResource(R.string.delete_journey_dialog_text) + " ${journey.name}?",
            icon = R.drawable.ic_delete_24,
            iconTint = colorResource(R.color.color_error),
        )
    }

    @Composable
    private fun ShowResetProgressConfirmDialog(journey: Journey) {
        ConfirmDialog(
            onConfirmListener = {
                viewModel.onEvent(UIEvent.OnGoalReset)
                setResult(RESULT_OK)
            },
            onDismissListener = {
                viewModel.onEvent(UIEvent.OnGoalResetDialogDismiss)
            },
            title = stringResource(R.string.goal_reset_progress),
            text = stringResource(R.string.reset_journey_goal_dialog_text) + " ${journey.name}?",
            icon = R.drawable.ic_refresh_24
        )
    }

    @Composable
    private fun ShowEditJourneyBottomSheet(journeyToEdit: Journey) {
        EditJourneyBottomSheet(
            journey = journeyToEdit,
            onDismissRequest = { viewModel.onEvent(OnEditSheetDismiss) },
            onJourneyEditedRequest = {
                viewModel.onEvent(OnJourneyEdited(it))
                setResult(RESULT_OK)
            },
            startWithOpenedIconsPicker =
                viewModel.uiState.collectAsState().value.editSheetIconPickerShowing
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DetailsTopBar() {
        TopAppBar(
            title = {
                val uiState = viewModel.uiState.collectAsState().value

                Text(
                    text = uiState.journey?.name
                        ?: stringResource(R.string.details_title_default),
                    style = MaterialTheme.typography.displayMedium
                )
            },
            actions = {
                IconButton(onClick = {
                    viewModel.onEvent(UIEvent.OnContextMenuSheetOpen)
                }) {
                    Icon(
                        painterResource(R.drawable.ic_more_vert_24),
                        "Open context menu",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(standardQuarterPadding)
                    )
                }
            })
    }

    @Composable
    private fun DetailsScreen(uiState: UIState, paddingValues: PaddingValues) {
        uiState.journey?.let {
            JourneyHeader(it, paddingValues)
        }
    }

    @Composable
    private fun JourneyHeader(
        journey: Journey,
        paddingValues: PaddingValues,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(standardPadding)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CardDefaults.shape
                        )
                        .clickable { viewModel.onEvent(UIEvent.OnContextMenuEditClicked(true)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(journey.icon.iconId),
                        contentDescription = journey.name + " icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(standardPadding)
                            .fillMaxSize()
                    )
                }

                StandardHalfSpacer()

                Text(
                    text = stringResource(R.string.currently_goal_progress),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${journey.goal.progress} / ${journey.goal.value}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                StandardSpacer()

                Button(onClick = {
                    viewModel.onEvent(UIEvent.OnGoalIncremented)
                    setResult(RESULT_OK)
                }) {
                    Icon(painterResource(R.drawable.ic_add_24), "Increment goal")
                }

                OutlinedButton(onClick = {
                    viewModel.onEvent(UIEvent.OnGoalDecremented)
                    setResult(RESULT_OK)
                }) {
                    Icon(painterResource(R.drawable.ic_remove_24), "Decrement goal")
                }
            }

            StandardSpacer()

            Column {
                Text(
                    text = stringResource(R.string.goal_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    journey.goal.goalSummaryString(LocalContext.current),
                    style = MaterialTheme.typography.labelMedium
                )

                StandardHalfSpacer()

                StepsProgressIndicator(
                    checkedSteps = journey.goal.progress,
                    maxSteps = journey.goal.value
                )
            }

        }
    }

    @Composable
    private fun ShowContextMenuBottomSheet(journey: Journey) {
        JourneyContextMenuBottomSheet(
            journey = journey,
            onDismissRequest = { viewModel.onEvent(UIEvent.OnContextMenuSheetDismiss) },
            onMenuOptionClick = { journey: Journey, option: JourneyContextMenuOption ->
                when (option) {
                    JourneyContextMenuOption.DELETE ->
                        viewModel.onEvent(UIEvent.OnContextMenuDeleteClicked)

                    JourneyContextMenuOption.EDIT ->
                        viewModel.onEvent(UIEvent.OnContextMenuEditClicked())

                    JourneyContextMenuOption.RESET ->
                        viewModel.onEvent(UIEvent.OnContextMenuResetClicked)
                }
            })
    }
}
