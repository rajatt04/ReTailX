package com.rajatt7z.retailx

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rajatt7z.retailx.databinding.ActivityAdminDashboardBinding
import com.rajatt7z.retailx.utils.Resource
import com.rajatt7z.retailx.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rajatt7z.retailx.auth.MainActivity

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupListeners()
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.fetchUserDetails(currentUser.uid)
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            navigateToMain()
        }

        binding.btnAddEmployee.setOnClickListener {
            showAddEmployeeDialog()
        }
    }

    private fun showAddEmployeeDialog() {
        // Use MaterialAlertDialogBuilder for M3 styling
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle("Add New Employee")
            .setIcon(R.drawable.baseline_person_24)
        
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_employee, null)
        builder.setView(dialogView)

        val etName = dialogView.findViewById<EditText>(R.id.etEmployeeName)
        val etPhone = dialogView.findViewById<EditText>(R.id.etEmployeePhone)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmployeeEmail)
        val etPassword = dialogView.findViewById<EditText>(R.id.etEmployeePassword)

        builder.setPositiveButton("Add Employee", null) // Set null here to override onClick later
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()

        // Override onClick to prevent closing on invalid input
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateEmployeeInput(name, phone, email, password)) {
                 val userMap: HashMap<String, Any> = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "email" to email,
                    "userType" to "employee",
                    "createdAt" to System.currentTimeMillis()
                )
                
                // Use the new method that keeps admin logged in
                viewModel.createEmployee(email, password, userMap)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please check your inputs", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateEmployeeInput(name: String, phone: String, email: String, password: String): Boolean {
        if (name.length < 3) return false
        if (phone.length < 10) return false
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        if (password.length < 6) return false
        return true
    }

    private fun setupObservers() {
        // User Details Observer
        viewModel.userDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvDetails.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvDetails.visibility = View.VISIBLE
                    val data = resource.data
                    if (data != null) {
                        displayDetails(data)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Auth Status Observer (for Registering Employee)
        viewModel.authStatus.observe(this) { resource ->
             when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val message = resource.data
                    if (message == "Employee Added Successfully") {
                         Toast.makeText(this, "Employee Added Successfully!", Toast.LENGTH_SHORT).show()
                         // Stay on dashboard, do NOT redirect.
                    } else if (message == "Login Successful") {
                        // Checking this might be redundant if we are already here, but safe to ignore
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    val msg = resource.message ?: "Error"
                    if (msg.contains("Login", true)) {
                        // ignore general login errors if we are already logged in
                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun displayDetails(data: Map<String, Any>) {
        val businessName = data["businessName"] as? String ?: "N/A"
        val ownerName = data["ownerName"] as? String ?: "N/A"
        val email = data["email"] as? String ?: "N/A"
        val address = data["address"] as? String ?: "N/A"

        val details = """
            Business (Admin): $businessName
            Owner: $ownerName
            Email: $email
            Address: $address
        """.trimIndent()

        binding.tvDetails.text = details
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
