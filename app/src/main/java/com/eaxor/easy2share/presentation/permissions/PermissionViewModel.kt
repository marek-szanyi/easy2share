/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.permissions

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.data.repository.PermissionRepository
import com.eaxor.easy2share.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Immutable UI state consumed by the permission composables.
 *
 * @property showFirstLaunchDialog whether the first-launch onboarding dialog is visible.
 * @property showPermissionRationale whether the "open settings" rationale dialog is visible.
 * @property permissionsToRequest runtime permissions that still need to be requested from the user.
 * @property allPermissionsGranted `true` when every runtime permission for the service is granted.
 */
data class PermissionUiState(
    val showFirstLaunchDialog: Boolean = false,
    val showPermissionRationale: Boolean = false,
    val permissionsToRequest: List<String> = emptyList(),
    val allPermissionsGranted: Boolean = false,
)

/**
 * Owns all logic and state for acquiring the permissions required to run the web server as a
 * foreground service. The View (composables) only observes [uiState] and forwards user events;
 * it contains no permission logic itself, keeping the layers cleanly separated (MVVM).
 */
@HiltViewModel
class PermissionViewModel
    @Inject
    constructor(
        private val permissionRepository: PermissionRepository,
        private val settingsRepository: SettingsRepository,
        application: Application,
    ) : AndroidViewModel(application) {
        private val _uiState = MutableStateFlow(PermissionUiState())
        val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

        init {
            observeFirstLaunch()
            refreshPermissionState()
        }

        private fun observeFirstLaunch() {
            viewModelScope.launch {
                permissionRepository.isFirstLaunch.collect { isFirst ->
                    _uiState.update { it.copy(showFirstLaunchDialog = isFirst) }
                }
            }
        }

        /** Recomputes which runtime permissions are still missing for the service. */
        private fun refreshPermissionState() {
            val missing = permissionRepository.getMissingRuntimePermissions()
            _uiState.update {
                it.copy(
                    permissionsToRequest = missing,
                    allPermissionsGranted = missing.isEmpty(),
                )
            }
        }

        /**
         * The user chose to enable the service, but no runtime permission is required (e.g., below
         * API 33). All install-time permissions are already granted, so enable the service.
         */
        fun onAllPermissionsAlreadyGranted() {
            viewModelScope.launch {
                settingsRepository.updateBackgroundServiceEnabled(true)
                refreshPermissionState()
                dismissFirstLaunchDialog()
            }
        }

        /** The user skipped the permission request. Remember that onboarding is complete. */
        fun onSkip() {
            dismissFirstLaunchDialog()
        }

        /**
         * Handles the result delivered by the system permission dialog.
         *
         * @param results map of permission -> granted, as returned by RequestMultiplePermissions.
         * @param activity used to decide whether a denied permission was permanently denied.
         */
        fun onPermissionsResult(
            results: Map<String, Boolean>,
            activity: Activity?,
        ) {
            viewModelScope.launch {
                if (permissionRepository.areAllRuntimePermissionsGranted()) {
                    settingsRepository.updateBackgroundServiceEnabled(true)
                    _uiState.update { it.copy(showPermissionRationale = false) }
                } else {
                    // A permission that was denied and can no longer show a rationale has been
                    // permanently denied; guide the user to the app settings screen.
                    val permanentlyDenied =
                        results
                            .filterValues { granted -> !granted }
                            .keys
                            .any { permission ->
                                activity != null &&
                                    !permissionRepository.shouldShowRationale(
                                        activity,
                                        permission,
                                    )
                            }
                    _uiState.update { it.copy(showPermissionRationale = permanentlyDenied) }
                }
                refreshPermissionState()
                dismissFirstLaunchDialog()
            }
        }

        /** Opens the system app-settings screen so the user can grant permanently denied permissions. */
        fun openAppSettings() {
            permissionRepository.openAppSettings()
            _uiState.update { it.copy(showPermissionRationale = false) }
        }

        fun dismissPermissionRationale() {
            _uiState.update { it.copy(showPermissionRationale = false) }
        }

        private fun dismissFirstLaunchDialog() {
            _uiState.update { it.copy(showFirstLaunchDialog = false) }
            viewModelScope.launch {
                permissionRepository.setNotificationRequested()
                permissionRepository.setFirstLaunchComplete()
            }
        }
    }
