/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.onboarding

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.eaxor.easy2share.R

/**
 * Section 1 illustration — file cards ride a conveyor into a mechanical press,
 * receive a hard SENT stamp, and exit as completed work.
 */
@Composable
fun IndustrialFilePressIllustration(
    accent: Color,
    accentSecondary: Color,
    paper: Color,
    modifier: Modifier = Modifier,
) {
    val sentLabel = "SENT"
    val stampPaint =
        remember(sentLabel, accent) {
            Paint().apply {
                color = accent.toArgb()
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
        }
    val transition = rememberInfiniteTransition(label = "industrialFilePress")
    val cycle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 2100, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "filePressCycle",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val beltTop = h * 0.72f
        val beltHeight = h * 0.15f
        val beltInset = h * 0.025f
        val fileWidth = w * 0.20f
        val fileHeight = h * 0.20f
        val fileY = beltTop - fileHeight + beltInset
        val pressFileX = (w - fileWidth) / 2f
        val fileX =
            when {
                cycle < 0.32f -> lerpValue(-fileWidth, pressFileX, cycle / 0.32f)
                cycle < 0.58f -> pressFileX
                else -> lerpValue(pressFileX, w + fileWidth, (cycle - 0.58f) / 0.42f)
            }
        val pressProgress =
            when {
                cycle < 0.32f -> 0f
                cycle < 0.40f -> (cycle - 0.32f) / 0.08f
                cycle < 0.51f -> 1f
                cycle < 0.59f -> 1f - ((cycle - 0.51f) / 0.08f)
                else -> 0f
            }
        val stamped = cycle >= 0.40f

        // Conveyor body and moving registration blocks.
        drawRect(
            color = accent,
            topLeft = Offset(0f, beltTop),
            size = Size(w, beltHeight),
        )
        drawRect(
            color = accentSecondary,
            topLeft = Offset(0f, beltTop + beltInset),
            size = Size(w, beltHeight - beltInset * 2f),
        )
        val markerSpacing = w * 0.12f
        val markerWidth = w * 0.035f
        val markerOffset = (cycle * markerSpacing * 8f) % markerSpacing
        var markerX = -markerSpacing + markerOffset
        while (markerX < w) {
            drawRect(
                color = accent,
                topLeft = Offset(markerX, beltTop + beltHeight * 0.36f),
                size = Size(markerWidth, beltHeight * 0.28f),
            )
            markerX += markerSpacing
        }

        // Press frame.
        val frameLeft = w * 0.28f
        val frameRight = w * 0.72f
        val frameTop = h * 0.12f
        val columnWidth = w * 0.065f
        drawRect(
            color = accent,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameRight - frameLeft, h * 0.16f),
        )
        repeat(3) { index ->
            drawRect(
                color = accentSecondary,
                topLeft = Offset(frameLeft + w * (0.055f + index * 0.105f), frameTop + h * 0.055f),
                size = Size(w * 0.045f, h * 0.045f),
            )
        }
        drawRect(
            color = accent,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(columnWidth, beltTop - frameTop),
        )
        drawRect(
            color = accent,
            topLeft = Offset(frameRight - columnWidth, frameTop),
            size = Size(columnWidth, beltTop - frameTop),
        )
        drawRect(
            color = accentSecondary,
            topLeft = Offset(frameLeft + w * 0.018f, frameTop + h * 0.20f),
            size = Size(columnWidth - w * 0.036f, beltTop - frameTop - h * 0.25f),
        )
        drawRect(
            color = accentSecondary,
            topLeft = Offset(frameRight - columnWidth + w * 0.018f, frameTop + h * 0.20f),
            size = Size(columnWidth - w * 0.036f, beltTop - frameTop - h * 0.25f),
        )

        // File card moving through the press.
        drawRect(
            color = paper,
            topLeft = Offset(fileX, fileY),
            size = Size(fileWidth, fileHeight),
        )
        drawRect(
            color = accent,
            topLeft = Offset(fileX, fileY),
            size = Size(fileWidth, fileHeight),
            style = Stroke(width = w * 0.009f),
        )
        val foldSize = fileWidth * 0.22f
        val fold =
            Path().apply {
                moveTo(fileX + fileWidth - foldSize, fileY)
                lineTo(fileX + fileWidth, fileY + foldSize)
                lineTo(fileX + fileWidth - foldSize, fileY + foldSize)
                close()
            }
        drawPath(path = fold, color = accentSecondary)
        repeat(2) { index ->
            drawRect(
                color = accent,
                topLeft = Offset(fileX + fileWidth * 0.14f, fileY + fileHeight * (0.22f + index * 0.17f)),
                size = Size(fileWidth * 0.48f, h * 0.018f),
            )
        }
        if (stamped) {
            val stampLeft = fileX + fileWidth * 0.10f
            val stampTop = fileY + fileHeight * 0.57f
            val stampWidth = fileWidth * 0.80f
            val stampHeight = fileHeight * 0.28f
            drawRect(
                color = accentSecondary,
                topLeft = Offset(stampLeft, stampTop),
                size = Size(stampWidth, stampHeight),
            )
            drawRect(
                color = accent,
                topLeft = Offset(stampLeft, stampTop),
                size = Size(stampWidth, stampHeight),
                style = Stroke(width = w * 0.007f),
            )
            stampPaint.textSize = stampHeight * 0.62f
            drawContext.canvas.nativeCanvas.drawText(
                sentLabel,
                stampLeft + stampWidth / 2f,
                stampTop + stampHeight * 0.72f,
                stampPaint,
            )
        }

        // Ram and platen strike after the file reaches the center.
        val ramCenter = w * 0.5f
        val platenRestY = h * 0.36f
        val platenY = platenRestY + pressProgress * h * 0.14f
        drawRect(
            color = accent,
            topLeft = Offset(ramCenter - w * 0.035f, frameTop + h * 0.12f),
            size = Size(w * 0.07f, platenY - frameTop - h * 0.10f),
        )
        drawRect(
            color = accent,
            topLeft = Offset(ramCenter - w * 0.15f, platenY),
            size = Size(w * 0.30f, h * 0.065f),
        )
        drawRect(
            color = accentSecondary,
            topLeft = Offset(ramCenter - w * 0.12f, platenY + h * 0.018f),
            size = Size(w * 0.24f, h * 0.025f),
        )

        if (pressProgress > 0.9f) {
            repeat(3) { index ->
                val blockY = fileY + h * (0.02f + index * 0.055f)
                drawRect(
                    color = accent,
                    topLeft = Offset(frameLeft - w * (0.07f + index * 0.025f), blockY),
                    size = Size(w * 0.045f, h * 0.025f),
                )
                drawRect(
                    color = accent,
                    topLeft = Offset(frameRight + w * (0.025f + index * 0.025f), blockY),
                    size = Size(w * 0.045f, h * 0.025f),
                )
            }
        }
    }
}

/**
 * Section 2 illustration — opposing payloads cross a central encryption gate,
 * disappear into cipher channels, and emerge verified on the other side.
 */
@Composable
fun EncryptionGateIllustration(
    accent: Color,
    accentSecondary: Color,
    paper: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "encryptionGate")
    val cycle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "encryptionCycle",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val topLaneY = h * 0.33f
        val bottomLaneY = h * 0.68f
        val laneWidth = h * 0.025f
        val packetWidth = w * 0.18f
        val packetHeight = h * 0.16f
        val gateLeft = w * 0.37f
        val gateWidth = w * 0.26f
        val gateTop = h * 0.10f
        val gateHeight = h * 0.80f

        // Bidirectional rails and moving registration marks.
        drawRect(
            color = accent,
            topLeft = Offset(0f, topLaneY - laneWidth / 2f),
            size = Size(w, laneWidth),
        )
        drawRect(
            color = accent,
            topLeft = Offset(0f, bottomLaneY - laneWidth / 2f),
            size = Size(w, laneWidth),
        )
        val markerSpacing = w * 0.13f
        val markerOffset = (cycle * markerSpacing * 7f) % markerSpacing
        var markerX = -markerSpacing + markerOffset
        while (markerX < w) {
            drawRect(
                color = accentSecondary,
                topLeft = Offset(markerX, topLaneY - h * 0.03f),
                size = Size(w * 0.035f, h * 0.06f),
            )
            drawRect(
                color = accentSecondary,
                topLeft = Offset(w - markerX - w * 0.035f, bottomLaneY - h * 0.03f),
                size = Size(w * 0.035f, h * 0.06f),
            )
            markerX += markerSpacing
        }

        // Payloads pass in opposite directions and gain verification marks
        // after crossing the center of the gate.
        val topPacketX = lerpValue(-packetWidth, w, cycle)
        val bottomPacketX = lerpValue(w, -packetWidth, cycle)
        val verified = cycle >= 0.60f
        drawSecurePacket(
            topLeft = Offset(topPacketX, topLaneY - packetHeight / 2f),
            packetSize = Size(packetWidth, packetHeight),
            accent = accent,
            fill = if (verified) accentSecondary else paper,
            verified = verified,
        )
        drawSecurePacket(
            topLeft = Offset(bottomPacketX, bottomLaneY - packetHeight / 2f),
            packetSize = Size(packetWidth, packetHeight),
            accent = accent,
            fill = if (verified) accentSecondary else paper,
            verified = verified,
        )

        // Heavy gate body hides each payload while the cipher channels process it.
        drawRect(
            color = accent,
            topLeft = Offset(gateLeft, gateTop),
            size = Size(gateWidth, gateHeight),
        )
        drawRect(
            color = accentSecondary,
            topLeft = Offset(gateLeft + w * 0.025f, gateTop + h * 0.035f),
            size = Size(gateWidth - w * 0.05f, h * 0.06f),
        )
        drawRect(
            color = accentSecondary,
            topLeft = Offset(gateLeft + w * 0.025f, gateTop + gateHeight - h * 0.095f),
            size = Size(gateWidth - w * 0.05f, h * 0.06f),
        )

        val channelHeight = packetHeight + h * 0.045f
        val cipherPhase = (cycle * 12f).toInt()
        listOf(topLaneY, bottomLaneY).forEachIndexed { laneIndex, laneY ->
            val channelTop = laneY - channelHeight / 2f
            drawRect(
                color = paper,
                topLeft = Offset(gateLeft, channelTop),
                size = Size(gateWidth, channelHeight),
            )
            drawRect(
                color = accentSecondary,
                topLeft = Offset(gateLeft, channelTop),
                size = Size(gateWidth, channelHeight),
                style = Stroke(width = w * 0.009f),
            )
            val cellWidth = gateWidth / 5f
            repeat(5) { cell ->
                val active = (cell + cipherPhase + laneIndex) % 2 == 0
                drawRect(
                    color = if (active) accent else accentSecondary,
                    topLeft =
                        Offset(
                            gateLeft + cell * cellWidth + w * 0.012f,
                            channelTop + channelHeight * 0.30f,
                        ),
                    size = Size(cellWidth - w * 0.024f, channelHeight * 0.40f),
                )
            }
            val scannerX = gateLeft + (cycle * 2f % 1f) * gateWidth
            drawRect(
                color = accent,
                topLeft = Offset(scannerX, channelTop),
                size = Size(w * 0.012f, channelHeight),
            )
        }

        // Central lock flashes during the verification window.
        val lockCenterX = w * 0.5f
        val lockCenterY = h * 0.505f
        val verificationActive = cycle in 0.43f..0.60f
        val lockColor = if (verificationActive) paper else accentSecondary
        drawArc(
            color = lockColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(lockCenterX - w * 0.045f, lockCenterY - h * 0.095f),
            size = Size(w * 0.09f, h * 0.13f),
            style = Stroke(width = w * 0.018f),
        )
        drawRect(
            color = lockColor,
            topLeft = Offset(lockCenterX - w * 0.065f, lockCenterY - h * 0.015f),
            size = Size(w * 0.13f, h * 0.12f),
        )
        drawRect(
            color = accent,
            topLeft = Offset(lockCenterX - w * 0.012f, lockCenterY + h * 0.018f),
            size = Size(w * 0.024f, h * 0.05f),
        )
        if (verificationActive) {
            repeat(2) { index ->
                val distance = w * (0.095f + index * 0.035f)
                drawRect(
                    color = accentSecondary,
                    topLeft = Offset(lockCenterX - distance, lockCenterY + h * 0.02f),
                    size = Size(w * 0.025f, h * 0.035f),
                )
                drawRect(
                    color = accentSecondary,
                    topLeft = Offset(lockCenterX + distance - w * 0.025f, lockCenterY + h * 0.02f),
                    size = Size(w * 0.025f, h * 0.035f),
                )
            }
        }
    }
}

// --------------------------------------------------------------------------
//  Drawing helpers
// --------------------------------------------------------------------------

private fun DrawScope.drawSecurePacket(
    topLeft: Offset,
    packetSize: Size,
    accent: Color,
    fill: Color,
    verified: Boolean,
) {
    drawRect(
        color = fill,
        topLeft = topLeft,
        size = packetSize,
    )
    drawRect(
        color = accent,
        topLeft = topLeft,
        size = packetSize,
        style = Stroke(width = packetSize.width * 0.05f),
    )
    repeat(3) { index ->
        drawRect(
            color = accent,
            topLeft =
                Offset(
                    topLeft.x + packetSize.width * 0.12f,
                    topLeft.y + packetSize.height * (0.18f + index * 0.18f),
                ),
            size = Size(packetSize.width * 0.42f, packetSize.height * 0.08f),
        )
    }
    if (verified) {
        val check =
            Path().apply {
                moveTo(
                    topLeft.x + packetSize.width * 0.62f,
                    topLeft.y + packetSize.height * 0.54f,
                )
                lineTo(
                    topLeft.x + packetSize.width * 0.72f,
                    topLeft.y + packetSize.height * 0.68f,
                )
                lineTo(
                    topLeft.x + packetSize.width * 0.89f,
                    topLeft.y + packetSize.height * 0.30f,
                )
            }
        drawPath(
            path = check,
            color = accent,
            style = Stroke(width = packetSize.width * 0.06f),
        )
    }
}

private fun lerpValue(
    start: Float,
    end: Float,
    progress: Float,
): Float = start + (end - start) * progress.coerceIn(0f, 1f)
