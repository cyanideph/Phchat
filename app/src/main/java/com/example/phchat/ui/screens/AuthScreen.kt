package com.example.phchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phchat.data.AuthState
import com.example.phchat.ui.theme.PhBluePrimary
import com.example.phchat.ui.theme.PhRedSecondary
import com.example.phchat.viewmodel.PhchatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: PhchatViewModel,
    onSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("NCR") }

    var userMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Retro Uzzap / Phchat Header
            Surface(
                color = PhBluePrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "", fontSize = 38.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phchat",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = PhBluePrimary
            )
            Text(
                text = "A community built for connection",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Switch between Sign In and Register
            TabRow(
                selectedTabIndex = if (isRegisterMode) 1 else 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = !isRegisterMode,
                    onClick = { isRegisterMode = false; userMessage = null },
                    text = { Text("Pumasok (Sign In)") }
                )
                Tab(
                    selected = isRegisterMode,
                    onClick = { isRegisterMode = true; userMessage = null },
                    text = { Text("Mag-rehistro (Register)") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error or status banner
            if (userMessage != null || authState is AuthState.Error) {
                val errorMsg = userMessage ?: (authState as? AuthState.Error)?.message.orEmpty()
                Surface(
                    color = PhRedSecondary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = PhRedSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = errorMsg, color = PhRedSecondary, fontSize = 13.sp)
                    }
                }
            }

            // Input Fields
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().testTag("auth_email")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().testTag("auth_password")
            )

            if (isRegisterMode) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username (Handle)") },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("auth_username")
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("auth_display_name")
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = selectedProvince,
                    onValueChange = { selectedProvince = it },
                    label = { Text("Province Code (hal. NCR, CEB, DVO, PAM)") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("auth_province")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        userMessage = "Pakilagay ang email at password."
                        return@Button
                    }
                    isSubmitting = true
                    if (isRegisterMode) {
                        viewModel.signUp(
                            email = email,
                            password = password,
                            username = if (username.isNotBlank()) username else email.substringBefore("@"),
                            displayName = if (displayName.isNotBlank()) displayName else email.substringBefore("@"),
                            province = selectedProvince
                        ) { resultMsg ->
                            isSubmitting = false
                            userMessage = resultMsg
                        }
                    } else {
                        viewModel.signIn(email, password) { success, msg ->
                            isSubmitting = false
                            if (!success) {
                                userMessage = msg
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PhBluePrimary),
                enabled = !isSubmitting && authState !is AuthState.Loading
            ) {
                if (isSubmitting || authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = if (isRegisterMode) "Create account" else "Sign in",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Konektado sa Supabase ap-northeast-1 (Tokyo)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
