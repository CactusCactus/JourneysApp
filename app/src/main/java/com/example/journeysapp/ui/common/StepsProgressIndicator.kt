package com.example.journeysapp.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor

@Composable
fun StepsProgressIndicator(checkedSteps: Int, maxSteps: Int, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val stepSize = 17.dp
        val stepSpacing = 4.dp

        val availableWidth = maxWidth
        val stepWidthWithSpacing = stepSize + stepSpacing

        val maxPossibleDisplayedSteps = if (stepWidthWithSpacing > 0.dp) {
            floor((availableWidth + stepSpacing) / stepWidthWithSpacing).toInt()
        } else {
            0
        }

        val uncheckedSteps = maxSteps - checkedSteps
        val isOverflowStart = checkedSteps - maxPossibleDisplayedSteps / 2 > 0
        val isOverflowEnd = uncheckedSteps - maxPossibleDisplayedSteps / 2 > 0

        val halfPointStart = maxPossibleDisplayedSteps / 2

        val overflowStartCount = if (isOverflowEnd) {
            checkedSteps - halfPointStart
        } else {
            maxSteps - maxPossibleDisplayedSteps + 1
        }.coerceAtLeast(0)

        val overflowEndCount = if (isOverflowStart) {
            maxSteps - (maxPossibleDisplayedSteps + overflowStartCount) + 2
        } else {
            maxSteps - maxPossibleDisplayedSteps + 1
        }.coerceAtLeast(0)

        var standardStepsCount = maxPossibleDisplayedSteps

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
            if (isOverflowStart) {
                OverflowStep(overflowSteps = overflowStartCount)
            }

            repeat(standardStepsCount) {
                Step(checked = currentlyCheckedSteps < displayedTarget)
                currentlyCheckedSteps++
            }

            if (isOverflowEnd) {
                OverflowStep(overflowSteps = overflowEndCount)
            }
        }
    }
}

@Composable
private fun Step(modifier: Modifier = Modifier, checked: Boolean = false) {
    val color = if (checked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier.border(shape = CircleShape, width = 1.dp, color = color)
    ) {
        Canvas(
            modifier = Modifier
                .size(16.dp)
                .padding(4.dp), onDraw = {
                if (checked) {
                    drawCircle(color = color)
                }
            })
    }
}

@Composable
private fun OverflowStep(
    modifier: Modifier = Modifier,
    overflowSteps: Int,
    checked: Boolean = false
) {
    val color = if (checked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier
            .size(24.dp)
            .border(shape = CircleShape, width = 2.dp, color = color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$overflowSteps",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
private fun StepsProgressIndicatorPreview() {
    OverflowStep(overflowSteps = 5)
}