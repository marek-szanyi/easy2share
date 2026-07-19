/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = BrandTeal,
        onPrimary = OnAccent,
        secondary = BrandIndigo,
        onSecondary = OnAccent,
        tertiary = BrandMagenta,
        onTertiary = OnAccent,
        background = DarkBackground,
        onBackground = DarkOnSurface,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceDim,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = BrandTeal,
        onPrimary = OnAccent,
        secondary = BrandIndigo,
        onSecondary = OnAccent,
        tertiary = BrandMagenta,
        onTertiary = OnAccent,
        background = LightBackground,
        onBackground = LightOnSurface,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceDim,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
    )

/**
 * App-specific colours that have no equivalent Material role — currently the
 * onboarding aurora backdrop. Provided through the theme so every screen reads
 * them the same way as the standard [androidx.compose.material3.ColorScheme].
 */
@Immutable
data class ExtendedColors(
    val auroraTop: Color,
    val auroraBottom: Color,
    val auroraBlobViolet: Color,
    val auroraBlobTeal: Color,
)

private val LightExtendedColors =
    ExtendedColors(
        auroraTop = LightSurface,
        auroraBottom = LightBackground,
        auroraBlobViolet = BrandTealDeep,
        auroraBlobTeal = LightSurfaceDim,
    )

private val DarkExtendedColors =
    ExtendedColors(
        auroraTop = DarkSurfaceDim,
        auroraBottom = DarkBackground,
        auroraBlobViolet = BrandIndigo,
        auroraBlobTeal = DarkSurfaceDim,
    )

private val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/** Accessor for app colours that extend the Material [MaterialTheme] palette. */
object Easy2shareColors {
    val extended: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}

@Composable
fun Easy2shareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic (wallpaper-based) colour is off by default so the muted brand
    // palette stays consistent across every screen and device.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
