package com.eaxor.easy2share.presentation.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Section 1 illustration — a wire-frame phone streaming glowing "content packets"
 * along a curved beam into a desktop monitor. Purely drawn with [Canvas], no assets.
 */
@Composable
fun PhoneToPcIllustration(
    accent: Color,
    accentSecondary: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "phoneToPc")
    val flow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "flow",
    )
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulse",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerY = h * 0.5f

        // --- Device geometry ---------------------------------------------------
        val phoneW = w * 0.17f
        val phoneH = phoneW * 2.02f
        val phoneTopLeft = Offset(w * 0.05f, centerY - phoneH / 2.5f)

        val monitorW = w * 0.24f
        val monitorH = monitorW * 0.59f
        val monitorTopLeft = Offset(w - monitorW - w * 0.05f, centerY *  1f/3f) //+ monitorH / 0.5f - h * 0.03f)

        val start = Offset(phoneTopLeft.x + phoneW, centerY)
        val end = Offset(monitorTopLeft.x, monitorTopLeft.y + monitorH / 2f)
        val control = Offset((start.x + end.x) / 2f, centerY - h * 0.20f)

        // --- Beam --------------------------------------------------------------
        val beam = Path().apply {
            moveTo(start.x, start.y)
            quadraticBezierTo(control.x, control.y, end.x, end.y)
        }
        drawPath(
            path = beam,
            color = accent.copy(alpha = 0.16f),
            style = Stroke(width = w * 0.02f),
        )
        drawPath(
            path = beam,
            color = accent.copy(alpha = 0.30f),
            style = Stroke(width = w * 0.006f),
        )

        // --- Source pulse rings around the phone ------------------------------
        repeat(3) { i ->
            val p = ((pulse + i / 3f) % 1f)
            drawCircle(
                color = accent.copy(alpha = (1f - p) * 0.35f),
                radius = phoneW * (0.4f + p * 1.4f),
                center = start,
                style = Stroke(width = w * 0.004f),
            )
        }

        // --- Flowing content packets ------------------------------------------
        val packetCount = 4
        repeat(packetCount) { i ->
            val t = ((flow + i.toFloat() / packetCount) % 1f)
            val pos = quadraticPoint(start, control, end, t)
            // fade in at the start, fade out near the monitor
            val alpha = (sin(t * PI).toFloat()).coerceIn(0f, 1f)
            val packet = size.minDimension * 0.045f
            val col = lerpColor(accentSecondary, accent, t)
            // glow
            drawCircle(
                color = col.copy(alpha = 0.20f * alpha),
                radius = packet * 2.1f,
                center = pos,
            )
            drawRoundRect(
                color = col.copy(alpha = alpha),
                topLeft = Offset(pos.x - packet / 2f, pos.y - packet / 2f),
                size = Size(packet, packet),
                cornerRadius = CornerRadius(packet * 0.32f, packet * 0.32f),
            )
        }

        // --- Phone -------------------------------------------------------------
        drawDevice(
            topLeft = phoneTopLeft,
            deviceSize = Size(phoneW, phoneH),
            cornerFraction = 0.22f,
            accent = accent,
            glow = accentSecondary,
            strokeWidth = w * 0.010f,
        )
        // phone camera notch
        drawCircle(
            color = accent.copy(alpha = 0.7f),
            radius = phoneW * 0.05f,
            center = Offset(phoneTopLeft.x + phoneW / 2f, phoneTopLeft.y + phoneH * 0.06f),
        )

        // --- Monitor -----------------------------------------------------------
        drawDevice(
            topLeft = monitorTopLeft,
            deviceSize = Size(monitorW, monitorH),
            cornerFraction = 0.10f,
            accent = accent,
            glow = accentSecondary,
            strokeWidth = w * 0.010f,
        )
        // monitor stand
        val standTop = monitorTopLeft.y + monitorH
        drawLine(
            color = accent,
            start = Offset(monitorTopLeft.x + monitorW / 2f, standTop),
            end = Offset(monitorTopLeft.x + monitorW / 2f, standTop + monitorH * 0.22f),
            strokeWidth = w * 0.012f,
        )
        drawLine(
            color = accent,
            start = Offset(monitorTopLeft.x + monitorW * 0.30f, standTop + monitorH * 0.22f),
            end = Offset(monitorTopLeft.x + monitorW * 0.70f, standTop + monitorH * 0.22f),
            strokeWidth = w * 0.012f,
        )
        // received "arrival" flash inside the monitor
        val arrival = (sin((flow) * 2f * PI).toFloat()).coerceIn(0f, 1f)
        drawRoundRect(
            color = accent.copy(alpha = 0.10f + 0.22f * arrival),
            topLeft = Offset(monitorTopLeft.x + monitorW * 0.14f, monitorTopLeft.y + monitorH * 0.2f),
            size = Size(monitorW * 0.72f, monitorH * 0.6f),
            cornerRadius = CornerRadius(monitorW * 0.04f, monitorW * 0.04f),
        )
    }
}

/**
 * Section 2 illustration — two devices bridged by expanding wireless rings with a
 * rotating two-way sync glyph in the center, communicating a secure live link.
 */
@Composable
fun WirelessSyncIllustration(
    accent: Color,
    accentSecondary: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "wirelessSync")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave",
    )
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spin",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.5f)

        val phoneW = w * 0.15f
        val phoneH = phoneW * 2.0f
        val phoneTopLeft = Offset(w * 0.08f, center.y - phoneH / 2f)

        val laptopW = w * 0.30f
        val laptopH = laptopW * 0.60f
        val laptopTopLeft = Offset(w * 0.62f, center.y - laptopH / 2f)

        // --- Expanding connection rings from the centre -----------------------
        repeat(4) { i ->
            val p = ((wave + i / 4f) % 1f)
            drawCircle(
                color = lerpColor(accent, accentSecondary, p).copy(alpha = (1f - p) * 0.45f),
                radius = w * 0.06f + p * w * 0.34f,
                center = center,
                style = Stroke(width = w * 0.005f),
            )
        }

        // --- Devices -----------------------------------------------------------
        drawDevice(
            topLeft = phoneTopLeft,
            deviceSize = Size(phoneW, phoneH),
            cornerFraction = 0.22f,
            accent = accent,
            glow = accentSecondary,
            strokeWidth = w * 0.010f,
        )
        drawDevice(
            topLeft = laptopTopLeft,
            deviceSize = Size(laptopW, laptopH),
            cornerFraction = 0.10f,
            accent = accent,
            glow = accentSecondary,
            strokeWidth = w * 0.010f,
        )
        // laptop base
        drawLine(
            color = accent,
            start = Offset(laptopTopLeft.x - laptopW * 0.08f, laptopTopLeft.y + laptopH),
            end = Offset(laptopTopLeft.x + laptopW * 1.08f, laptopTopLeft.y + laptopH),
            strokeWidth = w * 0.014f,
        )

        // --- Central hub glow --------------------------------------------------
        drawCircle(
            color = accentSecondary.copy(alpha = 0.18f),
            radius = w * 0.11f,
            center = center,
        )
        drawCircle(
            color = accent.copy(alpha = 0.9f),
            radius = w * 0.075f,
            center = center,
            style = Stroke(width = w * 0.006f),
        )

        // --- Rotating two-way sync arrows -------------------------------------
        val r = w * 0.05f
        rotate(degrees = spin, pivot = center) {
            drawSyncArrows(center = center, radius = r, color = accent, strokeWidth = w * 0.010f)
        }
    }
}

// --------------------------------------------------------------------------
//  Drawing helpers
// --------------------------------------------------------------------------

private fun DrawScope.drawDevice(
    topLeft: Offset,
    deviceSize: Size,
    cornerFraction: Float,
    accent: Color,
    glow: Color,
    strokeWidth: Float,
) {
    val corner = CornerRadius(
        deviceSize.minDimension * cornerFraction,
        deviceSize.minDimension * cornerFraction,
    )
    // soft outer glow
    drawRoundRect(
        color = glow.copy(alpha = 0.18f),
        topLeft = Offset(topLeft.x - strokeWidth * 1.5f, topLeft.y - strokeWidth * 1.5f),
        size = Size(deviceSize.width + strokeWidth * 3f, deviceSize.height + strokeWidth * 3f),
        cornerRadius = corner,
    )
    // subtle screen fill
    drawRoundRect(
        color = accent.copy(alpha = 0.06f),
        topLeft = topLeft,
        size = deviceSize,
        cornerRadius = corner,
    )
    // wire-frame outline
    drawRoundRect(
        color = accent,
        topLeft = topLeft,
        size = deviceSize,
        cornerRadius = corner,
        style = Stroke(width = strokeWidth),
    )
}

private fun DrawScope.drawSyncArrows(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float,
) {
    // Two opposing arcs forming a circular "sync" glyph.
    val arcSize = Size(radius * 2f, radius * 2f)
    val arcTopLeft = Offset(center.x - radius, center.y - radius)
    drawArc(
        color = color,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth),
    )
    drawArc(
        color = color,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth),
    )
    // arrow heads at each arc tip
    drawArrowHead(center, radius, 20f, color, strokeWidth)
    drawArrowHead(center, radius, 200f, color, strokeWidth)
}

private fun DrawScope.drawArrowHead(
    center: Offset,
    radius: Float,
    angleDeg: Float,
    color: Color,
    strokeWidth: Float,
) {
    val a = angleDeg * PI.toFloat() / 180f
    val tip = Offset(center.x + radius * cos(a), center.y + radius * sin(a))
    val len = radius * 0.5f
    drawLine(
        color = color,
        start = tip,
        end = Offset(tip.x - len * cos(a - 0.6f), tip.y - len * sin(a - 0.6f)),
        strokeWidth = strokeWidth,
    )
    drawLine(
        color = color,
        start = tip,
        end = Offset(tip.x - len * cos(a + 0.6f), tip.y - len * sin(a + 0.6f)),
        strokeWidth = strokeWidth,
    )
}

private fun quadraticPoint(p0: Offset, c: Offset, p1: Offset, t: Float): Offset {
    val u = 1f - t
    val x = u * u * p0.x + 2f * u * t * c.x + t * t * p1.x
    val y = u * u * p0.y + 2f * u * t * c.y + t * t * p1.y
    return Offset(x, y)
}

private fun lerpColor(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = a.alpha + (b.alpha - a.alpha) * t,
)

