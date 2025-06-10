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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyContextMenuOption
import com.example.journeysapp.ui.main.composables.JourneysLazyColumn
import com.example.journeysapp.ui.main.composables.MainBottomBar
import com.example.journeysapp.ui.main.composables.MainTopBar
import com.example.journeysapp.ui.main.composables.bottomSheets.AddNewJourneyBottomSheet
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
                    topBar = { MainTopBar() },
                    bottomBar = {
                        MainBottomBar(onAddClick = {
                            mainViewModel.onEvent(UIEvent.OnJourneyAddClick)
                        })
                    }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    JourneysLazyColumn(
                        journeyList = mainViewModel.journeyList.toList(), onMoreMenuClicked = {
                            mainViewModel.onEvent(UIEvent.OnJourneyContextMenuClick(it))
                        }, modifier = Modifier.padding(innerPadding)
                    )

                    SetUpAddJourneyBottomSheet()
                    SetUpContextMenuBottomSheet()
                }
            }
        }
    }

    @Composable
    private fun SetUpAddJourneyBottomSheet() {
        if (mainViewModel.uiState.collectAsState().value.addBottomSheetOpen) {
            AddNewJourneyBottomSheet(
                onDismissRequest = { mainViewModel.onEvent(UIEvent.OnAddSheetDismiss) },
                onJourneyCreated = {
                    mainViewModel.onEvent(UIEvent.OnJourneyCreated(it))
                    mainViewModel.onEvent(UIEvent.OnAddSheetDismiss)
                })
        }
    }

    @Composable
    private fun SetUpContextMenuBottomSheet() {
        if (mainViewModel.uiState.collectAsState().value.contextMenuSheetOpen) {
            mainViewModel.contextSelectedJourney?.let {
                JourneyContextMenuBottomSheet(
                    journey = it,
                    onDismissRequest = { mainViewModel.onEvent(UIEvent.OnContextMenuSheetDismiss) },
                    onMenuOptionClick = { journey: Journey, option: JourneyContextMenuOption ->
                        when (option) {
                            JourneyContextMenuOption.DELETE -> {
                                mainViewModel.onEvent(UIEvent.OnJourneyDeleted(journey))
                                mainViewModel.onEvent(UIEvent.OnContextMenuSheetDismiss)
                            }
                        }
                    })
            }
        }
    }
}