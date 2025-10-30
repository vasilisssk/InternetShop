package com.game.internetshop

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.game.internetshop.databinding.ActivityRegistrationBinding
import com.google.android.material.textfield.TextInputLayout
import utils.Utils

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val INTENT_USER_EMAIL = "UserEmail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.main.setOnTouchListener { view, event ->
            currentFocus?.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
        }

        binding.tILName.errorIconDrawable = null
        binding.tILPhoneNumber.errorIconDrawable = null
        binding.tILEmail.errorIconDrawable = null
        binding.tILPassword.errorIconDrawable = null
        binding.tILEmail.editText?.setText(intent.getStringExtra(INTENT_USER_EMAIL) ?: "")

        binding.buttonRegister.setOnClickListener {
            if (allInputsAreValid()) {
                // отправка данных в бд
            }
        }
        binding.buttonSignIn.setOnClickListener {
            val intent = Intent(this, SigningInActivity::class.java)
            val userEmail = binding.tILEmail.editText?.text.toString()
            if (Utils.isEmailValid(userEmail) && Utils.isEmailInDB(userEmail)) intent.putExtra(INTENT_USER_EMAIL, userEmail)
            startActivity(intent)
        }
    }

    private fun allInputsAreValid(): Boolean {
        var noErrors = true
        if (Utils.isEmailInDB(binding.tILEmail.editText?.text.toString())) {
            noErrors = false
            binding.tILEmail.error = getString(R.string.email_in_DB)
        } 
        
        if (!noErrors) return noErrors
        else binding.tILEmail.isErrorEnabled = false
        
        if (binding.tILName.editText?.text?.length == 0) {
            noErrors = false
            binding.tILName.error = getString(R.string.empty_name)
        }
        else binding.tILName.isErrorEnabled = false
        

        if (!Utils.isPhoneNumberValid(binding.tILPhoneNumber.editText?.text.toString())) {
            noErrors = false
            binding.tILPhoneNumber.error = getString(R.string.wrong_phone_number_format)
        }
        else binding.tILPhoneNumber.isErrorEnabled = false

        if (!Utils.isEmailValid(binding.tILEmail.editText?.text.toString())) {
            noErrors = false
            binding.tILEmail.error = getString(R.string.wrong_email_format)
        }
        else binding.tILEmail.isErrorEnabled = false

        if (!Utils.isPasswordLongEnough(binding.tILPassword.editText?.text.toString())) {
            noErrors = false
            binding.tILPassword.error = getString(R.string.wrong_password_format)
        }
        else binding.tILPassword.isErrorEnabled = false
        
        return noErrors
    }
}