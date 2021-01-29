package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver

class Login: Fragment(), WebServiceReceiver {
    private var _activity: AuthActivity? = null

    private lateinit var _navController: NavController

    private lateinit var _errorText: TextView
    private lateinit var _usernameField: EditText
    private lateinit var _passwordField: EditText
    private lateinit var _loginBtn: Button
    private lateinit var _registerLink: TextView

    private var _webServiceLink = WebServiceLink(this)

    // Views
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get activity
        if(activity is AuthActivity){
            this._activity = (activity as AuthActivity)
        }

        this._activity?.setToolbarTitle(R.string.auth_login_title)

        // Views
        initFields(view)

        // Navigation
        this._navController = Navigation.findNavController(view)
    }

    private fun initFields(view: View) {
        this._errorText = view.findViewById(R.id.login_errors)
        this._usernameField = view.findViewById(R.id.login_username)
        this._passwordField = view.findViewById(R.id.login_password)

        this._loginBtn = view.findViewById(R.id.login_button)
        this._loginBtn.setOnClickListener { logIn() }

        this._registerLink = view.findViewById(R.id.register_link)
        this._registerLink.setOnClickListener { goToRegisterPage() }
    }

    // Log in
    private fun logIn() {
        removeError()

        if (this._activity == null)
            return showError("Fatal error. Update the app and try again.")

        if (checkFields()) {
            this._webServiceLink.loginUser(
                this._usernameField.text.toString(),
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
        }

        // Password
        if (this._activity!!.isEmpty(this._passwordField)) {
            showError(this._activity!!.getString(R.string.auth_err_empty_password), this._passwordField)
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
    private fun goToRegisterPage() {
        this._navController.navigate(R.id.action_login_to_register)
    }

    private fun goToMainActivity() {}

    // Web service
    override fun addSuccessful(success: Boolean, message: String) {
        super.addSuccessful(success, message)

        if(success) {
            goToMainActivity()
        } else {
            showError(this._activity!!.getString(R.string.auth_err_unknown_login))
        }
    }
}