package com.eaxor.easy2share.presentation.home

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.Easy2ShareApplication
import com.eaxor.easy2share.domain.usecase.GetGreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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


    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.PermissionsNeeded)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvents>(replay = 0)
    val events : SharedFlow<HomeEvents> = _events.asSharedFlow()

    init {


    }

}

