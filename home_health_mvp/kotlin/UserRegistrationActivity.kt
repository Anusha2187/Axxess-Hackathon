package com.example.homehealth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.homehealth.R
import com.example.homehealth.data.HealthRepository
import com.example.homehealth.model.User
import kotlinx.coroutines.launch

class UserRegistrationActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var zipEditText: EditText
    private lateinit var registerButton: Button

    private val repo = HealthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        zipEditText = findViewById(R.id.zipEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener { onRegisterClicked() }
    }

    private fun onRegisterClicked() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val zip = zipEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || zip.isEmpty()) {
            toast("Please fill in all fields")
            return
        }
        if (password.length < 6) {
            toast("Password must be at least 6 characters")
            return
        }
        if (!zip.matches(Regex("\\d{5}"))) {
            toast("Zip code must be 5 digits")
            return
        }

        registerButton.isEnabled = false
        lifecycleScope.launch {
            try {
                repo.registerUser(email, password, User(name = name, email = email, zipCode = zip))
                toast("Account created")
                // Send the user straight to search results for their own zip code
                startActivity(
                    Intent(this@UserRegistrationActivity, NurseSearchActivity::class.java)
                        .putExtra(NurseSearchActivity.EXTRA_ZIP, zip)
                )
                finish()
            } catch (e: Exception) {
                toast("Registration failed: ${e.message}")
                registerButton.isEnabled = true
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
