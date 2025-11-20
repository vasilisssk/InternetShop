package com.game.internetshop.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.game.internetshop.R
import com.game.internetshop.ui.login.SigningInActivity
import com.game.internetshop.databinding.ActivityRegistrationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils
import kotlin.getValue

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val viewModel: RegistrationViewModel by viewModel()

    private lateinit var listOfInputs: List<TextInputLayout>

    private val INTENT_USER_EMAIL = "UserEmail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tILName.errorIconDrawable = null
        binding.tILPhoneNumber.errorIconDrawable = null
        binding.tILEmail.errorIconDrawable = null
        binding.tILPassword.errorIconDrawable = null
        binding.tILEmail.editText?.setText(intent.getStringExtra(INTENT_USER_EMAIL) ?: "")

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

        viewModel.uiState.observe(this) { state ->
            updateUI(state)
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
        //binding.progressBar.isVisible = state.isLoading
        binding.buttonSignIn.isEnabled = !state.isLoading
        binding.buttonSignIn.text = if (state.isLoading) "Loading..." else getString(R.string.register)

        if (state.isRegistrationSuccess) {
            navigateToMain()
            viewModel.resetSuccessState()  // Сбрасываем флаг успеха
        }
    }

    private fun navigateToMain() {

    }

    private fun simpleValidation(): Boolean {
        var correctnessFlag = true
        if (binding.tILName.editText?.text?.isEmpty() == true) {
            binding.tILName.error = getString(R.string.empty_name)
            correctnessFlag = false
        }
        else binding.tILName.isErrorEnabled = false

        if (!Utils.isPhoneNumberValid(binding.tILPhoneNumber.editText?.text.toString())) {
            binding.tILPhoneNumber.error = getString(R.string.wrong_phone_number_format)
            correctnessFlag = false
        }
        else binding.tILPhoneNumber.isErrorEnabled = false

        if (!Utils.Companion.isEmailValid(binding.tILEmail.editText?.text.toString())) {
            binding.tILEmail.error = getString(R.string.wrong_email_format)
            correctnessFlag = false
        }
        else binding.tILEmail.isErrorEnabled = false

        when (Utils.ValidatePassword(binding.tILPassword.editText?.text.toString()).validate()) {
            Utils.ValidatePassword.ErrorMessage.TO_SHORT -> {
                binding.tILPassword.error = getString(R.string.password_is_too_short)
                correctnessFlag = false
            }
            Utils.ValidatePassword.ErrorMessage.NO_DIGIT -> {
                binding.tILPassword.error = getString(R.string.password_no_digit)
                correctnessFlag = false
            }
            Utils.ValidatePassword.ErrorMessage.NO_UPPER_CASE -> {
                binding.tILPassword.error = getString(R.string.password_no_upper_case)
                correctnessFlag = false
            }
            Utils.ValidatePassword.ErrorMessage.NO_LOWER_CASE -> {
                binding.tILPassword.error = getString(R.string.password_no_lower_case)
                correctnessFlag = false
            }
            Utils.ValidatePassword.ErrorMessage.NO_SPECIAL_CHARACTER -> {
                binding.tILPassword.error = getString(R.string.password_no_special_character)
                correctnessFlag = false
            }
            Utils.ValidatePassword.ErrorMessage.NONE -> {
                binding.tILPassword.isErrorEnabled = false
                correctnessFlag = true
            }
        }

        return correctnessFlag
    }
}