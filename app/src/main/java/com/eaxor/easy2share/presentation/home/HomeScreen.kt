package com.eaxor.easy2share.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eaxor.easy2share.ui.theme.Easy2shareTheme

/**
 * Stateful home surface. Collects [HomeViewModel.uiState] and renders it,
 * keeping this composable free of any business or data-access logic.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(greeting = uiState.greeting, modifier = modifier)
}

/**
 * Stateless home content — trivially previewable and testable.
 */
@Composable
fun HomeScreen(
    greeting: String,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Text(
            text = greeting,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_9a", name = "Home")
@Composable
private fun HomeScreenPreview() {
    Easy2shareTheme {
        HomeScreen(greeting = "Hello Android!")
    }
}

