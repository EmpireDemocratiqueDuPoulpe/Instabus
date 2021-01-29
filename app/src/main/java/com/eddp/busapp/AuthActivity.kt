package com.eddp.busapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AuthActivity : AppCompatActivity() {
    private lateinit var _toolbar: Toolbar

    // Getters
    fun isEmailValid(field: EditText?) : Boolean {
        val email: CharSequence = field?.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isEmpty(field: EditText?) : Boolean {
        val value: CharSequence = field?.text.toString()
        return TextUtils.isEmpty(value)
    }

    // Setters
    fun setToolbarTitle(title: String) { this._toolbar.title = title }
    fun setToolbarTitle(resId: Int) { this._toolbar.title = getString(resId) }

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        this._toolbar = findViewById(R.id.toolbar)
        this._toolbar.title = getString(R.string.app_name)
        setSupportActionBar(this._toolbar)
    }

    // Auth
    fun isConnected(username: String, password: String) {
        val intent = Intent(this, MainActivity::class.java)
        //intent.putExtra("username", username)

        startActivity(intent)
        finish()
    }
}
