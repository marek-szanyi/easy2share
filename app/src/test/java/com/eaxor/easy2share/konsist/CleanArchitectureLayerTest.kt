/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.Test

/**
 * Verifies the Clean Architecture dependency rule across the three layers:
 *
 *  - Domain depends on nothing (it is the innermost, framework-free layer).
 *  - Data depends only on Domain (it implements Domain abstractions).
 *  - Presentation depends only on Domain (it consumes use cases via ViewModels).
 *
 * The composition root (`di`) and the app entry point are intentionally not
 * modelled as layers: wiring everything together is exactly their job.
 */
class CleanArchitectureLayerTest {
    @Test
    fun `clean architecture layers respect the dependency rule`() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val domain = Layer("Domain", "com.eaxor.easy2share.domain..")
                val data = Layer("Data", "com.eaxor.easy2share.data..")
                val presentation = Layer("Presentation", "com.eaxor.easy2share.presentation..")

                domain.dependsOnNothing()
                data.dependsOn(domain)
                presentation.dependsOn(domain)
            }
    }
}
