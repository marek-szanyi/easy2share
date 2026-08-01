/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.home

data class ConnectedClient(
    val id: String,
    val displayName: String,
    val address: String,
    val fingerprint: String,
)

sealed class HomeUiState {
    data object Stopped : HomeUiState()

    data object PermissionsNeeded : HomeUiState()

    data object WifiNotEnabled : HomeUiState()

    data object Scanning : HomeUiState()

    data class Error(
        val message: String,
    ) : HomeUiState()

    data class ServerRunning(
        val serverAddress: String,
        val serverPort: Int,
        val authPin: String? = null,
        val connectedClients: List<ConnectedClient> = emptyList(),
    ) : HomeUiState()

    data class AwaitingAuthentications(
        val pin: String,
    ) : HomeUiState()

    data class ClipboardSharing(
        val clipboardContent: String,
    ) : HomeUiState()

    data class CanStartServer(
        val linkKey: ByteArray?,
        val ipAddress : String,
        val serverPort: Int = 8080,
    ) : HomeUiState()
}
