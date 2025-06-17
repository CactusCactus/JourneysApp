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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.journeysapp.ui.theme.StandardSpacer
import com.example.journeysapp.ui.theme.standardHalfPadding
import com.example.journeysapp.ui.theme.standardPadding
import com.example.journeysapp.ui.theme.standardQuarterPadding

@Composable
fun AddJourneyBottomSheet(
    onDismissRequest: () -> Unit,
    onJourneyCreatedRequest: (Journey) -> Unit,
    modifier: Modifier = Modifier
) {
    ModifyJourneyBottomSheet(
        title = stringResource(R.string.add_new_journey_title),
        confirmButtonText = stringResource(R.string.create),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = { name: String, icon: Int ->
            onJourneyCreatedRequest(Journey(name = name))
        },
        modifier = modifier
    )
}

@Composable
fun EditJourneyBottomSheet(
    journey: Journey,
    onDismissRequest: () -> Unit,
    onJourneyEditedRequest: (Journey) -> Unit,
    modifier: Modifier = Modifier
) {
    ModifyJourneyBottomSheet(
        title = stringResource(R.string.edit_new_journey_title) + " $journey",
        confirmButtonText = stringResource(R.string.confirm),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = { name: String, icon: Int ->
            onJourneyEditedRequest(Journey(name = name))
        },
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModifyJourneyBottomSheet(
    title: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (name: String, icon: Int) -> Unit,
    modifier: Modifier = Modifier,
    namePlaceholder: String = stringResource(R.string.add_new_journey_hint),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest, modifier = modifier
    ) {
        Column(modifier = Modifier.padding(standardPadding)) {
            Text(text = title, style = MaterialTheme.typography.displaySmall)

            val journeyName = remember { mutableStateOf("") }

            StandardSpacer()

            var isIconPickerShowing by remember { mutableStateOf(false) }
            var currentIconId by remember { mutableIntStateOf(R.drawable.ic_smile_24) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { isIconPickerShowing = !isIconPickerShowing },
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(currentIconId),
                        contentDescription = "Journey icon",
                        modifier = modifier
                            .fillMaxSize()
                            .padding(standardHalfPadding),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                StandardSpacer()

                OutlinedTextField(
                    value = journeyName.value,
                    onValueChange = { value: String -> journeyName.value = value },
                    placeholder = { Text(namePlaceholder) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(isIconPickerShowing) {

                Column {
                    StandardSpacer()

                    JourneyIconPicker {
                        isIconPickerShowing = false
                        currentIconId = it
                    }
                }
            }

            StandardSpacer()

            Button(
                onClick = { onConfirmRequest(journeyName.value, currentIconId) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(confirmButtonText)
            }

            StandardSpacer()
        }
    }
}

@Composable
private fun JourneyIconPicker(onIconPicked: (Int) -> Unit) {
    val iconResList = JourneyIcons.entries.toList()

    Card {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 48.dp),
            verticalArrangement = Arrangement.spacedBy(standardQuarterPadding),
            horizontalArrangement = Arrangement.spacedBy(standardQuarterPadding),
            modifier = Modifier
                .padding(standardPadding)
                .fillMaxWidth()
        ) {
            items(iconResList) {
                IconButton(
                    onClick = { onIconPicked(it.icon) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        painter = painterResource(it.icon),
                        contentDescription = "Journey icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(standardHalfPadding),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}