package com.eaxor.easy2share

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eaxor.easy2share.onboarding.OnboardingPreferences
import com.eaxor.easy2share.onboarding.WelcomeScreen
import com.eaxor.easy2share.ui.theme.Easy2shareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val onboardingPreferences = OnboardingPreferences(this)
        setContent {
            Easy2shareTheme {
                var welcomeCompleted by remember {
                    mutableStateOf(onboardingPreferences.isWelcomeCompleted)
                }
                if (!welcomeCompleted) {
                    WelcomeScreen(
                        onFinished = {
                            onboardingPreferences.isWelcomeCompleted = true
                            welcomeCompleted = true
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, device = "id:pixel_9a", name = "prev")
@Composable
fun GreetingPreview() {
    Easy2shareTheme {
        Greeting("Android")
    }
}