package com.stewardapostol.jfc.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.R
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.stewardapostol.jfc.ui.viewmodel.JWTAuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeaderBar(
    authViewModel: JWTAuthViewModel,
    onLogout: () -> Unit,
    onUpdateProfile: () -> Unit,
    onChangePassword: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    // Extract first name from Firebase Display Name
    val fullName = authViewModel.currentUserName ?: "User"
    val firstName = fullName.split(" ").firstOrNull() ?: "User"

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.jblogo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("JFC Contact & Tasks", style = MaterialTheme.typography.titleSmall)
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                // Display the First Name
                Text(
                    text = "Hi, $firstName",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(end = 4.dp)
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Open Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Update Profile") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            onClick = {
                                showMenu = false
                                onUpdateProfile()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Change Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            onClick = {
                                showMenu = false
                                onChangePassword()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Sign Out") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                            onClick = {
                                showMenu = false
                                authViewModel.logout()
                                Toast.makeText(context, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                                onLogout()
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFCC0724),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
        )
    )
}