package utils

import android.util.Patterns
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.game.internetshop.R
import com.google.android.material.snackbar.Snackbar

class Utils {

    companion object {
        fun isPhoneNumberValid(phoneNumber: String): Boolean {
            return phoneNumber.length == 10
        }

        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun prepareName(name: String): String {
            val name = name.lowercase()
            val firstChar = name[0].uppercaseChar()
            val substring = name.substring(1)
            return firstChar+substring
        }

        fun showErrorSnackbar(view: View, text: CharSequence, duration: Int) {
            val snackbar = Snackbar.make(view, text, duration)
            snackbar.setTextColor(view.context.getColor(R.color.dark_red))
            snackbar.setBackgroundTint(view.context.getColor(R.color.red))
            val view = snackbar.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            val marginDp = 6
            val marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginDp.toFloat(),
                view.context.resources.displayMetrics
            ).toInt()
            params.setMargins(marginPx,marginPx*4,marginPx,0)
            view.layoutParams = params
            snackbar.show()
        }

        fun showSuccessSnackbar(view: View, text: CharSequence, duration: Int) {
            val snackbar = Snackbar.make(view, text, duration)
            snackbar.setTextColor(view.context.getColor(R.color.dark_green))
            snackbar.setBackgroundTint(view.context.getColor(R.color.green))
            val view = snackbar.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            val marginDp = 6
            val marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginDp.toFloat(),
                view.context.resources.displayMetrics
            ).toInt()
            params.setMargins(marginPx,marginPx*4,marginPx,0)
            view.layoutParams = params
            snackbar.show()
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

