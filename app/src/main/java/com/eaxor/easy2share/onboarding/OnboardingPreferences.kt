package com.eaxor.easy2share.onboarding

import android.content.Context

/**
 * Lightweight persistence for onboarding state.
 *
 * Backed by [android.content.SharedPreferences] so the welcome experience is
 * shown only once — right after the very first launch of the application.
 */
class OnboardingPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Whether the user has already completed (or skipped) the welcome flow. */
    var isWelcomeCompleted: Boolean
        get() = prefs.getBoolean(KEY_WELCOME_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_WELCOME_COMPLETED, value).apply()

    private companion object {
        const val PREFS_NAME = "easy2share_onboarding"
        const val KEY_WELCOME_COMPLETED = "welcome_completed"
    }
}

