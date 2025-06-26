package com.kuba.journeysapp.ui.bottomSheets

import android.content.Context
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
import com.kuba.journeysapp.R
import com.kuba.journeysapp.data.model.Goal
import com.kuba.journeysapp.data.model.GoalFrequency
import com.kuba.journeysapp.data.model.GoalType
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.data.model.internal.JourneyIcon
import com.kuba.journeysapp.ui.theme.StandardHalfSpacer
import com.kuba.journeysapp.ui.theme.StandardQuarterSpacer
import com.kuba.journeysapp.ui.theme.StandardSpacer
import com.kuba.journeysapp.ui.theme.standardHalfPadding
import com.kuba.journeysapp.ui.theme.standardPadding
import com.kuba.journeysapp.ui.theme.standardQuarterPadding
import com.kuba.journeysapp.util.MAX_GOAL_VALUE

@Composable
fun AddJourneyBottomSheet(
    onDismissRequest: () -> Unit,
    onJourneyCreatedRequest: (Journey) -> Unit,
    modifier: Modifier = Modifier,
    startWithOpenedIconsPicker: Boolean = false
) {
    ModifyJourneyBottomSheet(
        title = stringResource(R.string.add_new_journey_title),
        confirmButtonText = stringResource(R.string.create),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = { name: String, icon: JourneyIcon, goal: Goal ->
            onJourneyCreatedRequest(Journey(name = name, icon = icon, goal = goal))
        },
        modifier = modifier,
        startWithOpenedIconsPicker = startWithOpenedIconsPicker
    )
}

@Composable
fun EditJourneyBottomSheet(
    journey: Journey,
    onDismissRequest: () -> Unit,
    onJourneyEditedRequest: (Journey) -> Unit,
    modifier: Modifier = Modifier,
    startWithOpenedIconsPicker: Boolean = false
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
        modifier = modifier,
        startWithOpenedIconsPicker = startWithOpenedIconsPicker,
        resetWarningShowing = true
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
    startWithOpenedIconsPicker: Boolean = false,
    resetWarningShowing: Boolean = false
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
            var isIconPickerShowing by remember { mutableStateOf(startWithOpenedIconsPicker) }

            Text(text = title, style = MaterialTheme.typography.displaySmall)

            StandardSpacer()

            // Icon and name input
            Text(
                text = stringResource(R.string.new_journey_name_description),
                style = MaterialTheme.typography.bodyMedium
            )

            StandardHalfSpacer()

            var error: InputError? by remember { mutableStateOf(null) }

            IconNameInputRow(
                journeyName = selectedJourneyName,
                journeyNamePlaceholder = namePlaceholder,
                selectedIcon = selectedIcon,
                onIconClicked = { isIconPickerShowing = !isIconPickerShowing },
                onNameValueChanged = {
                    selectedJourneyName = it

                    error = if (it.isBlank()) {
                        InputError.CANT_BE_EMPTY
                    } else {
                        null
                    }
                },
                isError = error != null,
                supportingText = {
                    error?.let {
                        Text(
                            text = it.toString(LocalContext.current),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

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
                    currentJourneyGoal = currentJourneyGoal.copy(
                        progress = 0,
                        value = it
                    )
                },
                onFrequencyChanged = {
                    currentJourneyGoal = currentJourneyGoal.copy(goalFrequency = it)
                },
                onUnitChanged = {
                    currentJourneyGoal = currentJourneyGoal.copy(unit = it)
                }
            )

            StandardSpacer()

            val isGoalValid = selectedJourneyName.isNotBlank()
                    && currentJourneyGoal.value > 0
                    && currentJourneyGoal.value < MAX_GOAL_VALUE
                    && currentJourneyGoal.unit.isNotBlank()

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (resetWarningShowing) {
                    Text(
                        text = stringResource(R.string.edit_journey_progress_reset_warning),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    enabled = isGoalValid,
                    onClick = {
                        onConfirmRequest(
                            selectedJourneyName,
                            selectedIcon,
                            currentJourneyGoal
                        )
                    }
                ) {
                    Text(confirmButtonText)
                }
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
    onFrequencyChanged: (GoalFrequency) -> Unit,
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
    modifier: Modifier = Modifier,
    maxValue: Int = MAX_GOAL_VALUE
) {
    var numericStringValue by remember { mutableStateOf(initialValue.toString()) }
    var error: InputError? by remember { mutableStateOf(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        OutlinedTextField(
            value = numericStringValue,
            onValueChange = {
                error = if (!validateGoalValue(it)) {
                    InputError.GENERIC
                } else if ((it.toIntOrNull() ?: 0) > maxValue) {
                    InputError.ABOVE_MAX_VALUE
                } else if (it.isBlank()) {
                    InputError.CANT_BE_EMPTY
                } else {
                    null
                }

                if (it.isDigitsOnly()) {
                    numericStringValue = it
                }

                onValueChanged(it.toIntOrNull() ?: 0)
            },
            placeholder = { Text(stringResource(R.string.new_journey_goal_value_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(
                        text = it.toString(LocalContext.current),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .widthIn(min = 24.dp)
                .weight(1f)
        )

        StandardHalfSpacer()

        val defaultUnit = stringResource(R.string.new_journey_goal_value_label)
        var goalUnit by remember { mutableStateOf(defaultUnit) }
        var error: InputError? by remember { mutableStateOf(null) }


        TextField(
            value = goalUnit,
            onValueChange = {
                goalUnit = it

                error = if (it.isNotBlank()) {
                    null
                } else {
                    InputError.CANT_BE_EMPTY
                }

                onUnitChanged(it)
            },
            placeholder = { Text(text = defaultUnit) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(
                        text = it.toString(LocalContext.current),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .widthIn(min = 24.dp)
                .weight(2f)
        )
    }
}

private fun validateGoalValue(value: String): Boolean {
    return value.isDigitsOnly() && (value.toIntOrNull() ?: 0) > 0
}

@Composable
private fun IconNameInputRow(
    selectedIcon: JourneyIcon,
    journeyName: String,
    journeyNamePlaceholder: String,
    onIconClicked: () -> Unit,
    onNameValueChanged: (String) -> Unit,
    isError: Boolean = false,
    supportingText: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.Top,
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
            isError = isError,
            supportingText = supportingText,
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

enum class InputError {
    GENERIC, ABOVE_MAX_VALUE, CANT_BE_EMPTY;

    fun toString(context: Context) = when (this) {
        GENERIC -> context.getString(R.string.goal_value_error_default)
        ABOVE_MAX_VALUE -> context.getString(R.string.goal_value_error_above_max)
        CANT_BE_EMPTY -> context.getString(R.string.goal_value_error_empty)
    }
}