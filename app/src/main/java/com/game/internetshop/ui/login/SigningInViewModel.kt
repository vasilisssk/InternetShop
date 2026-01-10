package com.game.internetshop.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.domain.usecase.SignInUseCase
import kotlinx.coroutines.launch

class SigningInViewModel(private val signInUseCase: SignInUseCase) : ViewModel() {
    private val _uiState = MutableLiveData(SigningInUiState())
    val uiState: LiveData<SigningInUiState> = _uiState

    data class SigningInUiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isSigningInSuccess: Boolean = false
    )

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value?.copy(
            email = email,
            errorMessage = null  // Очищаем ошибку при изменении
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value?.copy(
            password = password,
            errorMessage = null  // Очищаем ошибку при изменении
        )
    }

    fun onLoginClicked() {
        // Устанавливаем состояние загрузки
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            errorMessage = null,
            isSigningInSuccess = false
        )

        viewModelScope.launch {
            val result = signInUseCase(
                _uiState.value?.email ?: "",
                _uiState.value?.password ?: ""
            )

            // Обновляем состояние на основе результата
            _uiState.value = when (result) {
                is AuthResult.Success -> {
                    _uiState.value?.copy(
                        isLoading = false,
                        isSigningInSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> _uiState.value?.copy(isLoading = false)
            }
        }
    }

    // Дополнительные методы для управления состоянием
    fun clearError() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }

    fun resetSuccessState() {
        _uiState.value = _uiState.value?.copy(isSigningInSuccess = false)
    }
}