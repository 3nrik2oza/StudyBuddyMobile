package com.megamaker.studybuddy.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomPasswordTextField(
    modifier: Modifier = Modifier,
    hintText: String,
    inputValue: String,
    numberOfLines: Int = 1,
    onValueChange: (String) -> Unit,
  //  isError: Boolean,
    //errorMessage: String = ""
){
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputValue,
            shape = RectangleShape,
            placeholder = { Text(text=hintText) },
            maxLines = numberOfLines,
            onValueChange = onValueChange,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            },
            textStyle = TextStyle.Default.copy()
        )
    }