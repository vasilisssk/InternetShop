package utils

import android.adservices.ondevicepersonalization.InferenceInput
import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

class Utils {
    companion object {
        fun isPhoneNumberValid(phoneNumber: String): Boolean {
            return phoneNumber.length == 10
        }

        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isEmailInDB(email: String): Boolean {
            return email.equals("erokhovvasiliy2005@gmail.com")
        }

        fun isPasswordLongEnough(password: String): Boolean {
            return password.length > 4
        }

        fun isPasswordHashInDB(passwordHash: String): Boolean {
            return passwordHash.equals("12345")
        }
    }
}