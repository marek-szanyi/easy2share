/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.webengine

import java.io.ByteArrayInputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WebEngineStateTest {
    @Test
    fun `resetWebEngineState drains queued clipboard and file shares`() =
        runTest {
            resetWebEngineState()
            notifyClients.trySend("stale clipboard")
            shareFiles.trySend(
                SharedFile(
                    fileName = "stale.txt",
                    mimeType = "text/plain",
                    size = 1,
                    openStream = { ByteArrayInputStream(byteArrayOf(1)) },
                ),
            )

            resetWebEngineState()

            assertTrue(notifyClients.tryReceive().isFailure)
            assertTrue(shareFiles.tryReceive().isFailure)

            assertTrue(notifyClients.trySend("fresh clipboard").isSuccess)
            assertEquals("fresh clipboard", notifyClients.tryReceive().getOrNull())
        }
}
