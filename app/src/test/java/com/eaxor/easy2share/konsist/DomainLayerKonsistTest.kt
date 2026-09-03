/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

/**
 * Guards the purity and conventions of the innermost (domain) layer.
 */
class DomainLayerKonsistTest {
    private companion object {
        const val DOMAIN_PACKAGE = "com.eaxor.easy2share.domain"
    }

    @Test
    fun `domain layer does not depend on the Android framework`() {
        Konsist
            .scopeFromProject()
            .files
            .filter {
                it.packagee
                    ?.name
                    .orEmpty()
                    .startsWith(DOMAIN_PACKAGE)
            }.assertFalse { file ->
                file.hasImport { import ->
                    import.name.startsWith("android.") || import.name.startsWith("androidx.")
                }
            }
    }

    @Test
    fun `domain layer does not depend on the data or presentation layers`() {
        Konsist
            .scopeFromProject()
            .files
            .filter {
                it.packagee
                    ?.name
                    .orEmpty()
                    .startsWith(DOMAIN_PACKAGE)
            }.assertFalse { file ->
                file.hasImport { import ->
                    import.name.startsWith("com.eaxor.easy2share.data") ||
                        import.name.startsWith("com.eaxor.easy2share.presentation")
                }
            }
    }

    @Test
    fun `use cases reside in the domain use case package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..domain.usecase..") }
    }

    @Test
    fun `use cases expose their behaviour through a single operator invoke`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { useCase ->
                useCase.hasFunction { function ->
                    function.name == "invoke" && function.hasOperatorModifier
                }
            }
    }
}
