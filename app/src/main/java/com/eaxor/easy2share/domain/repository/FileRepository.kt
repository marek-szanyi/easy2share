/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.repository

/** Domain contract for sharing files with the currently connected clients. */
interface FileRepository {
    /**
     * Queues every readable file behind [fileUris] for encrypted delivery and
     * returns how many of them could actually be read.
     *
     * Locations are passed as opaque strings so the domain layer stays free of
     * platform types.
     */
    suspend fun share(fileUris: List<String>): Int
}
