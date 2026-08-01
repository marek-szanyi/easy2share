/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eaxor.easy2share.presentation.home.HomeScreen
import com.eaxor.easy2share.presentation.home.HomeViewModel
import com.eaxor.easy2share.presentation.main.MainViewModel
import com.eaxor.easy2share.presentation.onboarding.WelcomeScreen
import com.eaxor.easy2share.presentation.onboarding.WelcomeViewModel
import com.eaxor.easy2share.presentation.permissions.PermissionDialogs
import com.eaxor.easy2share.presentation.scanning.ScannerScreen
import com.eaxor.easy2share.presentation.scanning.ScannerViewModel
import com.eaxor.easy2share.ui.theme.Easy2shareTheme
import com.eaxor.easy2share.service.WebEngineService
import android.content.pm.ApplicationInfo
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity and composition root.
 *
 * Annotated with [AndroidEntryPoint] so Hilt can inject dependencies into the
 * Compose tree via [hiltViewModel]. It contains no business or data-access
 * logic — everything flows through ViewModels and use cases.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TEMP-DEBUG: allow starting the sharing server from adb in debuggable builds only:
        // adb shell am start -n com.eaxor.easy2share/.MainActivity --es debug_key_b64 <base64-key>
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            intent?.getStringExtra("debug_key_b64")?.let { b64 ->
                val key = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                WebEngineService.start(this, key)
            }
        }

        setContent {
            Easy2shareTheme {
                Easy2ShareApp()
            }
        }
    }
}

/**
 * Top-level app shell. Observes the [MainViewModel] to decide whether to show
 * the onboarding flow or the home surface, and scopes each screen's ViewModel.
 */
@Composable
private fun Easy2ShareApp() {
    val mainViewModel: MainViewModel = hiltViewModel<MainViewModel>()
    val scannerViewModel: ScannerViewModel = hiltViewModel()
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    var scannerVisible by rememberSaveable { mutableStateOf(false) }

    when {
        uiState.isLoading -> {
            Unit
        }

        uiState.onboardingCompleted -> {
            PermissionDialogs()
            if (scannerVisible) {
                BackHandler { scannerVisible = false }
                ScannerScreen(
                    onScanFinished = { scannerVisible = false },
                    viewModel = scannerViewModel,
                )
            } else {
                val homeViewModel: HomeViewModel = hiltViewModel()
                homeViewModel.setScannedKey(scannerViewModel.linkKeyRaw)
                HomeScreen(
                    viewModel = homeViewModel,
                    onScanQrClick = { scannerVisible = true },
                )
            }
        }

        else -> {
            val welcomeViewModel: WelcomeViewModel = hiltViewModel()
            WelcomeScreen(
                viewModel = welcomeViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
