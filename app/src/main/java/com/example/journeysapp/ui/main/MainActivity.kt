package com.example.journeysapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.ui.theme.JourneysAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

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
                        journeyList = generateTestJourneys(),
                        modifier = Modifier.padding(innerPadding)
                    )

                    if(mainViewModel.uiState.collectAsState().value.addBottomSheetOpen) {
                        ModalBottomSheet(onDismissRequest = {
                            mainViewModel.onEvent(UIEvent.OnAddSheetDismiss)
                        }) {
                            Text("KURWA ALE MIE PISZCZEL BOI")
                        }
                    }
                }
            }
        }
    }

    private fun generateTestJourneys() = listOf(
        Journey("Quitting smoking"), Journey("Running"), Journey("Reading")
    )
}