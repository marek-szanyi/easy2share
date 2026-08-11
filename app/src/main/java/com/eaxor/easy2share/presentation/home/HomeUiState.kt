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
        val connectedClients: List<ConnectedClient> = emptyList(),
    ) : HomeUiState()

    data class AwaitingSessionKey(
        val linkKey: ByteArray? = null,
        val serverAddress: String,
        val serverPort: Int = 8080,
    ) : HomeUiState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AwaitingSessionKey

            if (serverPort != other.serverPort) return false
            if (!linkKey.contentEquals(other.linkKey)) return false
            if (serverAddress != other.serverAddress) return false

            return true
        }

        override fun hashCode(): Int {
            var result = serverPort
            result = 31 * result + (linkKey?.contentHashCode() ?: 0)
            result = 31 * result + serverAddress.hashCode()
            return result
        }
    }

    data class ClipboardSharing(
        val clipboardContent: String,
    ) : HomeUiState()

    data class CanStartServer(
        val linkKey: ByteArray?,
        val serverAddress: String,
        val serverPort: Int = 8080,
    ) : HomeUiState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CanStartServer

            if (serverPort != other.serverPort) return false
            if (!linkKey.contentEquals(other.linkKey)) return false
            if (serverAddress != other.serverAddress) return false

            return true
        }

        override fun hashCode(): Int {
            var result = serverPort
            result = 31 * result + (linkKey?.contentHashCode() ?: 0)
            result = 31 * result + serverAddress.hashCode()
            return result
        }
    }
}
