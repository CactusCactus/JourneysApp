package com.example.journeysapp.ui.bottomSheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyContextMenuOption
import com.example.journeysapp.ui.common.StandardListRow
import com.example.journeysapp.ui.theme.StandardSpacer
import com.example.journeysapp.ui.theme.standardPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyContextMenuBottomSheet(
    journey: Journey,
    onDismissRequest: () -> Unit,
    onMenuOptionClick: (Journey, JourneyContextMenuOption) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest, modifier = modifier
    ) {
        Column {
            Text(
                text = journey.name,
                style = MaterialTheme.typography.displaySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(standardPadding)
            )

            StandardSpacer()

            LazyColumn {
                items(JourneyContextMenuOption.entries.toTypedArray()) {
                    JourneyContextMenuRow(it, Modifier.clickable {
                        onMenuOptionClick.invoke(journey, it)
                    })
                }
            }
        }
    }
}

@Composable
fun JourneyContextMenuRow(option: JourneyContextMenuOption, modifier: Modifier = Modifier) {
    StandardListRow(
        label = stringResource(option.label),
        icon = option.icon,
        iconTint = option.tint,
        modifier = modifier,
    )
}