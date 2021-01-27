package com.eddp.busapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    fun checkEmail(text: EditText?): Boolean {
        val email: CharSequence = text!!.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isEmpty(text: EditText?): Boolean {
        val str: CharSequence = text!!.text.toString()
        return TextUtils.isEmpty(str)
    }
}
