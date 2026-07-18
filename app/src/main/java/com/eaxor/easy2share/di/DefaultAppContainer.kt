package com.eaxor.easy2share.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.eaxor.easy2share.data.local.OnboardingLocalDataSource
import com.eaxor.easy2share.data.local.OnboardingPreferencesDataSource
import com.eaxor.easy2share.data.repository.OnboardingRepositoryImpl
import com.eaxor.easy2share.domain.repository.OnboardingRepository
import com.eaxor.easy2share.domain.usecase.CompleteOnboardingUseCase
import com.eaxor.easy2share.domain.usecase.GetGreetingUseCase
import com.eaxor.easy2share.domain.usecase.ObserveOnboardingStatusUseCase

/** App-wide DataStore holding onboarding preferences. */
private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "easy2share_onboarding",
)

/**
 * Default [AppContainer] that assembles the dependency graph by hand:
 * DataStore -> local data source -> repository -> use cases.
 *
 * This manual wiring keeps the app free of DI-framework/annotation processing
 * while still enforcing the Clean Architecture dependency direction.
 */
class DefaultAppContainer(context: Context) : AppContainer {

    private val appContext = context.applicationContext

    private val onboardingLocalDataSource: OnboardingLocalDataSource =
        OnboardingPreferencesDataSource(appContext.onboardingDataStore)

    private val onboardingRepository: OnboardingRepository =
        OnboardingRepositoryImpl(onboardingLocalDataSource)

    override val observeOnboardingStatusUseCase: ObserveOnboardingStatusUseCase =
        ObserveOnboardingStatusUseCase(onboardingRepository)

    override val completeOnboardingUseCase: CompleteOnboardingUseCase =
        CompleteOnboardingUseCase(onboardingRepository)

    override val getGreetingUseCase: GetGreetingUseCase =
        GetGreetingUseCase()
}

