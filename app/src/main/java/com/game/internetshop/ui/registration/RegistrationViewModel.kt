package com.game.internetshop.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class RegistrationViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    private val _uiState = MutableLiveData(RegistrationUiState())
    val uiState: LiveData<RegistrationUiState> = _uiState

    data class RegistrationUiState(
        val name: String = "",
        val phoneNumber: String = "",
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isRegistrationSuccess: Boolean = false
    )

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value?.copy(
            name = name,
            errorMessage = null  // Очищаем ошибку при изменении
        )
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.value = _uiState.value?.copy(
            phoneNumber = phoneNumber,
            errorMessage = null  // Очищаем ошибку при изменении
        )
    }

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

    fun onRegisterClicked() {
        // Устанавливаем состояние загрузки
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            errorMessage = null,
            isRegistrationSuccess = false
        )

        viewModelScope.launch {
            val result = registerUseCase(
                _uiState.value?.name ?: "",
                _uiState.value?.phoneNumber ?: "",
                _uiState.value?.email ?: "",
                _uiState.value?.password ?: ""
            )

            // Обновляем состояние на основе результата
            _uiState.value = when (result) {
                is AuthResult.Success -> {
                    _uiState.value?.copy(
                        isLoading = false,
                        isRegistrationSuccess = true
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
        _uiState.value = _uiState.value?.copy(isRegistrationSuccess = false)
    }
}