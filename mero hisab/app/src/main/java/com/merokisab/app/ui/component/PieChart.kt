package com.merokisab.app.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.merokisab.app.ui.theme.IncomeGreen
import com.merokisab.app.ui.theme.ExpenseRed

@Composable
fun PieChart(
    data: Map<String, Double>,
    title: String,
    modifier: Modifier = Modifier,
) {
    val entries = data.entries.filter { it.value > 0 }
    val total = entries.sumOf { it.value }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (entries.isEmpty() || total <= 0) {
                Text(
                    text = "No data",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .fillMaxWidth()
                        .height(180.dp),
                ) {
                    var startAngle = 0f
                    entries.forEachIndexed { index, entry ->
                        val sweep = (entry.value / total * 360).toFloat()
                        drawArc(
                            color = pieColor(index),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                        )
                        startAngle += sweep
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                entries.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(2.dp),
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = pieColor(index))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${entry.key} — Rs. %.2f".format(entry.value),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun pieColor(index: Int): Color {
    val greens = listOf(
        IncomeGreen,
        Color(0xFF66BB6A),
        Color(0xFF81C784),
        Color(0xFFA5D6A7),
        Color(0xFFC8E6C9),
        Color(0xFFE8F5E9),
    )
    val reds = listOf(
        ExpenseRed,
        Color(0xFFEF5350),
        Color(0xFFE57373),
        Color(0xFFEF9A9A),
        Color(0xFFFFCDD2),
        Color(0xFFFFEBEE),
    )
    val colors = if (index < greens.size) greens else reds
    return colors[index % colors.size]
}