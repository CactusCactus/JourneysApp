package com.example.journeysapp.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.journeysapp.data.model.GoalHistory
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun GoalHistoryGraph(
    goalHistory: List<GoalHistory>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(goalHistory) {
        modelProducer.runTransaction {
            columnSeries {
                series(
                    x = goalHistory.map { it.resetTime.time },
                    y = goalHistory.map { it.progress })
            }
        }
    }

    val bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = BottomAxisValueFormatter)

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = bottomAxis
            ),
            modelProducer,
            modifier = modifier,
        )
    }
}

private val BottomAxisValueFormatter =
    object : CartesianValueFormatter {
        private val dateFormat =
            SimpleDateFormat("dd/MM", Locale.US).apply { timeZone = TimeZone.getTimeZone("GMT") }

        override fun format(
            context: CartesianMeasuringContext,
            value: Double,
            verticalAxisPosition: Axis.Position.Vertical?,
        ) = dateFormat.format(value)
    }