package com.example.homehealth.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.homehealth.R
import com.example.homehealth.data.HealthRepository
import com.example.homehealth.model.Nurse
import kotlinx.coroutines.launch

class NurseRegistrationActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var zipEditText: EditText
    private lateinit var availabilityEditText: EditText
    private lateinit var certificationEditText: EditText
    private lateinit var licenseEditText: EditText
    private lateinit var registerButton: Button

    private val repo = HealthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_registration)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        zipEditText = findViewById(R.id.zipEditText)
        availabilityEditText = findViewById(R.id.availabilityEditText)
        certificationEditText = findViewById(R.id.certificationEditText)
        licenseEditText = findViewById(R.id.licenseEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener { onRegisterClicked() }
    }

    private fun onRegisterClicked() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val phone = phoneEditText.text.toString().trim()
        val zip = zipEditText.text.toString().trim()
        val availability = availabilityEditText.text.toString().trim()
        val certification = certificationEditText.text.toString().trim()
        val license = licenseEditText.text.toString().trim()

        // Required: name, email, password, phone, zip, license
        // Optional: availability, certification (left in for completeness)
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
            phone.isEmpty() || zip.isEmpty() || license.isEmpty()
        ) {
            toast("Please fill in all required fields")
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
                val nurse = Nurse(
                    name = name,
                    email = email,
                    phone = phone,
                    zipCode = zip,
                    availability = availability,
                    certification = certification,
                    licenseNumber = license
                )
                repo.registerNurse(email, password, nurse)
                toast("Nurse profile created")
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
