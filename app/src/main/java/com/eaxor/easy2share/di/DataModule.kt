/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.di

import com.eaxor.easy2share.data.local.OnboardingLocalDataSource
import com.eaxor.easy2share.data.local.OnboardingPreferencesDataSource
import com.eaxor.easy2share.data.repository.ClipboardRepositoryImpl
import com.eaxor.easy2share.data.repository.FileRepositoryImpl
import com.eaxor.easy2share.data.repository.NetworkRepositoryImpl
import com.eaxor.easy2share.data.repository.OnboardingRepositoryImpl
import com.eaxor.easy2share.data.repository.PermissionRepositoryImpl
import com.eaxor.easy2share.domain.repository.ClipboardRepository
import com.eaxor.easy2share.domain.repository.FileRepository
import com.eaxor.easy2share.domain.repository.NetworkRepository
import com.eaxor.easy2share.domain.repository.OnboardingRepository
import com.eaxor.easy2share.domain.repository.PermissionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the data-layer implementations to the abstractions the rest of the app
 * depends on, keeping the Clean Architecture dependency direction intact:
 * callers see only the domain [OnboardingRepository] / data-source interfaces,
 * never the concrete classes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindOnboardingLocalDataSource(impl: OnboardingPreferencesDataSource): OnboardingLocalDataSource

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindClipboardRepository(impl: ClipboardRepositoryImpl): ClipboardRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(impl: FileRepositoryImpl): FileRepository

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(impl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindPermissionRepository(impl: PermissionRepositoryImpl): PermissionRepository
}
