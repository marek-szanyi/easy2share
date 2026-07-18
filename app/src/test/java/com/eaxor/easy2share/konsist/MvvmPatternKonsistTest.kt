package com.eaxor.easy2share.konsist

import androidx.lifecycle.ViewModel
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withParentClassOf
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

/**
 * Verifies the MVVM presentation pattern is applied consistently:
 *
 *  - Every ViewModel lives in the presentation layer and carries the
 *    `ViewModel` suffix (and vice-versa).
 *  - ViewModels never reach into the Android UI toolkit (Compose / Views),
 *    keeping them free of rendering concerns and unit-testable on the JVM.
 */
class MvvmPatternKonsistTest {

    @Test
    fun `classes extending ViewModel reside in the presentation layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .assertTrue { it.resideInPackage("..presentation..") }
    }

    @Test
    fun `classes extending ViewModel are named with a ViewModel suffix`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .assertTrue { it.hasNameEndingWith("ViewModel") }
    }

    @Test
    fun `classes with a ViewModel suffix actually extend the Android ViewModel`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { it.hasParentClassOf(ViewModel::class) }
    }

    @Test
    fun `view models do not depend on the Android UI toolkit`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertFalse { viewModel ->
                viewModel.containingFile.hasImport { import ->
                    import.name.startsWith("androidx.compose") ||
                        import.name.startsWith("android.widget") ||
                        import.name.startsWith("android.view")
                }
            }
    }
}

