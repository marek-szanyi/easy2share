/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** App-wide DataStore holding onboarding preferences. */
private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "easy2share_onboarding",
)

/**
 * Provides framework/storage-level singletons that Hilt cannot construct on its
 * own (they require the application [Context] or a builder).
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideOnboardingDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.onboardingDataStore
}
