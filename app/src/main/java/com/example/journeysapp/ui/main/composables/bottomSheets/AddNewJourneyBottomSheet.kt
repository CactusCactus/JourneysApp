package com.example.journeysapp.ui.main.composables.bottomSheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyIcons
import com.example.journeysapp.ui.theme.DoubleStandardSpacer
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
            Text(
                stringResource(R.string.add_new_journey_title),
                style = MaterialTheme.typography.displaySmall
            )
            val journeyName = remember { mutableStateOf("") }

            StandardSpacer()
            val iconResList = JourneyIcons.entries.toList()

            var isIconPickerShowing by remember { mutableStateOf(false) }

            AnimatedVisibility(isIconPickerShowing) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 48.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(iconResList) {
                        IconButton(
                            onClick = { isIconPickerShowing = false }, modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(it.icon),
                                contentDescription = "Journey icon",
                                modifier = modifier.fillMaxSize(),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(!isIconPickerShowing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        isIconPickerShowing = true
                    }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_smile_24),
                            contentDescription = "Journey icon",
                            modifier = modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    StandardSpacer()

                    OutlinedTextField(
                        value = journeyName.value,
                        onValueChange = { value: String -> journeyName.value = value },
                        placeholder = { Text(stringResource(R.string.add_new_journey_hint)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            StandardSpacer()

            Button(
                onClick = { onJourneyCreated(Journey(name = journeyName.value)) }) {
                Text(stringResource(R.string.create))
            }

            DoubleStandardSpacer()
        }
    }
}