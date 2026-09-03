/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.eaxor.easy2share.ui.theme.HazardYellow
import com.eaxor.easy2share.ui.theme.IndustrialInk
import com.eaxor.easy2share.ui.theme.IndustrialPaper

@Composable
fun BrutalistBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(IndustrialPaper)) {
        val gridSize = 48.dp.toPx()
        val gridColor = IndustrialInk.copy(alpha = 0.08f)
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx(),
            )
            x += gridSize
        }

        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
            )
            y += gridSize
        }

        val railWidth = 10.dp.toPx()
        drawRect(
            color = HazardYellow,
            size = Size(railWidth, size.height),
        )
        drawLine(
            color = IndustrialInk,
            start = Offset(railWidth, 0f),
            end = Offset(railWidth, size.height),
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
fun HazardStripe(
    shouldBeAnimated: Boolean,
    modifier: Modifier = Modifier,
) {
    val conveyorProgress =
        if (shouldBeAnimated) {
            val transition = rememberInfiniteTransition(label = "hazardConveyor")
            val progress by transition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 520, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
                label = "hazardConveyorProgress",
            )
            progress
        } else {
            0f
        }

    Canvas(modifier = modifier.background(HazardYellow)) {
        val stripeWidth = 10.dp.toPx()
        val stripeStep = 24.dp.toPx()
        var x = -size.height - stripeStep + (stripeStep * conveyorProgress)
        while (x < size.width + size.height) {
            drawLine(
                color = IndustrialInk,
                start = Offset(x, size.height),
                end = Offset(x + size.height, 0f),
                strokeWidth = stripeWidth,
            )
            x += stripeStep
        }
        drawLine(
            color = IndustrialInk,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            strokeWidth = 3.dp.toPx(),
        )
    }
}
