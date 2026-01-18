package com.megamaker.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.megamaker.studybuddy.auth.LoginScreen
import com.megamaker.studybuddy.auth.LoginViewModel
import com.megamaker.studybuddy.data.AuthStore
import com.megamaker.studybuddy.forum.ForumEvent
import com.megamaker.studybuddy.forum.ForumScreen
import com.megamaker.studybuddy.forum.ForumViewModel
import com.megamaker.studybuddy.main_screen.ui.MainScreen
import com.megamaker.studybuddy.main_screen.ui.MainScreenViewModel
import com.megamaker.studybuddy.thread.ThreadScreen
import com.megamaker.studybuddy.thread.ThreadScreenEvent
import com.megamaker.studybuddy.thread.ThreadViewModel
import com.megamaker.studybuddy.ui.theme.StudyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val mainVm: MainScreenViewModel by viewModels()
        val loginVm: LoginViewModel by viewModels()
        val forumVm: ForumViewModel by viewModels()
        val threadVm: ThreadViewModel by viewModels()

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
                    MainScreen(
                        forumVm,
                        threadVm,
                    )

                }
            }
        }
    }
}
