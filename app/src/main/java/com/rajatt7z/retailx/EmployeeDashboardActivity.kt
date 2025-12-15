package com.rajatt7z.retailx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.rajatt7z.retailx.auth.MainActivity

class EmployeeDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_employee_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvWelcome = findViewById<android.widget.TextView>(R.id.textView)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val email = document.getString("email")
                        val userType = document.getString("userType")
                        val name = document.getString("name")
                        val phone = document.getString("phone")
                        
                        tvWelcome.text = "Welcome Employee, $name!\n\nDetails:\nEmail: $email\nPhone: $phone\nRole: $userType"
                    }
                }
                .addOnFailureListener {
                    tvWelcome.text = "Welcome Employee!\n(Failed to fetch details)"
                }
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
