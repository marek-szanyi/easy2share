/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.ui.theme

import androidx.compose.ui.graphics.Color

// -----------------------------------------------------------------------------
//  Application palette
//
//  Single source of truth for every colour in the app. Screens must never
//  hardcode `Color(0x...)`; they consume these through [Easy2shareTheme] via
//  `MaterialTheme.colorScheme` and [Easy2shareColors.extended].
//
//  Brand accents are intentionally muted — roughly 35% less saturated than their
//  vivid originals — and every on-* colour clears WCAG AA contrast on its surface.
// -----------------------------------------------------------------------------

// --- Brand accents (muted) ---------------------------------------------------
val BrandTeal = Color(0xFF0087B5)
val BrandIndigo = Color(0xFF616CD5)
val BrandMagenta = Color(0xFFB93ACF)
val BrandViolet = Color(0xFFCD8CF6)
val BrandTealDeep = Color(0xFF18A9E7)

/** High-contrast content colour that sits on top of any brand accent. */
val OnAccent = Color(0xFF5E5E5E)

// --- Light surfaces & content ------------------------------------------------
val LightBackground = Color(0xF0F5F6F6)
val LightSurface = Color(0xFFFBFCFE)
val LightSurfaceDim = Color(0xFFBBD3FF)
val LightOnSurface = Color(0xFF1B1D24) // titles / high emphasis
val LightOnSurfaceVariant = Color(0xFF3F434C) // body / secondary text
val LightOutline = Color(0xFF5B606B) // medium emphasis (e.g. Skip)

val LightWidgetDarkBlue = Color(0xFF192543)

val LightWidgetLightBlue = Color(0xFF3C538C)

// --- Dark surfaces & content -------------------------------------------------
val DarkBackground = Color(0xFF0F1218)
val DarkSurface = Color(0xFF0F1218)
val DarkSurfaceDim = Color(0xFF090B0F)
val DarkOnSurface = Color(0xFFECEEF3)
val DarkOnSurfaceVariant = Color(0xFFC4C8D0)
val DarkOutline = Color(0xFF9AA0AD)
