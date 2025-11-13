package utils

import android.util.Patterns

class Utils {
    companion object {
        fun isPhoneNumberValid(phoneNumber: String): Boolean {
            return phoneNumber.length == 10
        }

        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    class ValidatePassword(val password: String) {
        enum class ErrorMessage {
            NONE,
            TO_SHORT,
            NO_DIGIT,
            NO_UPPER_CASE,
            NO_LOWER_CASE,
            NO_SPECIAL_CHARACTER
        }

        val arrayOfSpecialCharacters = arrayOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", ";", ":", ",", ".", "/", "?", "\\", "|", "`", "~", "[", "]", "{", "}")

        fun validate(): ErrorMessage {
            val passwordArray = password.toCharArray()
            if (password.length < 6) {
                return ErrorMessage.TO_SHORT
            }
            if (!passwordArray.any {it.isDigit()}) {
                return ErrorMessage.NO_DIGIT
            }
            if (!passwordArray.any {it.isUpperCase()}) {
                return ErrorMessage.NO_UPPER_CASE
            }
            if (!passwordArray.any {it.isLowerCase()}) {
                return ErrorMessage.NO_LOWER_CASE
            }
            if (!passwordArray.any {it.toString() in arrayOfSpecialCharacters}) {
                return ErrorMessage.NO_SPECIAL_CHARACTER
            }
            return ErrorMessage.NONE
        }
    }
}

