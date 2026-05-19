package com.example.homehealth.data

import com.example.homehealth.model.Nurse
import com.example.homehealth.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Thin wrapper over Firebase Auth + Firestore.
 *
 * Two top-level collections:
 *   /users/{uid}   — people who search for a nurse
 *   /nurses/{uid}  — nurses who offer services
 *
 * Keying both by Firebase Auth uid means a single account = a single profile,
 * and Firestore security rules can simply say "users may only write their own
 * uid document" (we're not generating those rules here, but the schema supports it).
 */
class HealthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /** Create an auth account and write the User profile in one suspend flow. */
    suspend fun registerUser(email: String, password: String, profile: User) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("Auth succeeded but uid is null")
        db.collection("users").document(uid).set(profile).await()
    }

    /** Create an auth account and write the Nurse profile in one suspend flow. */
    suspend fun registerNurse(email: String, password: String, profile: Nurse) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("Auth succeeded but uid is null")
        db.collection("nurses").document(uid).set(profile).await()
    }

    /**
     * Return every nurse whose registered zipCode matches.
     * Exact-match for the MVP — a future iteration can do radius/nearby lookups
     * with geohashing or a Cloud Function.
     */
    suspend fun findNursesByZip(zipCode: String): List<Nurse> {
        val snapshot = db.collection("nurses")
            .whereEqualTo("zipCode", zipCode)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Nurse::class.java) }
    }
}
