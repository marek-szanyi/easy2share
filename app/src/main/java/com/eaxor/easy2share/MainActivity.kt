package com.eaxor.easy2share

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaxor.easy2share.di.ViewModelFactory
import com.eaxor.easy2share.presentation.home.HomeScreen
import com.eaxor.easy2share.presentation.home.HomeViewModel
import com.eaxor.easy2share.presentation.main.MainViewModel
import com.eaxor.easy2share.presentation.onboarding.WelcomeScreen
import com.eaxor.easy2share.presentation.onboarding.WelcomeViewModel
import com.eaxor.easy2share.ui.theme.Easy2shareTheme

/**
 * The single Activity and composition root.
 *
 * It obtains the [ViewModelFactory] from the application-scoped container and
 * hands it to the Compose tree. It contains no business or data-access logic —
 * everything flows through ViewModels and use cases.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModelFactory = ViewModelFactory(
            (application as Easy2ShareApplication).container,
        )

        setContent {
            Easy2shareTheme {
                Easy2ShareApp(viewModelFactory = viewModelFactory)
            }
        }
    }
}

/**
 * Top-level app shell. Observes the [MainViewModel] to decide whether to show
 * the onboarding flow or the home surface, and scopes each screen's ViewModel.
 */
@Composable
private fun Easy2ShareApp(viewModelFactory: ViewModelProvider.Factory) {
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> Unit // brief splash while the first value is loaded

        uiState.onboardingCompleted -> {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = homeViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            val welcomeViewModel: WelcomeViewModel = viewModel(factory = viewModelFactory)
            WelcomeScreen(
                viewModel = welcomeViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
