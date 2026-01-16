package com.megamaker.studybuddy.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.megamaker.studybuddy.auth.components.CustomPasswordTextField

@Composable
fun LoginScreen(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit

) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Admin Login", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        CustomPasswordTextField(
            inputValue = state.password,
            onValueChange = onPasswordChange,
            hintText = "Password"
        )

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = onLogin,
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.loading) "Logging in..." else "Login")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(state = LoginState(), onEmailChange = {}, onPasswordChange = {}, onLogin = {})
}