package sfllhkhan95.doodle.auth.views

import android.app.Dialog
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import sfllhkhan95.doodle.R

class LoginView(view: Dialog) {
    private val emailField: TextInputEditText? = view.findViewById(R.id.emailField)
    private val emailWrapper: TextInputLayout? = view.findViewById(R.id.emailWrapper)

    private val passwordField: TextInputEditText? = view.findViewById(R.id.passwordField)
    private val passwordWrapper: TextInputLayout? = view.findViewById(R.id.passwordWrapper)

    val email: String
        get() {
            return emailField?.let {
                it.text?.toString()?.trim() ?: ""
            } ?: ""
        }

    val password: String
        get() {
            return passwordField?.let {
                it.text?.toString()?.trim() ?: ""
            } ?: ""
        }

    fun hideErrors() {
        emailWrapper?.isErrorEnabled = false
        passwordWrapper?.isErrorEnabled = false
    }

    var emailError: String = ""
        set(error) {
            field = error
            emailWrapper?.let {
                it.isErrorEnabled = true
                it.error = error
            }
        }

    var passwordError: String = ""
        set(error) {
            field = error
            passwordWrapper?.let {
                it.isErrorEnabled = true
                it.error = error
            }
        }
}
