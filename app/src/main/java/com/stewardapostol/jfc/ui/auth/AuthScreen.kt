package com.stewardapostol.jfc.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.stewardapostol.jfc.data.local.PREF
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import com.stewardapostol.jfc.R

@Composable
fun AuthScreen(viewModel: JWTAuthViewModel, onLoginSuccess: () -> Unit) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val state by viewModel.loginState.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (PREF.USERNAME.isNotEmpty()) {
            email = PREF.USERNAME
            rememberMe = true
        }
    }

    LaunchedEffect(state) {
        if (state == "Registered successfully! Please login.") {
            Toast.makeText(context, state, Toast.LENGTH_LONG).show()
            isRegisterMode = false
            password = ""
            confirmPassword = ""
        }
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF1F40), Color(0xFFCC0724), Color(0x56FFFFFF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.jblogo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 10.dp)
            )
            Text(
                text = "JFC Contact and Tasks",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!isRegisterMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                                Text("Remember Me", fontSize = 14.sp)
                            }
                            TextButton(onClick = {
                                if (email.isNotEmpty()) viewModel.forgotPassword(email)
                                else Toast.makeText(context, "Enter email first", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Forgot Password?")
                            }
                        }
                    }

                    if (isRegisterMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            enabled = !isLoading,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    } else {
                        Button(
                            onClick = {
                                if (isRegisterMode) {
                                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    } else if (password != confirmPassword) {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.register(name, email, password)
                                    }
                                } else {
                                    if (email.isNotEmpty() && password.isNotEmpty()) {
                                        if (rememberMe) {
                                            PREF.USERNAME = email
                                        } else {
                                            PREF.USERNAME = ""
                                        }
                                        viewModel.login(email, password)
                                    } else {
                                        Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isRegisterMode) "Register" else "Login")
                        }
                    }

                    TextButton(
                        onClick = { isRegisterMode = !isRegisterMode },
                        enabled = !isLoading
                    ) {
                        Text(
                            text = if (isRegisterMode) "Already registered? Login" else "New here? Register",
                            color = Color(0xFF7F7FD5)
                        )
                    }

                    if (state.isNotEmpty()) {
                        Text(
                            text = state,
                            color = if (state.contains("Error") || state.contains("Failed")) Color.Red else Color.DarkGray,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}