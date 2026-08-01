package com.eaxor.easy2share.presentation.home

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.Constants
import com.eaxor.easy2share.Easy2ShareApplication
import com.eaxor.easy2share.domain.usecase.GetGreetingUseCase
import com.eaxor.easy2share.domain.usecase.GetIpAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
class HomeViewModel @Inject constructor() : ViewModel() {

    private var linkKey : ByteArray? = null

    val getIpAddress = GetIpAddressUseCase()

    public fun setScannedKey(linkKeyRaw: ByteArray?) {
        linkKey = linkKeyRaw
        // Never downgrade an active sharing session on recomposition.
        if (_uiState.value !is HomeUiState.ServerRunning) {
            _uiState.value = HomeUiState.CanStartServer(linkKey, getIpAddress())
        }
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
                    _events.emit(HomeEvents.EncryptionKeyMissing)
                }
                return
            }
            _uiState.value =
                HomeUiState.ServerRunning(
                    serverAddress = getIpAddress(),
                    serverPort = Constants.DEFAULT_PORT,
                )
            viewModelScope.launch {
                _events.emit(HomeEvents.StartServer(key, Constants.DEFAULT_PORT))
            }
        } else {
            _uiState.value = HomeUiState.CanStartServer(linkKey, getIpAddress())
            viewModelScope.launch {
                _events.emit(HomeEvents.StopServer)
            }
        }
    }


    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.PermissionsNeeded)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvents>(replay = 0)
    val events : SharedFlow<HomeEvents> = _events.asSharedFlow()

    init {


    }



}

