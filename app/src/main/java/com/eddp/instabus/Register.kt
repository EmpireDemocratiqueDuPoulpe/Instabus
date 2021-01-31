package com.eddp.instabus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.eddp.instabus.data.WebServiceLink
import com.eddp.instabus.interfaces.WebServiceReceiver

class Register : Fragment(), WebServiceReceiver {
    private var _activity: AuthActivity? = null

    private lateinit var _navController: NavController

    private lateinit var _errorText: TextView
    private lateinit var _usernameField: EditText
    private lateinit var _emailField: EditText
    private lateinit var _passwordField: EditText
    private lateinit var _confirmPasswordField: EditText
    private lateinit var _registerBtn: Button
    private lateinit var _registerLoading: ProgressBar
    private lateinit var _loginLink: TextView

    private var _webServiceLink = WebServiceLink(this)

    // Setters
    private fun setLoading(loading: Boolean) {
        if (loading) {
            this._registerBtn.visibility = View.INVISIBLE
            this._registerLoading.visibility = View.VISIBLE
        } else {
            this._registerBtn.visibility = View.VISIBLE
            this._registerLoading.visibility = View.INVISIBLE
        }
    }

    // Views
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get activity
        if(activity is AuthActivity){
            this._activity = (activity as AuthActivity)
        }

        this._activity?.setToolbarTitle(R.string.auth_register_title)

        // Views
        initFields(view)

        // Navigation
        this._navController = Navigation.findNavController(view)
    }

    private fun initFields(view: View) {
        this._errorText = view.findViewById(R.id.register_errors)
        this._usernameField = view.findViewById(R.id.register_username)
        this._emailField = view.findViewById(R.id.register_email)
        this._passwordField = view.findViewById(R.id.register_password)
        this._confirmPasswordField = view.findViewById(R.id.register_confirm_password)

        this._registerBtn = view.findViewById(R.id.register_button)
        this._registerBtn.setOnClickListener { register() }
        this._registerLoading = view.findViewById(R.id.register_loading)

        this._loginLink = view.findViewById(R.id.login_link)
        this._loginLink.setOnClickListener { goToLoginPage() }
    }

    // Register
    private fun register() {
        setLoading(true)
        removeError()

        if (this._activity == null)
            return showError("Fatal error. Update the app and try again.")

        if (checkFields()) {
            this._webServiceLink.addUser(
                this._usernameField.text.toString(),
                this._emailField.text.toString(),
                this._passwordField.text.toString()
            )
        }
    }

    private fun checkFields() : Boolean {
        var completed = true

        // Username
        if (this._activity!!.isEmpty(this._usernameField)) {
            showError(this._activity!!.getString(R.string.auth_err_empty_username), this._usernameField)
            completed = false
        } else if (this._usernameField.text.length > 32) {
            showError(this._activity!!.getString(R.string.auth_err_too_long_username), this._usernameField)
            completed = false
        }

        // Email
        if (!this._activity!!.isEmailValid(this._emailField)) {
            showError(this._activity!!.getString(R.string.auth_err_empty_email), this._emailField)
            completed = false
        }

        // Passwords
        if (this._activity!!.isEmpty(this._passwordField)) {
            showError(this._activity!!.getString(R.string.auth_err_empty_password), this._passwordField)
            completed = false
        }

        if (this._activity!!.isEmpty(this._confirmPasswordField)) {
            showError(this._activity!!.getString(R.string.auth_err_empty_password), this._confirmPasswordField)
            completed = false
        }

        if(this._passwordField.text.toString() != this._confirmPasswordField.text.toString()) {
            showError(
                this._activity!!.getString(R.string.auth_err_password_match),
                this._passwordField,
                this._confirmPasswordField
            )
            completed = false
        }

        return completed
    }

    private fun showError(message: String, vararg fields: EditText) {
        if (this._errorText.text.isEmpty()) {
            this._errorText.text = message
        }

        for (field in fields) {
            field.error = message
        }
    }

    private fun removeError() {
        this._errorText.text = ""
    }

    // Navigation
    private fun goToLoginPage() {
        this._navController.navigate(R.id.action_register_to_login)
    }

    private fun finishRegistering(selector: String, authToken: String, userId: Int, username: String) {
        this._activity?.isConnected(selector, authToken, userId, username)
    }

    override fun onRegister(registered: Boolean, selector: String, authToken: String, userId: Int, username: String, err: String) {
        super.onRegister(registered, selector, authToken, userId, username, err)

        if (registered) {
            finishRegistering(selector, authToken, userId, username)
        } else {
            if (err.isNotEmpty()) {
                showError(err)
            } else {
                showError(this._activity!!.getString(R.string.auth_err_unknown_register))
            }

            setLoading(false)
        }
    }
}