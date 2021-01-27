package com.eddp.busapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import java.io.Console

class Register : Fragment(), WebServiceReceiver {
    private lateinit var _username: EditText
    private lateinit var _email: EditText
    private lateinit var _password: EditText
    private lateinit var _confirmPassword: EditText
    private lateinit var _register: Button
    private lateinit var _login_link: TextView
    private var _activity: AuthActivity? = null
    private lateinit var _navController: NavController
    private var _webServiceLink: WebServiceLink? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity is AuthActivity){
            _activity = (activity as AuthActivity)
        }
        val navHostFragment = _activity?.supportFragmentManager
            ?.findFragmentById(R.id.auth_fragment_container) as NavHostFragment
        this._webServiceLink = WebServiceLink(this)
        _navController = navHostFragment.navController
        initFields(view)
    }

    private fun initFields(view: View) {
        _username = view.findViewById(R.id.register_username)
        _password = view.findViewById(R.id.register_password)
        _confirmPassword = view.findViewById(R.id.register_confirm_password)
        _email = view.findViewById(R.id.register_email)
        _login_link = view.findViewById(R.id.login_link)
        _register = view.findViewById(R.id.register_button)
        _register.setOnClickListener { checkData() }
        _login_link.setOnClickListener { _navController.navigate(R.id.action_register_to_login) }
    }

    private fun checkData() {
        var validatedCheck = true
        if (_activity?.isEmpty(_username) == true) {
            val t =
                Toast.makeText(_activity, "Username is required.", Toast.LENGTH_SHORT)
            t.show()
            validatedCheck = false
        }
        if (_username.text.length > 32) {
            val t =
                Toast.makeText(_activity, "Username must not exceed 32 characters.", Toast.LENGTH_LONG)
            t.show()
            validatedCheck = false
        }
        if (_activity?.isEmpty(_password) == true) {
            _password.error = "Password is required."
            validatedCheck = false
        }
        if (_activity?.isEmpty(_confirmPassword) == true) {
            _confirmPassword.error = "Confirm password is required."
            validatedCheck = false
        }
        if(_password.text.toString() != _confirmPassword.text.toString()) {
            _confirmPassword.error = "Password and password confirmation doesn't match."
            validatedCheck = false
        }
        if (_activity?.checkEmail(_email) == false) {
            _email.error = "Email is required."
            validatedCheck = false
        }

        if(validatedCheck){
            this._webServiceLink?.addUser(
                _username.text.toString(),
                _email.text.toString(),
                _password.text.toString()
            )
        }
    }

    override fun addSuccessful(success: Boolean) {
        super.addSuccessful(success)
        if(success){
            _navController.navigate(R.id.action_register_to_login)
        }else{
            val t =
                Toast.makeText(_activity, "Error when creating new account.", Toast.LENGTH_LONG)
            t.show()
        }
    }
}