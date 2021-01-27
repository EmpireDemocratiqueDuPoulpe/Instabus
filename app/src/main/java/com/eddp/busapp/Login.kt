package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver

class Login: Fragment(), WebServiceReceiver {
    private lateinit var _username: EditText
    private lateinit var _password: EditText
    private lateinit var _login: Button
    private lateinit var _register_link: TextView
    private var _activity: AuthActivity? = null
    private lateinit var _navController: NavController
    private var _webServiceLink: WebServiceLink? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity is AuthActivity){
            _activity = (activity as AuthActivity)
        }
        _navController = Navigation.findNavController(view)
        this._webServiceLink = WebServiceLink(this)
        initFields(view)
    }

    private fun initFields(view: View) {
        _username = view.findViewById(R.id.login_username)
        _password = view.findViewById(R.id.login_password)
        _register_link = view.findViewById(R.id.register_link)
        _login = view.findViewById(R.id.login_button)
        _login.setOnClickListener { checkData() }
        _register_link.setOnClickListener { _navController.navigate(R.id.action_login_to_register) }
    }

    private fun checkData() {
        var validatedCheck = true
        if (_activity?.isEmpty(_username) == true) {
            val t =
                Toast.makeText(_activity, "Username is required.", Toast.LENGTH_SHORT)
            t.show()
            validatedCheck = false
        }
        if (_activity?.isEmpty(_password) == true) {
            _password.error = "Password is required."
            validatedCheck = false
        }

        if(validatedCheck){
            this._webServiceLink?.loginUser(
                _username.text.toString(),
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