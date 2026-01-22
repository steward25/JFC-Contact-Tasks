package com.stewardapostol.jfc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class JWTAuthViewModel(application: Application) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _loginState = MutableStateFlow("")
    val loginState = _loginState.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val currentUserEmail = auth.currentUser?.email
    val currentUserName = auth.currentUser?.displayName
    private val _idToken = MutableStateFlow<String?>(null)
    val idToken = _idToken.asStateFlow()

    fun checkLoggedInStatus() {
        _isLoggedIn.value = auth.currentUser != null
        if (_isLoggedIn.value) fetchJwt()
    }

    fun clearLoginState() {
        _loginState.value = ""
    }

    // --- 1. UPDATE PROFILE (Name/Email) ---
    fun updateProfile(newName: String, newEmail: String) {
        val user = auth.currentUser ?: return
        _isLoading.value = true

        // Update Display Name
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }

        user.updateProfile(profileUpdates).addOnCompleteListener { nameTask ->
            if (nameTask.isSuccessful) {
                // Update Email (Note: Firebase may require re-authentication for this)
                user.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                    _isLoading.value = false
                    if (emailTask.isSuccessful) {
                        _loginState.value = "Profile updated successfully!"
                    } else {
                        _loginState.value = "Email update failed: ${emailTask.exception?.message}"
                    }
                }
            } else {
                _isLoading.value = false
                _loginState.value = "Name update failed."
            }
        }
    }

    // --- 2. UPDATE PASSWORD ---
    fun updatePassword(currentPass: String, newPass: String) {
        val user = auth.currentUser ?: return
        _isLoading.value = true

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPass)

        user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
            if (reAuthTask.isSuccessful) {
                user.updatePassword(newPass).addOnCompleteListener { passTask ->
                    _isLoading.value = false
                    if (passTask.isSuccessful) {
                        _loginState.value = "SUCCESS_PASSWORD_UPDATED"
                        // Use a unique string to trigger specific UI logic
                    } else {
                        _loginState.value = "Error: ${passTask.exception?.message}"
                    }
                }
            } else {
                _isLoading.value = false
                _loginState.value = "Current password incorrect."
            }
        }
    }

    // --- 3. DELETE ACCOUNT ---
    fun deleteAccount(password: String, onDeleted: () -> Unit) {
        val user = auth.currentUser ?: return
        _isLoading.value = true

        // Re-authenticate before sensitive operation
        val credential = EmailAuthProvider.getCredential(user.email!!, password)

        user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
            if (reAuthTask.isSuccessful) {
                user.delete().addOnCompleteListener { deleteTask ->
                    _isLoading.value = false
                    if (deleteTask.isSuccessful) {
                        logout()
                        onDeleted()
                    } else {
                        _loginState.value = "Account deletion failed."
                    }
                }
            } else {
                _isLoading.value = false
                _loginState.value = "Incorrect password for deletion."
            }
        }
    }

    /**
     * Registers a user and updates their display name (JWT profile)
     */
    fun register(name: String, email: String, pass: String) {
        _isLoading.value = true
        _loginState.value = "Creating account..."

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    auth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        auth.signOut()
                        _isLoggedIn.value = false
                        _loginSuccess.value = false
                        _isLoading.value = false
                        _loginState.value = "Registered successfully! Please login."
                    }
                } else {
                    _isLoading.value = false
                    _loginState.value = "Error: ${task.exception?.message}"
                }
            }
    }

    /**
     * Signs in and automatically fetches the fresh JWT ID Token
     */
    fun login(email: String, pass: String) {
        _isLoading.value = true
        _loginState.value = "Signing in..."

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isLoggedIn.value = true
                    fetchJwt() // fetchJwt will set _loginSuccess to true once token is ready
                } else {
                    _isLoading.value = false
                    _loginState.value = "Login Failed: ${task.exception?.message}"
                }
            }
    }

    /**
     * Fetches the actual JWT string (ID Token).
     * Setting 'forceRefresh' to true ensures you get a non-expired token.
     */
    fun fetchJwt() {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            _isLoading.value = false
            if (task.isSuccessful) {
                _idToken.value = task.result.token
                _loginSuccess.value = true
            } else {
                _loginState.value = "Failed to retrieve secure token."
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _loginState.value = "Please enter your email first."
            return
        }
        _isLoading.value = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _loginState.value = "Reset link sent to your email!"
                } else {
                    _loginState.value = "Error: ${task.exception?.message}"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _loginSuccess.value = false
        _idToken.value = null
        _loginState.value = ""
    }
}