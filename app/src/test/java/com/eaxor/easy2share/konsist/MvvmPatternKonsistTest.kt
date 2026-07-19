package com.eaxor.easy2share.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
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
 *
 * Parenthood is checked by parent name rather than [Class]-based matching:
 * Konsist's class-vs-interface classification cannot resolve the `ViewModel`
 * super-type on classes that declare an annotated primary constructor (e.g.
 * Hilt's `@Inject constructor`), so a name-based check keeps these guards
 * accurate for constructor-injected ViewModels.
 */
class MvvmPatternKonsistTest {

    @Test
    fun `classes extending ViewModel reside in the presentation layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { it.extendsViewModel() }
            .assertTrue { it.resideInPackage("..presentation..") }
    }

    @Test
    fun `classes extending ViewModel are named with a ViewModel suffix`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { it.extendsViewModel() }
            .assertTrue { it.hasNameEndingWith("ViewModel") }
    }

    @Test
    fun `classes with a ViewModel suffix actually extend the Android ViewModel`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { it.extendsViewModel() }
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

/** True when the class declares the Android `ViewModel` as a direct super-type. */
private fun KoClassDeclaration.extendsViewModel(): Boolean =
    parents().any { it.name == "ViewModel" }

