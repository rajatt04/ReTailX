package com.rajatt7z.retailx.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rajatt7z.retailx.utils.Resource
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        email: String,
        password: String,
        userMap: HashMap<String, Any>,
        userId: String? = null
    ): Resource<String> {
        return try {
            val uid = userId ?: auth.createUserWithEmailAndPassword(email, password).await().user?.uid
            if (uid != null) {
                userMap["uid"] = uid
                db.collection("users").document(uid).set(userMap).await()
                Resource.Success("Registration Successful")
            } else {
                Resource.Error("Failed to get User ID")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
    
    // Create employee without logging out the current admin
    suspend fun createEmployeeAccount(
        email: String, 
        password: String, 
        userMap: HashMap<String, Any>
    ): Resource<String> {
        return try {
            // 1. Initialize a secondary Firebase App
            val appOptions = com.google.firebase.FirebaseOptions.Builder()
                .setApiKey(auth.app.options.apiKey)
                .setApplicationId(auth.app.options.applicationId)
                .setProjectId(auth.app.options.projectId)
                .build()

            val secondaryApp = try {
                 com.google.firebase.FirebaseApp.getInstance("SecondaryApp")
            } catch (e: Exception) {
                 com.google.firebase.FirebaseApp.initializeApp(auth.app.applicationContext, appOptions, "SecondaryApp")
            }

            // 2. Get Auth instance for this secondary app
            val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

            // 3. Create the user on this secondary instance
            val result = secondaryAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid

            if (uid != null) {
                // 4. Use the PRIMARY Firestore (auth as Admin) to write the data
                // We use 'db' which is the main instance. Admin should have write permission to "users" collection.
                // NOTE: Firestore rules must allow authenticated users to write to 'users' collection.
                userMap["uid"] = uid
                db.collection("users").document(uid).set(userMap).await()
                
                // 5. Sign out the secondary instance to be clean
                secondaryAuth.signOut()
                
                Resource.Success("Employee Added Successfully")
            } else {
                secondaryAuth.signOut()
                Resource.Error("Failed to generate Employee ID")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to add employee: ${e.message}")
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success("Login Successful")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login Failed")
        }
    }
    
    suspend fun getUserDetails(uid: String): Resource<Map<String, Any>> {
        return try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) {
                Resource.Success(document.data ?: emptyMap())
            } else {
                Resource.Error("User details not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch user details")
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun logout() = auth.signOut()
}
