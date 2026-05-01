package com.game.internetshop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.common.Result
import com.game.internetshop.domain.usecase.auth.GetCurrentUserUseCase
import com.game.internetshop.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {
    private val _uiState = MutableLiveData(SettingsUiState())
    val uiState: LiveData<SettingsUiState> = _uiState

    init {
        getUserInfo()
    }

    data class SettingsUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isSigningOutSuccess: Boolean = false,
        val userName: String = "",
        val userPhone: String = "",
        val userEmail: String = ""
    )

    fun getUserInfo() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase.invoke()) {
                is Result.Success -> _uiState.value = _uiState.value?.copy(
                    userName = result.data.userName,
                    userPhone = result.data.phoneNumber,
                    userEmail = result.data.email
                )
                is Result.Error -> _uiState.value = _uiState.value?.copy(errorMessage = result.message)
                is Result.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    fun onLogOutClicked() {
        viewModelScope.launch {
            val result = signOutUseCase.invoke()
            _uiState.value = when (result) {
                is Result.Success ->  {
                    _uiState.value?.copy(
                        isLoading = false,
                        isSigningOutSuccess = true

                    )
                }
                is Result.Error -> {
                    _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value?.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }

    fun clearSigningOutResult() {
        _uiState.value = _uiState.value?.copy(isSigningOutSuccess = false)
    }

}