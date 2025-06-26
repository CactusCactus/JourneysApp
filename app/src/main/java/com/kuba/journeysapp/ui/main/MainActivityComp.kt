package com.kuba.journeysapp.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kuba.journeysapp.R
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.data.model.internal.SortMode
import com.kuba.journeysapp.ui.common.StepsOverflowProgressIndicator
import com.kuba.journeysapp.ui.theme.StandardSpacer
import com.kuba.journeysapp.ui.theme.standardPadding
import com.kuba.journeysapp.ui.theme.standardQuarterPadding
import com.kuba.journeysapp.util.goalSummaryString
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium
            )
        })
}

@Composable
fun MainBottomBar(
    onAddClick: () -> Unit,
    onSortModeChanged: (SortMode) -> Unit,
    modifier: Modifier = Modifier,
    initialSortMode: SortMode = SortMode.ALPHABETICALLY_ASC
) {
    BottomAppBar(
        actions = {
            BottomBarSortIcon(
                onSortModeChanged = onSortModeChanged,
                initialSortMode = initialSortMode
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        modifier = modifier
    )
}

@Composable
fun BottomBarSortIcon(
    onSortModeChanged: (SortMode) -> Unit,
    modifier: Modifier = Modifier,
    initialSortMode: SortMode = SortMode.ALPHABETICALLY_ASC
) {
    val sortModes = SortMode.entries.toTypedArray()

    var showSortModeText by remember { mutableStateOf(false) }
    var currentSortMode by remember { mutableStateOf(initialSortMode) }

    // This fixes problem with field keeping the old value at startup
    currentSortMode = initialSortMode


    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                val sortIndex = sortModes.indexOf(currentSortMode)

                currentSortMode = if (sortIndex == sortModes.lastIndex) {
                    sortModes.first()
                } else {
                    sortModes[sortIndex + 1]
                }

                onSortModeChanged(currentSortMode)
                showSortModeText = true
            }, modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .zIndex(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sort_24),
                contentDescription = "Sort button",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        if (showSortModeText) {
            LaunchedEffect(currentSortMode) {
                delay(1000L)
                showSortModeText = false
            }
        }

        AnimatedVisibility(
            visible = showSortModeText,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            Text(
                currentSortMode.toString(LocalContext.current),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun JourneysLazyColumn(
    journeyList: List<Journey>,
    onIncrementClicked: (Journey) -> Unit,
    onClick: (Journey) -> Unit,
    onLongPress: (Journey) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = journeyList) { journey ->
            JourneyRow(
                item = journey,
                onIncrementClicked = onIncrementClicked,
                onClick = onClick,
                onLongPress = onLongPress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = standardPadding)
                    .animateItem()
            )
        }
    }
}

@Composable
fun JourneyRow(
    item: Journey,
    onIncrementClicked: (Journey) -> Unit,
    onClick: (Journey) -> Unit,
    onLongPress: (Journey) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick(item) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress(item)
                }
            )
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CardDefaults.shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.icon.iconId),
                contentDescription = item.name + " icon",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        StandardSpacer()

        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
            Text(item.name)

            if (item.goal.isCompleted()) {
                Text(
                    item.goal.goalType.toCompletionString(LocalContext.current),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                val goalString = item.goal.goalSummaryString(LocalContext.current)

                Text(
                    text = goalString,
                    style = MaterialTheme.typography.labelMedium
                )

                StepsOverflowProgressIndicator(
                    checkedSteps = item.goal.progress,
                    maxSteps = item.goal.value,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        StandardSpacer()

        AnimatedVisibility(
            visible = !item.goal.isCompleted(),
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            IconButton(
                enabled = !item.goal.isCompleted(),
                onClick = { onIncrementClicked(item) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_circle_24),
                    contentDescription = item.name + " increment",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(standardQuarterPadding)
                )
            }
        }
    }
}
