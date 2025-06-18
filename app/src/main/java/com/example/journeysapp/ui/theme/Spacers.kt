package com.example.journeysapp.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StandardSpacer(modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.size(standardPadding))

@Composable
fun StandardDoubleSpacer(modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.size(standardDoublePadding))

@Composable
fun StandardHalfSpacer(modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.size(standardHalfPadding))

@Composable
fun StandardQuarterSpacer(modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.size(standardQuarterPadding))