package com.example.journeysapp.ui.main.composables.bottomSheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.ui.theme.StandardSpacer
import com.example.journeysapp.ui.theme.standardPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewJourneyBottomSheet(
    onDismissRequest: () -> Unit, onJourneyCreated: (Journey) -> Unit, modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest, modifier = modifier
    ) {
        Column(modifier = Modifier.padding(standardPadding)) {
            Text(stringResource(R.string.add_new_journey_title))
            val journeyName = remember { mutableStateOf("") }

            StandardSpacer()

            TextField(
                value = journeyName.value,
                onValueChange = { value: String -> journeyName.value = value })

            StandardSpacer()

            Button(onClick = {
                onJourneyCreated(Journey(name = journeyName.value))
            }) {
                Text(stringResource(R.string.create))
            }
        }
    }
}