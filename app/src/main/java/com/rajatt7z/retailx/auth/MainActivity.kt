package com.rajatt7z.retailx.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.rajatt7z.retailx.utils.Resource
import com.rajatt7z.retailx.viewmodel.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.rajatt7z.retailx.AdminDashboardActivity
import com.rajatt7z.retailx.EmployeeDashboardActivity
import com.rajatt7z.retailx.R
import com.rajatt7z.retailx.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButtons()
        observeViewModel()
        checkUserSession()
    }

    private fun setupButtons() {
        binding.appInfo.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("App Information")
                .setMessage(
                    "ReTailX\n" +
                            "Store Management System\n\n" +
                            "Version: 1.0.0\n" +
                            "Developed by: Rajat Kevat"
                )
                .setIcon(R.drawable.rounded_info_24)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.loginCustomerBtn.setOnClickListener {
            startActivity(Intent(this, EmployeeLogin::class.java))
        }

        binding.loginBusinessBtn.setOnClickListener {
            startActivity(Intent(this, AdminLogin::class.java))
        }
    }

    private fun checkUserSession() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModel.fetchUserDetails(user.uid)
        }
    }

    private fun observeViewModel() {
        viewModel.userDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginCustomerBtn.isEnabled = false
                    binding.loginBusinessBtn.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = resource.data
                    if (data != null) {
                        val userType = data["userType"] as? String
                        if (userType == "admin") {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                            finish()
                        } else if (userType == "employee") {
                            startActivity(Intent(this, EmployeeDashboardActivity::class.java))
                            finish()
                        } else {
                            // Unknown type, maybe show error or just stay
                             binding.loginCustomerBtn.isEnabled = true
                             binding.loginBusinessBtn.isEnabled = true
                             Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginCustomerBtn.isEnabled = true
                    binding.loginBusinessBtn.isEnabled = true
                    // Silent fail or toast? Maybe User was deleted from DB but Auth persists.
                    // Ideally logout.
                     Toast.makeText(this, "Session expired or invalid", Toast.LENGTH_SHORT).show()
                     FirebaseAuth.getInstance().signOut()
                }
            }
        }
    }
}