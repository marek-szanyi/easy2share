package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.model.Greeting
import javax.inject.Inject

/**
 * Produces the [Greeting] shown on the home surface.
 *
 * Business logic (who we greet) lives here rather than in the UI or ViewModel,
 * keeping the presentation layer free of policy decisions.
 */
class GetGreetingUseCase @Inject constructor() {
    operator fun invoke(): Greeting = Greeting(recipient = "Android")
}

