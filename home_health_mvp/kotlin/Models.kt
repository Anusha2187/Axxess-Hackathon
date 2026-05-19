package com.example.homehealth.model

/**
 * Stored at /users/{uid} in Firestore.
 * uid comes from FirebaseAuth — we don't store it in the document body.
 */
data class User(
    val name: String = "",
    val email: String = "",
    val zipCode: String = ""
) {
    // No-arg constructor required by Firestore deserialization is provided
    // automatically by the default values on each field.
}

/**
 * Stored at /nurses/{uid} in Firestore.
 * zipCode is the field we query on for matching.
 */
data class Nurse(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val zipCode: String = "",
    val availability: String = "",
    val certification: String = "",
    val licenseNumber: String = ""
)
