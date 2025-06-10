package com.example.journeysapp.ui.main.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.ui.theme.StandardSpacer
import com.example.journeysapp.ui.theme.standardPadding

@Composable
fun MainBottomBar(
    onAddClick: () -> Unit, modifier: Modifier = Modifier
) {
    BottomAppBar(
        actions = {
        // TODO those are mocked, to be replaced by actual actions
        IconButton(onClick = { /* do something */ }) {
            Icon(Icons.Filled.Check, contentDescription = "Localized description")
        }
        IconButton(onClick = { /* do something */ }) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Localized description",
            )
        }
        IconButton(onClick = { /* do something */ }) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Localized description",
            )
        }
        IconButton(onClick = { /* do something */ }) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Localized description",
            )
        }
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Icon(Icons.Filled.Add, "Add")
        }
    }, modifier = modifier
    )
}

@Composable
fun JourneysLazyColumn(
    journeyList: List<Journey>, onMoreMenuClicked: (Journey) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = journeyList) { journey ->
            JourneyRow(
                item = journey,
                onMoreMenuClicked = onMoreMenuClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = standardPadding)
            )
        }
    }
}

@Composable
fun JourneyRow(item: Journey, onMoreMenuClicked: (Journey) -> Unit, modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_test),
            contentDescription = item.name + " icon",
        )

        StandardSpacer()

        item.name?.let { Text(it) }

        Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.ic_more_vert_24),
            contentDescription = item.name + " more menu",
            modifier = Modifier.clickable { onMoreMenuClicked(item) })
    }
}
