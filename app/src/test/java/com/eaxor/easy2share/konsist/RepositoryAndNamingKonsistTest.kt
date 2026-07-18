package com.eaxor.easy2share.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

/**
 * Verifies the repository abstraction and cross-layer naming conventions that
 * make the Clean Architecture split enforceable and self-documenting.
 */
class RepositoryAndNamingKonsistTest {

    @Test
    fun `repository abstractions are interfaces that live in the domain layer`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..domain..") }
    }

    @Test
    fun `repository implementations live in the data layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { it.resideInPackage("..data..") }
    }

    @Test
    fun `repository implementations implement a domain repository interface`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { impl ->
                impl.hasParentInterface { parent -> parent.name.endsWith("Repository") }
            }
    }

    @Test
    fun `presentation layer never depends on the data layer`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { it.packagee?.name.orEmpty().startsWith("com.eaxor.easy2share.presentation") }
            .assertFalse { file ->
                file.hasImport { import -> import.name.startsWith("com.eaxor.easy2share.data") }
            }
    }
}

