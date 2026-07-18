package com.eaxor.easy2share.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eaxor.easy2share.presentation.home.HomeViewModel
import com.eaxor.easy2share.presentation.main.MainViewModel
import com.eaxor.easy2share.presentation.onboarding.WelcomeViewModel

/**
 * [ViewModelProvider.Factory] that constructs ViewModels with their domain
 * dependencies pulled from the [AppContainer].
 *
 * Living in the composition root, this is the single seam where presentation
 * types meet the wired-up graph, so ViewModels themselves stay constructor-
 * injected and free of service-locator lookups.
 */
class ViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MainViewModel::class.java) ->
            MainViewModel(container.observeOnboardingStatusUseCase) as T

        modelClass.isAssignableFrom(WelcomeViewModel::class.java) ->
            WelcomeViewModel(container.completeOnboardingUseCase) as T

        modelClass.isAssignableFrom(HomeViewModel::class.java) ->
            HomeViewModel(container.getGreetingUseCase) as T

        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

