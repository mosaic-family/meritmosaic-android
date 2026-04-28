// SettingsViewModel.kt
package com.katafract.meritmosaic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.katafract.meritmosaic.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = AuthService(app)

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email.asStateFlow()

    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            _displayName.value = auth.displayNameFlow.first()
            _email.value       = auth.emailFlow.first()
            _signedIn.value    = auth.currentToken() != null
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun signIn() {
        viewModelScope.launch {
            auth.signInStub()
            _displayName.value = auth.displayNameFlow.first()
            _email.value       = auth.emailFlow.first()
            _signedIn.value    = true
        }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _displayName.value = null
            _email.value       = null
            _signedIn.value    = false
        }
    }
}
