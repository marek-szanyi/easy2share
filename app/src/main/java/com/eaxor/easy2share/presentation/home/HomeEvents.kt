/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.home

sealed class HomeEvents {
    data class StartServer(
        val linkKey: ByteArray,
        val serverPort: Int,
    ) : HomeEvents()

    object StopServer : HomeEvents()

    object EncryptionKeyMissing : HomeEvents()

    object SelectFilesForSharing : HomeEvents()

    object ClipboardShared : HomeEvents()

    object ClipboardEmpty : HomeEvents()

    object SharingNotActive : HomeEvents()

    data class NetworkConnectionChanged(
        val isConnected: Boolean,
    ) : HomeEvents()
}
