package com.borisphen.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.borisphen.memoryshot.util.platform.log

@Composable
fun MyFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        log("1. constraints.minWidth = ${constraints.minWidth}, constraints.maxWidth = ${constraints.maxWidth} ")
        measurables.forEach {
            log("measurable = $it")
        }
        // List to hold the placeables
        val placeables = measurables.map { measurable ->
            // Measure each child with unbounded width to get its natural size
            measurable.measure(constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity))
        }

        var rowWidth = 0
        var rowHeight = 0

        val rowSizes = mutableListOf<List<Placeable>>()
        var currentRow = mutableListOf<Placeable>()

        // Group placeables into rows
        placeables.forEach { placeable ->
            if (rowWidth + placeable.width > constraints.maxWidth) {
                // If it doesn't fit, start a new row
                rowSizes.add(currentRow)
                currentRow = mutableListOf(placeable)
                rowWidth = placeable.width + horizontalArrangement.spacing.roundToPx()
            } else {
                // Otherwise, add it to the current row
                currentRow.add(placeable)
                rowWidth += placeable.width + horizontalArrangement.spacing.roundToPx()
            }
            rowHeight = maxOf(rowHeight, placeable.height)
            log("placeable COUNT: width = ${placeable.width}, height = ${placeable.height}, rowHeight = $rowHeight")
        }
        // Add the last row
        rowSizes.add(currentRow)

        // Calculate the total height of the layout
        val totalHeight = rowSizes.sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        } + (rowSizes.size - 1) * verticalArrangement.spacing.roundToPx()
        log("Total height = $totalHeight")
        layout(constraints.maxWidth, totalHeight) {
            var xPosition = 0
            var yOffset = 0

            // Place each row
            rowSizes.forEach { row ->
                val rowHeight = row.maxOfOrNull { it.height } ?: 0
                xPosition = 0 // Reset x position for the new row

                // Place each item in the current row
                row.forEach { placeable ->
                    log("placeable PLACE: width = ${placeable.width}, " +
                            "height = ${placeable.height}, rowHeight = $rowHeight, " +
                            "yOffset = $yOffset")
                    placeable.placeRelative(
                        x = xPosition,
                        y = yOffset + (rowHeight - placeable.height) / 2 // Center vertically if needed
                    )
                    xPosition += placeable.width + horizontalArrangement.spacing.roundToPx()
                }
                yOffset += rowHeight + verticalArrangement.spacing.roundToPx()
            }
        }
    }
}