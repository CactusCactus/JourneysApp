package com.example.journeysapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.ui.theme.JourneysAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JourneysAppTheme {
                Scaffold(
                    bottomBar = { MainBottomBar() }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    JourneysLazyColumn(
                        journeyList = generateTestJourneys(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun generateTestJourneys() = listOf(
        Journey("Quitting smoking"), Journey("Running"), Journey("Reading")
    )
}