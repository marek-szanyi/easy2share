package com.eaxor.easy2share.presentation.home

public sealed class HomeUiState {
    object Stopped : HomeUiState()
    object PermissionsNeeded : HomeUiState()
    object WifiNotEnabled : HomeUiState()

    data class Error(val message: String) : HomeUiState()
    data class ServerRunning(val serverAddress: String, val serverPort: Int) : HomeUiState()
    data class ClientConnected(val clientFingerprint: String) : HomeUiState()
    data class AwaitingAuthentications(val pin: String) : HomeUiState()
    data class ClipboardSharing(val clipboardContent: String) : HomeUiState()
}
