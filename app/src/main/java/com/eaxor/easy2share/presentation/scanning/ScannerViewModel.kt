/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.scanning

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.io.encoding.Base64

@HiltViewModel
class ScannerViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiState =
            MutableStateFlow<ScannerUiState>(ScannerUiState.Initial)
        val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
        var linkKeyRaw: ByteArray? = null
            private set

        fun initialize() {
            if (_uiState.value is ScannerUiState.Initial) {
                _uiState.value = ScannerUiState.Scanning(emptyList(), 0, 0)
            }
        }

        fun setQrCodes(
            qrCodes: List<DetectedQr>,
            sourceWidth: Int,
            sourceHeight: Int,
        ) {
            if (_uiState.value !is ScannerUiState.Scanning) return

            val qrCode = qrCodes.firstOrNull { it.value.isNotBlank() }
            if (qrCode == null) {
                _uiState.value = ScannerUiState.Scanning(qrCodes, sourceWidth, sourceHeight)
                return
            }

            val decodedLinkKey =
                try {
                    Base64.decode(qrCode.value)
                } catch (_: IllegalArgumentException) {
                    _uiState.value =
                        ScannerUiState.Error("The detected QR code does not contain a valid link key.")
                    return
                }

            if (decodedLinkKey.size != LINK_KEY_SIZE_BYTES) {
                _uiState.value =
                    ScannerUiState.Error("The detected QR code does not contain a valid link key.")
                return
            }

            linkKeyRaw = decodedLinkKey
            _uiState.value = ScannerUiState.Finished(qrCode)
        }

        fun resetError() {
            _uiState.value = ScannerUiState.Scanning(emptyList(), 0, 0)
        }

        fun onScanResultHandled() {
            if (_uiState.value is ScannerUiState.Finished) {
                _uiState.value = ScannerUiState.Initial
            }
        }

        private companion object {
            const val LINK_KEY_SIZE_BYTES = 32
        }
    }
