package com.example.journeysapp.ui.bottomSheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Goal
import com.example.journeysapp.data.model.GoalFrequency
import com.example.journeysapp.data.model.GoalType
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyIcon
import com.example.journeysapp.ui.theme.StandardHalfSpacer
import com.example.journeysapp.ui.theme.StandardQuarterSpacer
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
        onConfirmRequest = { name: String, icon: JourneyIcon, goal: Goal ->
            onJourneyCreatedRequest(Journey(name = name, icon = icon, goal = goal))
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
        title = stringResource(R.string.edit_new_journey_title) + " ${journey.name}",
        confirmButtonText = stringResource(R.string.confirm),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = { name: String, icon: JourneyIcon, goal: Goal ->
            onJourneyEditedRequest(journey.copy(name = name, icon = icon, goal = goal))
        },
        journeyName = journey.name,
        journeyIcon = journey.icon,
        journeyGoal = journey.goal,
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModifyJourneyBottomSheet(
    title: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (name: String, icon: JourneyIcon, goal: Goal) -> Unit,
    modifier: Modifier = Modifier,
    journeyIcon: JourneyIcon = JourneyIcon.SMILE,
    journeyName: String = "",
    journeyGoal: Goal = Goal(
        goalType = GoalType.LESS_THAN,
        value = 10,
        unit = stringResource(R.string.new_journey_goal_value_label),
        goalFrequency = GoalFrequency.DAILY
    ),
    namePlaceholder: String = stringResource(R.string.add_new_journey_hint),
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(standardPadding)) {

            var selectedJourneyName by remember { mutableStateOf(journeyName) }
            var selectedIcon by remember { mutableStateOf(journeyIcon) }
            var isIconPickerShowing by remember { mutableStateOf(false) }

            Text(text = title, style = MaterialTheme.typography.displaySmall)

            StandardSpacer()

            // Icon and name input
            Text(
                text = stringResource(R.string.new_journey_name_description),
                style = MaterialTheme.typography.bodyMedium
            )

            StandardHalfSpacer()

            IconNameInputRow(
                journeyName = selectedJourneyName,
                journeyNamePlaceholder = namePlaceholder,
                selectedIcon = selectedIcon,
                onIconClicked = { isIconPickerShowing = !isIconPickerShowing },
                onNameValueChanged = { value: String -> selectedJourneyName = value })

            AnimatedVisibility(isIconPickerShowing) {
                Column {
                    StandardSpacer()
                    JourneyIconPicker {
                        isIconPickerShowing = false
                        selectedIcon = it
                    }
                }
            }

            StandardSpacer()

            var currentJourneyGoal by remember { mutableStateOf(journeyGoal) }

            GoalFrequencyInputRow(
                currentGoal = currentJourneyGoal,
                onGoalTypeChanged = {
                    currentJourneyGoal = currentJourneyGoal.copy(goalType = it)
                },
                onValueChanged = {
                    currentJourneyGoal = currentJourneyGoal.copy(value = it)
                },
                onFrequencyChanged = {
                    currentJourneyGoal = currentJourneyGoal.copy(goalFrequency = it)
                },
                onUnitChanged = {
                    currentJourneyGoal = currentJourneyGoal.copy(unit = it)
                }
            )

            StandardSpacer()

            Button(
                onClick = {
                    onConfirmRequest(
                        selectedJourneyName,
                        selectedIcon,
                        currentJourneyGoal
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(confirmButtonText)
            }

            StandardSpacer()
        }
    }
}

@Composable
private fun GoalFrequencyInputRow(
    currentGoal: Goal,
    onGoalTypeChanged: (GoalType) -> Unit,
    onValueChanged: (Int) -> Unit,
    onUnitChanged: (String) -> Unit,
    onFrequencyChanged: (GoalFrequency) -> Unit
) {
    // Goal type
    Text(
        text = stringResource(R.string.new_journey_goal_description),
        style = MaterialTheme.typography.bodyMedium
    )
    StandardQuarterSpacer()

    GoalTypeSegmentedButtonRow(
        initialGoalType = currentGoal.goalType,
        onGoalTypeChanged = onGoalTypeChanged
    )

    StandardSpacer()

    // Goal value
    GoalValueInputRow(
        initialValue = currentGoal.value,
        onValueChanged = onValueChanged,
        onUnitChanged = onUnitChanged
    )

    StandardSpacer()

    // Goal frequency
    Text(
        text = stringResource(R.string.new_journey_check_in_description),
        style = MaterialTheme.typography.bodyMedium
    )
    StandardQuarterSpacer()

    GoalFrequencySegmentedButtonRow(
        iniGoalFrequency = currentGoal.goalFrequency,
        onFrequencyChanged = onFrequencyChanged
    )
}

@Composable
private fun GoalFrequencySegmentedButtonRow(
    iniGoalFrequency: GoalFrequency,
    onFrequencyChanged: (GoalFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(iniGoalFrequency.ordinal) }
    val options = GoalFrequency.entries.map { it.toString(LocalContext.current) }

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    onFrequencyChanged(GoalFrequency.entries[index])
                    selectedIndex = index
                },
                selected = index == selectedIndex,
                label = { Text(option) }
            )
        }
    }
}

@Composable
private fun GoalTypeSegmentedButtonRow(
    initialGoalType: GoalType,
    onGoalTypeChanged: (GoalType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(initialGoalType.ordinal) }
    val options = GoalType.entries.map { it.toString(LocalContext.current) }

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    onGoalTypeChanged(GoalType.entries[index])
                    selectedIndex = index
                },
                selected = index == selectedIndex,
                label = { Text(option) }
            )
        }
    }
}

@Composable
private fun GoalValueInputRow(
    initialValue: Int,
    onValueChanged: (Int) -> Unit,
    onUnitChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var numericStringValue by remember { mutableStateOf(initialValue.toString()) }
    var isError by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = numericStringValue,
            onValueChange = {
                val numericValue = it.toIntOrNull() ?: 0
                isError = !validateGoalValue(numericValue)

                if (it.isDigitsOnly()) {
                    numericStringValue = it
                }

                if (!isError) {
                    onValueChanged(it.toIntOrNull() ?: 0)
                }
            },
            placeholder = { Text(stringResource(R.string.new_journey_goal_value_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = isError,
            singleLine = true,
            modifier = Modifier
                .widthIn(min = 24.dp)
                .weight(1f)
        )

        StandardHalfSpacer()

        val defaultUnit = stringResource(R.string.new_journey_goal_value_label)
        var goalUnit by remember { mutableStateOf(defaultUnit) }

        TextField(
            value = goalUnit,
            onValueChange = {
                goalUnit = it

                if (it.isNotBlank()) {
                    onUnitChanged(it)
                } else {
                    onUnitChanged(defaultUnit)
                }
            },
            placeholder = { Text(text = defaultUnit) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier
                .widthIn(min = 24.dp)
                .weight(2f)
        )
    }
}

private fun validateGoalValue(value: Int): Boolean {
    return value > 0
}

@Composable
private fun IconNameInputRow(
    selectedIcon: JourneyIcon,
    journeyName: String,
    journeyNamePlaceholder: String,
    onIconClicked: () -> Unit,
    onNameValueChanged: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        JourneyIconButton(
            icon = selectedIcon,
            onClick = onIconClicked
        )

        StandardSpacer()

        OutlinedTextField(
            value = journeyName,
            onValueChange = onNameValueChanged,
            placeholder = { Text(journeyNamePlaceholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun JourneyIconPicker(onIconPicked: (JourneyIcon) -> Unit) {
    val iconResList = JourneyIcon.entries.toList()

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
                JourneyIconButton(icon = it, onClick = { onIconPicked(it) })
            }
        }
    }
}

@Composable
private fun JourneyIconButton(icon: JourneyIcon, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(52.dp)
            .clip(CardDefaults.shape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon.iconId),
            contentDescription = "Journey icon",
            modifier = Modifier
                .fillMaxSize()
                .padding(standardHalfPadding),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}