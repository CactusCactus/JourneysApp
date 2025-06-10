package com.example.journeysapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.ui.main.addJourney.AddNewJourneyBottomSheet
import com.example.journeysapp.ui.theme.JourneysAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private val testJourneys = mutableStateListOf(*generateTestJourneys().toTypedArray())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JourneysAppTheme {
                Scaffold(
                    bottomBar = {
                        MainBottomBar(onAddClick = {
                            mainViewModel.onEvent(UIEvent.OnAddJourneyClick)
                        })
                    }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    JourneysLazyColumn(
                        journeyList = testJourneys,
                        modifier = Modifier.padding(innerPadding)
                    )

                    if(mainViewModel.uiState.collectAsState().value.addBottomSheetOpen) {
                        AddNewJourneyBottomSheet(
                            onDismissRequest = { mainViewModel.onEvent(UIEvent.OnAddSheetDismiss)},
                            onJourneyCreated = {
                                testJourneys.add(it)
                                mainViewModel.onEvent(UIEvent.OnAddSheetDismiss)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun generateTestJourneys() = listOf(
        Journey(name = "Quitting smoking"), Journey(name = "Running"), Journey(name = "Reading")
    )
}