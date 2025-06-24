package com.example.journeysapp.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor
import kotlin.math.min

@Composable
fun StepsProgressIndicator(
    checkedSteps: Int,
    maxSteps: Int,
    modifier: Modifier = Modifier,
    stepSize: Dp = 16.dp,
    stepSpacing: Dp = 4.dp
) {
    FlowRow(modifier = modifier, verticalArrangement = Arrangement.spacedBy(stepSpacing)) {
        var currentlyCheckedSteps = remember { 0f }

        repeat(maxSteps) {
            StepDot(checked = currentlyCheckedSteps < checkedSteps, stepSize = stepSize)
            currentlyCheckedSteps++

            if (currentlyCheckedSteps < maxSteps) {
                Spacer(modifier = Modifier.size(stepSpacing))
            }
        }
    }
}

@Composable
fun StepsOverflowProgressIndicator(
    checkedSteps: Int,
    maxSteps: Int,
    modifier: Modifier = Modifier,
    stepSize: Dp = 16.dp,
    overflowStepSize: Dp = 24.dp,
    stepSpacing: Dp = 4.dp
) {
    BoxWithConstraints(modifier = modifier) {
        val availableWidth = maxWidth
        val stepWidthWithSpacing = stepSize + stepSpacing

        val maxPossibleDisplayedSteps = if (stepWidthWithSpacing > 0.dp) {
            floor((availableWidth - overflowStepSize + stepSpacing) / stepWidthWithSpacing).toInt()
        } else 0

        val uncheckedSteps = maxSteps - checkedSteps
        val halfPoint = maxPossibleDisplayedSteps / 2

        val isOverflowStart = checkedSteps - halfPoint > 0
        val isOverflowEnd = uncheckedSteps - halfPoint > 0

        val overflowStartCount = if (isOverflowEnd) {
            checkedSteps - halfPoint
        } else {
            maxSteps - maxPossibleDisplayedSteps + 1
        }.coerceAtLeast(0)

        val overflowEndCount = if (isOverflowStart) {
            maxSteps - (maxPossibleDisplayedSteps + overflowStartCount) + 2
        } else {
            maxSteps - maxPossibleDisplayedSteps + 1
        }.coerceAtLeast(0)

        var standardStepsCount = min(maxPossibleDisplayedSteps, maxSteps)

        if (isOverflowStart)
            standardStepsCount--

        if (isOverflowEnd)
            standardStepsCount--

        var currentlyCheckedSteps = remember { 0f }
        val displayedTarget = checkedSteps - overflowStartCount

        Row(
            horizontalArrangement = Arrangement.spacedBy(stepSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibilityForSteps(isOverflowStart, fromLeft = true) {
                OverflowStepDot(
                    overflowSteps = overflowStartCount,
                    checked = true,
                    stepSize = overflowStepSize
                )
            }

            repeat(standardStepsCount) {
                StepDot(checked = currentlyCheckedSteps < displayedTarget, stepSize = stepSize)
                currentlyCheckedSteps++
            }

            AnimatedVisibilityForSteps(isOverflowEnd, fromLeft = false) {
                OverflowStepDot(
                    overflowSteps = overflowEndCount,
                    checked = false,
                    stepSize = overflowStepSize
                )
            }
        }
    }
}

@Composable
private fun StepDot(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    stepSize: Dp = 16.dp
) {
    val color = if (checked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier
            .size(stepSize)
            .border(shape = CircleShape, width = 1.dp, color = color)
    ) {
        AnimatedVisibility(checked, enter = scaleIn(), exit = scaleOut()) {
            Canvas(
                modifier = Modifier
                    .size(stepSize)
                    .padding(4.dp),
                onDraw = {
                    drawCircle(color = color)
                }
            )
        }
    }
}

@Composable
private fun OverflowStepDot(
    modifier: Modifier = Modifier,
    overflowSteps: Int,
    checked: Boolean = false,
    stepSize: Dp = 24.dp
) {
    val color = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(stepSize)
            .border(shape = CircleShape, width = 1.dp, color = color),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Canvas(
                modifier = Modifier
                    .size(stepSize)
                    .padding(3.dp),
                onDraw = {
                    drawCircle(color = color)
                })
        }

        Text(
            text = "$overflowSteps",
            color = if (checked) MaterialTheme.colorScheme.onPrimary else color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AnimatedVisibilityForSteps(
    visible: Boolean,
    fromLeft: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val offset: (fullWidth: Int) -> Int = if (fromLeft) {
        { -it / 2 }
    } else {
        { it * 2 }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = offset) + scaleIn(),
        exit = slideOutHorizontally(targetOffsetX = offset) + scaleOut(),
        content = content
    )
}

@Preview
@Composable
private fun StepsProgressIndicatorPreview() {
    StepsProgressIndicator(checkedSteps = 3, maxSteps = 10)
}