package com.megamaker.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.megamaker.studybuddy.auth.LoginScreen
import com.megamaker.studybuddy.auth.LoginViewModel
import com.megamaker.studybuddy.data.AuthStore
import com.megamaker.studybuddy.main_screen.MainScreen
import com.megamaker.studybuddy.main_screen.MainScreenViewModel
import com.megamaker.studybuddy.ui.theme.StudyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainVm: MainScreenViewModel by viewModels()
        val loginVm: LoginViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            StudyBuddyTheme {
                val token by AuthStore(applicationContext).tokenFlow.collectAsState(initial = null)

                if (token.isNullOrBlank()) {
                    val s by loginVm.state.collectAsState()
                    LoginScreen(
                        state = s,
                        onEmailChange = loginVm::onEmailChange,
                        onPasswordChange = loginVm::onPasswordChange,
                        onLogin = { loginVm.login { } }
                    )
                } else {
                    val state by mainVm.state.collectAsState()
                    MainScreen(
                        state = state,
                        onEvent = mainVm::onEvent,
                        onLogout = { mainVm.logout() }
                    )
                }
            }
        }
    }
}
