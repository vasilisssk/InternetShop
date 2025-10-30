package com.game.internetshop

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.game.internetshop.databinding.ActivitySigningInBinding
import com.google.android.material.textfield.TextInputLayout
import utils.Utils

class SigningInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigningInBinding
    private val INTENT_USER_EMAIL = "UserEmail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigningInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.main.setOnTouchListener { view, event ->
            currentFocus?.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
        }

        binding.tILEmail.errorIconDrawable = null
        binding.tILPassword.errorIconDrawable = null
        binding.tILEmail.editText?.setText(intent.getStringExtra(INTENT_USER_EMAIL) ?: "")

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            val userEmail = binding.tILEmail.editText?.text.toString()
            if (Utils.isEmailValid(userEmail) && !Utils.isEmailInDB(userEmail)) intent.putExtra(INTENT_USER_EMAIL, userEmail)
            startActivity(intent)
        }
        binding.buttonSignIn.setOnClickListener {
            if (allInputsAreValid()) {
                if (Utils.isPasswordHashInDB(binding.tILPassword.editText?.text.toString())) {
                    // вход в аккаунт
                } else {
                    binding.tILEmail.error = getString(R.string.wrong_email_or_password)
                    binding.tILPassword.error = getString(R.string.wrong_email_or_password)
                }
            }
        }
    }

    private fun allInputsAreValid(): Boolean {
        var noErrors = true
        // сначала проверяется корректность написания эл. почты (ошибка только у email)
        if (!Utils.isEmailValid(binding.tILEmail.editText?.text.toString())) {
            noErrors = false
            binding.tILEmail.error = getString(R.string.wrong_email_format)
            binding.tILPassword.isErrorEnabled = false
        }
        // затем проверяется наличие эл. почты в БД
        else if (!Utils.isEmailInDB(binding.tILEmail.editText?.text.toString())){
            noErrors = false
            binding.tILEmail.error = getString(R.string.email_not_in_DB)
            binding.tILPassword.isErrorEnabled = false
        }
        // в конце проверяется совпадение hash'а пароля в БД по почте
        else if (!Utils.isEmailInDB(binding.tILEmail.editText?.text.toString())) {
            noErrors = false
            binding.tILEmail.error = getString(R.string.wrong_email_or_password)
            binding.tILPassword.error = getString(R.string.wrong_email_or_password)
        }
        // если все верно, то ошибки предыдущих нажатий убираются
        else {
            binding.tILEmail.isErrorEnabled = false
            binding.tILPassword.isErrorEnabled = false
        }
        return noErrors
    }
}