package com.megamaker.studybuddy.main_screen

import com.megamaker.studybuddy.data.Faculty

data class MainScreenState(
    val urlString: String = "https://studdybuddyapp.azurewebsites.net/api/v1/Faculties",
    val faculties: List<Faculty> = emptyList(),
    val error: String = "",

    val showCreateFacultyDialog: Boolean = false,
    val facultyName: String = "",
    val facultySlug: String = "",
)