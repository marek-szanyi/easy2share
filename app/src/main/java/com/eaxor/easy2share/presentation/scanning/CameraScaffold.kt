/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.scanning

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraScaffold(
    permissions: List<String>,
    modifier: Modifier = Modifier,
    rationale: String = "Camera access is required to scan QR codes.",
    deniedMessage: String = "Camera access was denied. Enable it in Settings to scan QR codes.",
    content: @Composable BoxScope.() -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    fun allGranted(): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    var hasPermission by remember { mutableStateOf(allGranted()) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { results ->
            hasPermission = allGranted()
            if (!hasPermission && activity != null) {
                permanentlyDenied =
                    results
                        .filterValues { granted -> !granted }
                        .keys
                        .any { permission ->
                            !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                        }
            }
        }

    // Re-check when returning from the system settings screen.
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    hasPermission = allGranted()
                    if (hasPermission) permanentlyDenied = false
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(permissions.toTypedArray())
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            if (hasPermission) {
                content()
            } else {
                PermissionCard(
                    message = if (permanentlyDenied) deniedMessage else rationale,
                    buttonText = if (permanentlyDenied) "Open Settings" else "Grant Permission",
                    onClick = {
                        if (permanentlyDenied) {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                },
                            )
                        } else {
                            launcher.launch(permissions.toTypedArray())
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.PermissionCard(
    message: String,
    buttonText: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = buttonText, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
