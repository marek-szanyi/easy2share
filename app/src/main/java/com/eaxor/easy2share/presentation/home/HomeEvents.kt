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
    ) : HomeEvents() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StartServer

            if (serverPort != other.serverPort) return false
            if (!linkKey.contentEquals(other.linkKey)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = serverPort
            result = 31 * result + linkKey.contentHashCode()
            return result
        }
    }

    object StopServer : HomeEvents()

    object EncryptionKeyMissing : HomeEvents()

    object ClipboardShared : HomeEvents()

    object ClipboardEmpty : HomeEvents()

    object SharingNotActive : HomeEvents()

}
