package com.game.internetshop.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.game.internetshop.R
import com.game.internetshop.databinding.ActivityRegistrationBinding
import com.game.internetshop.ui.login.SigningInActivity
import com.game.internetshop.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val viewModel: RegistrationViewModel by viewModel()
    private lateinit var listOfInputs: List<TextInputLayout>
    private val INTENT_USER_EMAIL = "UserEmail"
    private lateinit var mapOfRegistrationErrors: MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.uiState.observe(this) { state ->
            updateUI(state)
        }

        binding.tILName.errorIconDrawable = null
        binding.tILPhoneNumber.errorIconDrawable = null
        binding.tILEmail.errorIconDrawable = null
        binding.tILPassword.errorIconDrawable = null
        binding.tILEmail.editText?.setText(intent.getStringExtra(INTENT_USER_EMAIL) ?: "")

        mapOfRegistrationErrors = mutableMapOf<String, String>().apply {
            put("Email", getString(R.string.user_exists))
            put("Phone", getString(R.string.phone_exists))
            put("General", getString(R.string.registration_general))
        }

        binding.buttonSignIn.setOnClickListener {
            val intent = Intent(this, SigningInActivity::class.java)
            startActivity(intent)
        }

        listOfInputs = listOf(binding.tILName, binding.tILPhoneNumber, binding.tILEmail, binding.tILPassword)

        binding.main.setOnTouchListener { view, event ->
            currentFocus?.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
        }

        binding.tILName.editText?.doAfterTextChanged {
                text -> viewModel.onNameChanged(text.toString())
        }

        binding.tILPhoneNumber.editText?.doAfterTextChanged {
                text -> viewModel.onPhoneNumberChanged(text.toString())
        }

        binding.tILEmail.editText?.doAfterTextChanged {
                text -> viewModel.onEmailChanged(text.toString())
        }

        binding.tILPassword.editText?.doAfterTextChanged {
                text -> viewModel.onPasswordChanged(text.toString())
        }

        binding.buttonRegister.setOnClickListener {
            if (simpleValidation()) {
                viewModel.onRegisterClicked()
            }
        }
    }

    private fun updateUI(state: RegistrationViewModel.RegistrationUiState) {
        Log.w("supabase_registration_updateUI", "state: has active session - "+state.hasActiveSession.toString())
        if (state.hasActiveSession) {
            navigateToMain()
        }
        //binding.progressBar.isVisible = state.isLoading
        binding.buttonSignIn.isEnabled = !state.isLoading
        binding.buttonRegister.isEnabled = !state.isLoading
        binding.buttonRegister.text = if (state.isLoading) "Loading..." else getString(R.string.register)

        if (state.isRegistrationSuccess) {
            viewModel.resetSuccessState()
            navigateToMain()
        } else {
            state.errorMessage?.let { error ->
                when {
                    error.contains("Email") -> {
                        Utils.showErrorSnackbar(binding.root, mapOfRegistrationErrors.get("Email")!!,
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Phone") -> {
                        Utils.showErrorSnackbar(binding.root, mapOfRegistrationErrors.get("Phone")!!,
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("General") -> {
                        Utils.showErrorSnackbar(binding.root, mapOfRegistrationErrors.get("General")!!,
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    else -> {
                        Utils.showErrorSnackbar(binding.root, state.errorMessage,
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                 }

            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun simpleValidation(): Boolean {
        if (binding.tILName.editText?.text?.isEmpty() == true) {
            binding.tILName.error = getString(R.string.empty_name)
            return false
        }
        else binding.tILName.isErrorEnabled = false

        if (!Utils.isPhoneNumberValid(binding.tILPhoneNumber.editText?.text.toString())) {
            binding.tILPhoneNumber.error = getString(R.string.wrong_phone_number_format)
            return false
        }
        else binding.tILPhoneNumber.isErrorEnabled = false

        if (!Utils.Companion.isEmailValid(binding.tILEmail.editText?.text.toString())) {
            binding.tILEmail.error = getString(R.string.wrong_email_format)
            return false
        }
        else binding.tILEmail.isErrorEnabled = false

        when (Utils.ValidatePassword(binding.tILPassword.editText?.text.toString()).validate()) {
            Utils.ValidatePassword.ErrorMessage.TO_SHORT -> {
                binding.tILPassword.error = getString(R.string.password_is_too_short)
                return false
            }
            Utils.ValidatePassword.ErrorMessage.NO_DIGIT -> {
                binding.tILPassword.error = getString(R.string.password_no_digit)
                return false
            }
            Utils.ValidatePassword.ErrorMessage.NO_UPPER_CASE -> {
                binding.tILPassword.error = getString(R.string.password_no_upper_case)
                return false
            }
            Utils.ValidatePassword.ErrorMessage.NO_LOWER_CASE -> {
                binding.tILPassword.error = getString(R.string.password_no_lower_case)
                return false
            }
            Utils.ValidatePassword.ErrorMessage.NO_SPECIAL_CHARACTER -> {
                binding.tILPassword.error = getString(R.string.password_no_special_character)
                return false
            }
            Utils.ValidatePassword.ErrorMessage.NONE -> {
                binding.tILPassword.isErrorEnabled = false
            }
        }

        return true
    }
}