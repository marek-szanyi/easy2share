/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.repository

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.permissionDataStore by preferencesDataStore(name = "permissions_prefs")

class PermissionRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
            private val NOTIFICATION_REQUESTED_KEY = booleanPreferencesKey("notification_requested")
        }

        /**
         * All permissions required to run the embedded web server as a foreground service.
         *
         * The list is API-aware: [Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC] and
         * [Manifest.permission.POST_NOTIFICATIONS] only exist / are enforced on newer platforms.
         */
        val requiredServicePermissions: List<String>
            get() =
                buildList {
                    add(Manifest.permission.INTERNET)
                    add(Manifest.permission.ACCESS_NETWORK_STATE)
                    add(Manifest.permission.ACCESS_WIFI_STATE)
                    add(Manifest.permission.CHANGE_NETWORK_STATE)
                    add(Manifest.permission.FOREGROUND_SERVICE)
                    add(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
                    add(Manifest.permission.POST_NOTIFICATIONS)
                }

        /**
         * Subset of [requiredServicePermissions] that is classified as *dangerous* and therefore
         * must be granted by the user at runtime. Normal permissions are granted at install time.
         */
        val runtimeServicePermissions: List<String>
            get() =
                listOf(Manifest.permission.POST_NOTIFICATIONS)

        /** Returns `true` when [permission] is currently granted to the app. */
        fun isPermissionGranted(permission: String): Boolean =
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

        /** Runtime permissions still missing before the foreground service can be started. */
        fun getMissingRuntimePermissions(): List<String> = runtimeServicePermissions.filterNot { isPermissionGranted(it) }

        /** `true` when every runtime permission needed by the web server service is granted. */
        fun areAllRuntimePermissionsGranted(): Boolean = getMissingRuntimePermissions().isEmpty()

        /**
         * Whether the system suggests showing a rationale for [permission]. Returns `false` when the
         * permission was permanently denied ("don't ask again"), which the caller can use to route the
         * user to the app settings screen instead.
         */
        fun shouldShowRationale(
            activity: Activity,
            permission: String,
        ): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        val isFirstLaunch: Flow<Boolean> =
            context.permissionDataStore.data
                .map { preferences ->
                    preferences[FIRST_LAUNCH_KEY] ?: true
                }

        suspend fun setFirstLaunchComplete() {
            context.permissionDataStore.edit { preferences ->
                preferences[FIRST_LAUNCH_KEY] = false
            }
        }

        suspend fun setNotificationRequested() {
            context.permissionDataStore.edit { preferences ->
                preferences[NOTIFICATION_REQUESTED_KEY] = true
            }
        }

        fun openAppSettings() {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context.startActivity(intent)
        }
    }
