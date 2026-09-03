/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.repository

import kotlinx.coroutines.flow.Flow

/** Domain contract for the permission state required by the sharing service. */
interface PermissionRepository {
    val isFirstLaunch: Flow<Boolean>

    fun getMissingRuntimePermissions(): List<String>

    fun areAllRuntimePermissionsGranted(): Boolean

    suspend fun setFirstLaunchComplete()

    suspend fun setNotificationRequested()

    fun openAppSettings()
}
