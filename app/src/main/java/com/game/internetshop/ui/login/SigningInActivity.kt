package com.game.internetshop.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.game.internetshop.R
import com.game.internetshop.databinding.ActivitySigningInBinding
import com.game.internetshop.ui.registration.RegistrationActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils

class SigningInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigningInBinding
    private val viewModel: SigningInViewModel by viewModel()

    private val INTENT_USER_EMAIL = "UserEmail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigningInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tILEmail.errorIconDrawable = null
        binding.tILPassword.errorIconDrawable = null
        binding.tILEmail.editText?.setText(intent.getStringExtra(INTENT_USER_EMAIL) ?: "")

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
//            val userEmail = binding.tILEmail.editText?.text.toString()
//            if (Utils.Companion.isEmailValid(userEmail) && !Utils.Companion.isEmailInDB(userEmail)) intent.putExtra(INTENT_USER_EMAIL, userEmail)
            startActivity(intent)
        }

        binding.main.setOnTouchListener { view, event ->
            currentFocus?.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
        }

        viewModel.uiState.observe(this) { state ->
            updateUI(state)
        }

        binding.tILEmail.editText?.doAfterTextChanged {
            text -> viewModel.onEmailChanged(text.toString())
        }

        binding.tILPassword.editText?.doAfterTextChanged {
                text -> viewModel.onPasswordChanged(text.toString())
        }

        binding.buttonSignIn.setOnClickListener {
            if (simpleValidation()) {
                viewModel.onLoginClicked()
            }
        }
    }

    private fun updateUI(state: SigningInViewModel.SigningInUiState) {
        //binding.progressBar.isVisible = state.isLoading
        binding.buttonSignIn.isEnabled = !state.isLoading
        binding.buttonSignIn.text = if (state.isLoading) "Loading..." else getString(R.string.sign_in)

        if (state.isSigningInSuccess) {
            navigateToMain()
            viewModel.resetSuccessState()  // Сбрасываем флаг успеха
        }
    }

    private fun navigateToMain() {

    }

    private fun simpleValidation(): Boolean {
        var correctnessFlag = true

        if (!Utils.Companion.isEmailValid(binding.tILEmail.editText?.text.toString())) {
            binding.tILEmail.error = getString(R.string.wrong_email_format)
            correctnessFlag = false
        }
        else binding.tILEmail.isErrorEnabled = false

        return correctnessFlag
    }
}