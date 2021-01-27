package com.eddp.busapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {
    // Getters
    fun isEmailValid(field: EditText?) : Boolean {
        val email: CharSequence = field?.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isEmpty(field: EditText?) : Boolean {
        val value: CharSequence = field?.text.toString()
        return TextUtils.isEmpty(value)
    }

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}
