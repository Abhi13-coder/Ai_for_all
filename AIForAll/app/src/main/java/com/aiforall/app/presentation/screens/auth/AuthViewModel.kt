package com.aiforall.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiforall.app.domain.model.UserProfile
import com.aiforall.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Exposed so NavGraph can decide auth-flow vs main-app, and so any
    // screen can read the signed-in user's tier/role without re-fetching.
    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun signUp(displayName: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.signUp(email, password, displayName)
                .onFailure { _errorMessage.value = it.message ?: "Sign-up failed." }
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.signIn(email, password)
                .onFailure { _errorMessage.value = it.message ?: "Sign-in failed." }
            _isLoading.value = false
        }
    }

    fun signOut() = authRepository.signOut()

    fun redeemClubCode(code: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(authRepository.redeemClubCode(code))
        }
    }

    fun clearError() { _errorMessage.value = null }
}
