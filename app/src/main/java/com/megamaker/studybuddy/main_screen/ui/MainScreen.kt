package com.megamaker.studybuddy.main_screen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.megamaker.studybuddy.data.Faculty
import com.megamaker.studybuddy.forum.ForumEvent
import com.megamaker.studybuddy.forum.ForumScreen
import com.megamaker.studybuddy.forum.ForumViewModel
import com.megamaker.studybuddy.thread.ThreadScreen
import com.megamaker.studybuddy.thread.ThreadScreenEvent
import com.megamaker.studybuddy.thread.ThreadViewModel

@Composable
fun MainScreen(
    forumVm: ForumViewModel,
    threadVm: ThreadViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "ForumScreen") {

        composable("ForumScreen") { backStackEntry ->
            val state by forumVm.state.collectAsState()

            LaunchedEffect(backStackEntry) {
                forumVm.onEvent(ForumEvent.LoadThreads)
            }

            ForumScreen(
                state = state,
                onEvent = {
                    when(it){
                        is ForumEvent.OpenThread -> {
                            navController.navigate("ThreadScreen")
                            threadVm.onEvent(ThreadScreenEvent.OpenThreadScreen(it.threadId))
                        }
                        else -> {}
                    }
                    forumVm.onEvent(it)
                }
            )
        }

        composable("ThreadScreen") {
            val state by threadVm.state.collectAsState()
            ThreadScreen(
                state = state,
                onEvent = {
                    when(it){
                        is ThreadScreenEvent.OnBackClick -> {
                            navController.popBackStack()
                        }
                        else -> {}
                    }
                    threadVm.onEvent(it)
                }
            )
        }
    }
}
