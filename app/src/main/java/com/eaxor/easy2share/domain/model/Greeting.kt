package com.eaxor.easy2share.domain.model

/**
 * Domain entity describing the greeting shown on the home surface.
 *
 * Pure Kotlin with no framework knowledge — the presentation layer decides how
 * to render [recipient] (e.g. "Hello Android!").
 */
data class Greeting(
    val recipient: String,
)

