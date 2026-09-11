/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.Constants
import com.eaxor.easy2share.domain.usecase.GetClipboardContentUseCase
import com.eaxor.easy2share.domain.usecase.GetIpAddressUseCase
import com.eaxor.easy2share.domain.usecase.ShareClipboardContentUseCase
import com.eaxor.easy2share.domain.usecase.ShareFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the home surface.
 *
 * Pulls the [com.eaxor.easy2share.domain.model.Greeting] from the domain layer
 * and maps it into a display-ready [HomeUiState]. Formatting is a presentation
 * concern and therefore lives here rather than in the use case.
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getClipboardContent: GetClipboardContentUseCase,
        private val getIpAddress: GetIpAddressUseCase,
        private val shareClipboardContent: ShareClipboardContentUseCase,
        private val shareFiles: ShareFilesUseCase,
    ) : ViewModel() {
        val serverAddress: String
            get() {
                return "${getIpAddress()}:${Constants.DEFAULT_PORT}"
            }
        private var linkKey: ByteArray? = null

        fun setScannedKey(linkKeyRaw: ByteArray?) {
            linkKey = linkKeyRaw
            if (linkKeyRaw == null || linkKeyRaw.isEmpty()) {
                if (_uiState.value !is HomeUiState.ServerRunning) {
                    _uiState.value = HomeUiState.AwaitingSessionKey(linkKey = null, getIpAddress())
                }
                return
            }
            onSharingToggled(isSharing = true)
        }

        /**
         * Reacts to the "SHARING ON" / "NOT SHARING" switch.
         *
         * Sharing can only start once an encryption key was obtained by scanning a
         * QR code; otherwise [HomeEvents.EncryptionKeyMissing] is emitted so the UI
         * can notify the user. On a successful start the ui state is refreshed with
         * the IP address on which the web engine is reachable.
         */
        fun onSharingToggled(isSharing: Boolean) {
            if (isSharing) {
                val key = linkKey
                if (key == null || key.isEmpty()) {
                    viewModelScope.launch {
                        _events.send(HomeEvents.EncryptionKeyMissing)
                    }
                    return
                }
                _uiState.value =
                    HomeUiState.ServerRunning(
                        serverAddress = getIpAddress(),
                        serverPort = Constants.DEFAULT_PORT,
                    )
                viewModelScope.launch {
                    _events.send(HomeEvents.StartServer(key, Constants.DEFAULT_PORT))
                }
            } else if (linkKey != null && linkKey!!.isNotEmpty()) {
                _uiState.value = HomeUiState.CanStartServer(linkKey, getIpAddress())
                viewModelScope.launch {
                    _events.send(HomeEvents.StopServer)
                }
            }
        }

        /**
         * Reacts to the "SHARE CLIPBOARD" button.
         *
         * Reads the clipboard (text as-is, non-text Base64 encoded by the use
         * case) and hands it to the web engine, which encrypts it and pushes it
         * to every connected client. Emits feedback events when sharing is off
         * or the clipboard is empty.
         */
        fun onShareClipboardClicked() {
            viewModelScope.launch {
                if (_uiState.value !is HomeUiState.ServerRunning) {
                    _events.send(HomeEvents.SharingNotActive)
                    return@launch
                }
                val content = getClipboardContent()
                if (content.isNullOrEmpty()) {
                    _events.send(HomeEvents.ClipboardEmpty)
                } else {
                    shareClipboardContent(content)
                    _events.send(HomeEvents.ClipboardShared)
                }
            }
        }

        /**
         * Reacts to the "SHARE FILES" button.
         *
         * Only asks the UI to open the document picker once sharing is on, so the
         * user is never sent into a picker whose result could not be delivered.
         */
        fun onShareFilesClicked() {
            viewModelScope.launch {
                if (_uiState.value !is HomeUiState.ServerRunning) {
                    _events.send(HomeEvents.SharingNotActive)
                    return@launch
                }
                _events.send(HomeEvents.PickFiles)
            }
        }

        /**
         * Hands the documents picked by the user to the web engine, which encrypts
         * and streams them to every connected client. [fileUris] are opaque
         * location strings; an empty selection is a silent no-op.
         */
        fun onFilesSelected(fileUris: List<String>) {
            if (fileUris.isEmpty()) return
            viewModelScope.launch {
                if (_uiState.value !is HomeUiState.ServerRunning) {
                    _events.send(HomeEvents.SharingNotActive)
                    return@launch
                }
                val sharedCount = shareFiles(fileUris)
                if (sharedCount == 0) {
                    _events.send(HomeEvents.FilesShareFailed)
                } else {
                    _events.send(HomeEvents.FilesShared(sharedCount))
                }
            }
        }

        private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.PermissionsNeeded)
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        private val _events = Channel<HomeEvents>(Channel.BUFFERED)
        val events: Flow<HomeEvents> = _events.receiveAsFlow()

        init {
            viewModelScope.launch {
                _uiState.value = HomeUiState.AwaitingSessionKey(linkKey = null, getIpAddress())
            }
        }
    }
