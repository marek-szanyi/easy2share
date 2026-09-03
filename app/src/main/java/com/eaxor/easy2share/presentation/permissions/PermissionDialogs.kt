/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.permissions

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.eaxor.easy2share.R

/**
 * Renders the permission-request flow for running the web server as a foreground service.
 *
 * This composable is a pure View: it only observes [PermissionViewModel.uiState] and forwards
 * user events. All permission decisions live in the ViewModel / repository (MVVM).
 */
@Composable
fun PermissionDialogs() {
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionViewModel = hiltViewModel<PermissionViewModel>()
    val uiState by permissionViewModel.uiState.collectAsState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { results ->
            permissionViewModel.onPermissionsResult(results, activity)
        }

    if (uiState.showFirstLaunchDialog) {
        FirstLaunchDialog(
            onEnable = {
                val permissions = uiState.permissionsToRequest
                if (permissions.isEmpty()) {
                    // Nothing to request at runtime (all install-time permissions granted).
                    permissionViewModel.onAllPermissionsAlreadyGranted()
                } else {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            },
            onSkip = { permissionViewModel.onSkip() },
        )
    }

    if (uiState.showPermissionRationale) {
        PermissionRationaleDialog(
            onDismiss = { permissionViewModel.dismissPermissionRationale() },
            onOpenSettings = { permissionViewModel.openAppSettings() },
        )
    }
}

@Composable
fun FirstLaunchDialog(
    onEnable: () -> Unit,
    onSkip: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onSkip,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.cd_notifications),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.dialog_welcome_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.dialog_welcome_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.dialog_welcome_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.action_skip))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onEnable,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.action_enable))
                }
            }
        },
        dismissButton = null,
    )
}

@Composable
fun PermissionRationaleDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.cd_settings),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.dialog_permission_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.dialog_permission_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.dialog_permission_instruction),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.dialog_open_settings))
                }
            }
        },
        dismissButton = null,
    )
}
