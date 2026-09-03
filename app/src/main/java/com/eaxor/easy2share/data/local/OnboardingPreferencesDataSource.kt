/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [OnboardingLocalDataSource] backed by Jetpack [DataStore].
 *
 * Replaces the previous synchronous `SharedPreferences` wrapper with a
 * reactive, coroutine-friendly source that exposes the completion flag as a
 * [Flow], so the rest of the app can observe changes rather than poll.
 */
class OnboardingPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : OnboardingLocalDataSource {
        override val isOnboardingCompleted: Flow<Boolean> =
            dataStore.data.map { preferences -> preferences[KEY_WELCOME_COMPLETED] ?: false }

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            dataStore.edit { preferences -> preferences[KEY_WELCOME_COMPLETED] = completed }
        }

        private companion object {
            val KEY_WELCOME_COMPLETED = booleanPreferencesKey("welcome_completed")
        }
    }
