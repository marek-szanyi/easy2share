package com.eaxor.easy2share.presentation.home

sealed class HomeEvents {
    object StartServer : HomeEvents()
    object StopServer : HomeEvents()
    object SelectFilesForSharing : HomeEvents()

    data class NetworkConnectionChanged(val isConnected: Boolean) : HomeEvents()
}