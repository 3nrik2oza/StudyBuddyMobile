package com.megamaker.studybuddy.main_screen

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.megamaker.studybuddy.data.Faculty

@Composable
fun MainScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
){

    if(state.showCreateFacultyDialog){
        AddNewFacultyDialog(
            state = state,
            onEvent = onEvent
        )
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))

        Text(
            "StudyBuddy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(

        ) {
            itemsIndexed(state.faculties){ index, item ->
                FacultyCard(item)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            onEvent(MainScreenEvent.ToggleCreateFacultyDialog)
                        }
                    ){
                        Text("Add new faculty")
                    }
                }
            }
        }
    }
}
@Composable
fun AddNewFacultyDialog(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
){
    AlertDialog(
        onDismissRequest = { onEvent(MainScreenEvent.ToggleCreateFacultyDialog) },
        confirmButton = {},
        dismissButton = {},
        modifier = Modifier,
        containerColor = Color.White,
        title = {
            Text(
                text = "Create faculty",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.facultyName,
                    placeholder = { Text(text = "Enter faculty name...", color = Color.LightGray) },
                    maxLines = 1,
                    onValueChange = { if(it.length < 30){
                        onEvent(MainScreenEvent.OnFacultyNameChange(it))
                    } },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.facultySlug,
                    placeholder = { Text(text = "Enter faculty slug", color = Color.LightGray) },
                    maxLines = 1,
                    onValueChange = { if(it.length < 30){
                        onEvent(MainScreenEvent.OnFacultySlugChange(it))
                    } },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                )

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            onEvent(MainScreenEvent.CreateNewFaculty)
                        }
                    ){
                        Text("Create new faculty")
                    }
                }

            }
        }
    )
}


@Composable
fun FacultyCard(
    faculty: Faculty,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
            //.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = faculty.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}