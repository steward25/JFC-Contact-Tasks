package com.stewardapostol.jfc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
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

    private val _idToken = MutableStateFlow<String?>(null)
    val idToken = _idToken.asStateFlow()

    fun checkLoggedInStatus() {
        _isLoggedIn.value = auth.currentUser != null
        if (_isLoggedIn.value) fetchJwt()
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