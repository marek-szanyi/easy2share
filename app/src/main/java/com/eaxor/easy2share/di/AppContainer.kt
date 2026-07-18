package com.eaxor.easy2share.di

import com.eaxor.easy2share.domain.usecase.CompleteOnboardingUseCase
import com.eaxor.easy2share.domain.usecase.GetGreetingUseCase
import com.eaxor.easy2share.domain.usecase.ObserveOnboardingStatusUseCase

/**
 * Dependency container (composition root).
 *
 * Exposes the domain use cases the presentation layer needs. Concrete data/
 * domain wiring is hidden inside [DefaultAppContainer], so the UI depends only
 * on these abstractions — never on the data layer directly.
 */
interface AppContainer {
    val observeOnboardingStatusUseCase: ObserveOnboardingStatusUseCase
    val completeOnboardingUseCase: CompleteOnboardingUseCase
    val getGreetingUseCase: GetGreetingUseCase
}

