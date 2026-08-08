/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.scanning

import android.graphics.Rect
import androidx.compose.runtime.Immutable

/**
 * A single Qr detected by ML Kit on an analysis frame.
 *
 * @param value the decoded raw value, or an empty string if it could not be decoded.
 * @param bounds the detected bounding box in the analysis image's coordinate space, or `null` if
 * ML Kit could not provide one.
 */
data class DetectedQr(
    val value: String,
    val bounds: Rect?,
)

sealed interface ScannerUiState {
    data object Initial : ScannerUiState

    /**
     * Live scanning state.
     *
     * @param qrCodes the QR codes detected on the most recent analysis frame.
     * @param sourceWidth width of the analysis frame the [qrCodes] were detected in, in pixels.
     * @param sourceHeight height of the analysis frame the [qrCodes] were detected in, in pixels.
     */
    @Immutable
    data class Scanning(
        val qrCodes: List<DetectedQr>,
        val sourceWidth: Int,
        val sourceHeight: Int,
    ) : ScannerUiState

    data class Finished(
        val qrCode: DetectedQr,
    ) : ScannerUiState

    data class Error(
        val errorMessage: String?,
    ) : ScannerUiState
}
