/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.repository

import android.util.Log
import com.eaxor.easy2share.domain.repository.NetworkRepository
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import javax.inject.Inject

class NetworkRepositoryImpl
    @Inject
    constructor() : NetworkRepository {
        override fun getIpAddress(): String? =
            try {
                NetworkInterface
                    .getNetworkInterfaces()
                    ?.asSequence()
                    ?.flatMap { it.inetAddresses.asSequence() }
                    ?.firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                    ?.hostAddress
            } catch (exception: SocketException) {
                Log.e(TAG, "Unable to get IP address", exception)
                null
            } catch (exception: RuntimeException) {
                Log.e(TAG, "Unable to inspect network interfaces", exception)
                null
            }

        private companion object {
            const val TAG = "NetworkRepository"
        }
    }
