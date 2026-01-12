package com.megamaker.studybuddy.main_screen

interface MainScreenEvent {

    data class OnFacultyNameChange(val facultyName: String): MainScreenEvent
    data class OnFacultySlugChange(val facultySlug: String): MainScreenEvent
    object CreateNewFaculty: MainScreenEvent

    object GetData: MainScreenEvent

    object ToggleCreateFacultyDialog: MainScreenEvent

}