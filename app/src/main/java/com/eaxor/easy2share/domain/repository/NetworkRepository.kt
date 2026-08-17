/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.repository

/** Domain contract for discovering the address exposed by the sharing server. */
interface NetworkRepository {
    fun getIpAddress(): String?
}
