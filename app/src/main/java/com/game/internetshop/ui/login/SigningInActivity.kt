package com.game.internetshop.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.game.internetshop.R
import com.game.internetshop.databinding.ActivitySigningInBinding
import com.game.internetshop.ui.main.MainActivity
import com.game.internetshop.ui.registration.RegistrationActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils

class SigningInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigningInBinding
    private val viewModel: SigningInViewModel by viewModel()
    private val INTENT_USER_EMAIL = "UserEmail"
    private lateinit var mapOfSigningInErrors: MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySigningInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tILEmail.errorIconDrawable = null
        binding.tILPassword.errorIconDrawable = null
        binding.tILEmail.editText?.setText(intent.getStringExtra(INTENT_USER_EMAIL) ?: "")

        mapOfSigningInErrors = mutableMapOf<String, String>().apply {
            put("Data", getString(R.string.wrong_data))
            put("General", getString(R.string.signing_in_general))
        }

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
        binding.buttonRegister.isEnabled = !state.isLoading
        binding.buttonSignIn.text = if (state.isLoading) "Loading..." else getString(R.string.sign_in)

        if (state.isSigningInSuccess) {
            viewModel.resetSuccessState()
            navigateToMain()
        }
        else {
            state.errorMessage?.let { error ->
                when {
                    error.contains("Data") -> {
                        Utils.showErrorSnackbar(binding.root, mapOfSigningInErrors.get("Data")!!,
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("General") -> {
                        Utils.showErrorSnackbar(binding.root, mapOfSigningInErrors.get("General")!!,
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
        if (!Utils.Companion.isEmailValid(binding.tILEmail.editText?.text.toString())) {
            binding.tILEmail.error = getString(R.string.wrong_email_format)
            return false
        }
        else binding.tILEmail.isErrorEnabled = false

        return true
    }
}