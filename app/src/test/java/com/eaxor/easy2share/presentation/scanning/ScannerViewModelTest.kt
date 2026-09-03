/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.scanning

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScannerViewModelTest {
    private lateinit var viewModel: ScannerViewModel

    @Before
    fun setUp() {
        viewModel = ScannerViewModel()
        viewModel.initialize()
    }

    @Test
    fun `valid QR code stores link key and finishes scanning`() {
        viewModel.setQrCodes(
            qrCodes = listOf(DetectedQr(value = "AQIDBA==", bounds = null)),
            sourceWidth = 640,
            sourceHeight = 480,
        )

        assertArrayEquals(byteArrayOf(1, 2, 3, 4), viewModel.linkKeyRaw)
        assertTrue(viewModel.uiState.value is ScannerUiState.Finished)
    }

    @Test
    fun `handled result preserves link key and allows another scan`() {
        viewModel.setQrCodes(
            qrCodes = listOf(DetectedQr(value = "AQIDBA==", bounds = null)),
            sourceWidth = 640,
            sourceHeight = 480,
        )

        viewModel.onScanResultHandled()
        viewModel.initialize()

        assertArrayEquals(byteArrayOf(1, 2, 3, 4), viewModel.linkKeyRaw)
        assertTrue(viewModel.uiState.value is ScannerUiState.Scanning)
    }

    @Test
    fun `empty detection keeps scanning`() {
        viewModel.setQrCodes(
            qrCodes = emptyList(),
            sourceWidth = 640,
            sourceHeight = 480,
        )

        assertNull(viewModel.linkKeyRaw)
        assertTrue(viewModel.uiState.value is ScannerUiState.Scanning)
    }

    @Test
    fun `invalid QR code reports an error`() {
        viewModel.setQrCodes(
            qrCodes = listOf(DetectedQr(value = "not a link key!", bounds = null)),
            sourceWidth = 640,
            sourceHeight = 480,
        )

        assertNull(viewModel.linkKeyRaw)
        assertTrue(viewModel.uiState.value is ScannerUiState.Error)
    }
}
