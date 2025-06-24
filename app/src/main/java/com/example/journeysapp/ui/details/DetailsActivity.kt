package com.example.journeysapp.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.ui.details.DetailsViewModel.UIState
import com.example.journeysapp.ui.theme.AppTheme
import com.example.journeysapp.ui.theme.standardPadding
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


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                Scaffold(topBar = {
                    TopAppBar(
                        title = {
                            val uiState = viewModel.uiState.collectAsState().value

                            Text(
                                text = when (uiState) {
                                    is UIState.JourneyFetched -> uiState.journey.name
                                    else -> stringResource(R.string.app_name)
                                },
                                style = MaterialTheme.typography.displayMedium
                            )
                        })
                }) { paddingValues ->
                    val uiState = viewModel.uiState.collectAsState().value
                    DetailsScreen(uiState, paddingValues)
                }
            }
        }
    }

    @Composable
    private fun DetailsScreen(uiState: UIState, paddingValues: PaddingValues) {
        when (uiState) {
            UIState.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )

            UIState.Error ->
                Text(
                    stringResource(R.string.error_fetching_journey),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )

            is UIState.JourneyFetched -> JourneyHeader(
                uiState.journey,
                paddingValues
            )
        }
    }

    @Composable
    private fun JourneyHeader(
        journey: Journey,
        paddingValues: PaddingValues,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(standardPadding)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CardDefaults.shape
                    ),
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
        }
    }
}