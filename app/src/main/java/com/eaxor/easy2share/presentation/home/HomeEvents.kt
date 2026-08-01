package com.eaxor.easy2share.presentation.home

sealed class HomeEvents {
    data class StartServer(val linkKey: ByteArray, val serverPort: Int) : HomeEvents()
    object StopServer : HomeEvents()
    object EncryptionKeyMissing : HomeEvents()
    object SelectFilesForSharing : HomeEvents()

    data class NetworkConnectionChanged(val isConnected: Boolean) : HomeEvents()
}