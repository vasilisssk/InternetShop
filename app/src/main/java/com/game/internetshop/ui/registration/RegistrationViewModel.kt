package com.game.internetshop.ui.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.domain.usecase.IsSessionActiveUseCase
import com.game.internetshop.domain.usecase.RegisterUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val isSessionActiveUseCase: IsSessionActiveUseCase
) : ViewModel() {
    private val _uiState = MutableLiveData(RegistrationUiState())
    val uiState: LiveData<RegistrationUiState> = _uiState

    init {
        viewModelScope.launch {
            delay(1000)
            checkSessionWithRetry()
        }
    }

    data class RegistrationUiState(
        val name: String = "",
        val phoneNumber: String = "",
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isRegistrationSuccess: Boolean = false,
        val isCheckingSession: Boolean = true,
        val hasActiveSession: Boolean = false
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
            // Проверяем, не зарегистрирован ли уже пользователь
            if (_uiState.value.hasActiveSession) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Вы уже авторизованы"
                )
            }

            // Проверяем, идет ли уже проверка сессии
            if (_uiState.value.isCheckingSession) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Пожалуйста, подождите, идет проверка..."
                )
            }

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

                else -> _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private fun checkSessionWithRetry() {
        viewModelScope.launch {
            try {
                var result = isSessionActiveUseCase.invoke()
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value?.copy(
                            isCheckingSession = false,
                            hasActiveSession = result.data
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value?.copy(
                            errorMessage = result.message,
                            isCheckingSession = false,
                            hasActiveSession = false
                        )
                    }
                    is AuthResult.Loading -> {
                        _uiState.value = _uiState.value?.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                _uiState.value?.copy(errorMessage = e.message.toString())
            }
            Log.w("supabase_registration_checkSession", "End: ${_uiState.value?.hasActiveSession}")
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