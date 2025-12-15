package com.rajatt7z.retailx.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.rajatt7z.retailx.R
import com.rajatt7z.retailx.databinding.ActivityAdminRegBinding
import com.rajatt7z.retailx.utils.Resource
import com.rajatt7z.retailx.viewmodel.AuthViewModel

class AdminReg : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRegBinding
    private val viewModel: AuthViewModel by viewModels()

    private val mapLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val address = result.data?.getStringExtra("address")
            binding.etAddress.setText(address)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminRegBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Clear errors when user starts typing
        binding.etBusinessName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilBusinessName.error = null
        }

        binding.etOwnerName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilOwnerName.error = null
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
        }

        binding.etAddress.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilAddress.error = null
        }
        
        // Location Picker Trigger
        binding.etAddress.isFocusable = false
        binding.etAddress.isClickable = true
        binding.etAddress.setOnClickListener {
            val intent = Intent(this, com.rajatt7z.retailx.utils.MapsActivity::class.java)
            mapLauncher.launch(intent)
        }
        
        // Also trigger on TextInputLayout start icon click (if possible via setEndIconOnClickListener but this is custom drawableStart)
        // Since drawableStart is on EditText, click on EditText covers it mostly.
        binding.tilAddress.setStartIconOnClickListener {
             val intent = Intent(this, com.rajatt7z.retailx.utils.MapsActivity::class.java)
            mapLauncher.launch(intent)
        }

        binding.btnRegister.setOnClickListener {
            registerAdmin()
        }

        observeViewModel()
    }

    private fun registerAdmin() {
        val businessName = binding.etBusinessName.text.toString().trim()
        val ownerName = binding.etOwnerName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (!validateInput(businessName, ownerName, email, password, address)) {
            return
        }

        val userMap: HashMap<String, Any> = hashMapOf(
            "businessName" to businessName,
            "ownerName" to ownerName,
            "email" to email,
            "address" to address,
            "userType" to "admin",
            "createdAt" to System.currentTimeMillis()
        )

        viewModel.registerUser(email, password, userMap)
    }

    private fun validateInput(
        businessName: String,
        ownerName: String,
        email: String,
        password: String,
        address: String
    ): Boolean {
        // Clear previous errors
        binding.tilBusinessName.error = null
        binding.tilOwnerName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilAddress.error = null

        var isValid = true

        // Validate business name
        when {
            businessName.isEmpty() -> {
                binding.tilBusinessName.error = "Business name is required"
                binding.etBusinessName.requestFocus()
                isValid = false
            }
            businessName.length < 3 -> {
                binding.tilBusinessName.error = "Business name must be at least 3 characters"
                binding.etBusinessName.requestFocus()
                isValid = false
            }
        }

        // Validate owner name
        when {
            ownerName.isEmpty() -> {
                binding.tilOwnerName.error = "Owner name is required"
                if (isValid) binding.etOwnerName.requestFocus()
                isValid = false
            }
            ownerName.length < 3 -> {
                binding.tilOwnerName.error = "Owner name must be at least 3 characters"
                if (isValid) binding.etOwnerName.requestFocus()
                isValid = false
            }
        }

        // Validate email
        when {
            email.isEmpty() -> {
                binding.tilEmail.error = "Email is required"
                if (isValid) binding.etEmail.requestFocus()
                isValid = false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Please enter a valid email address"
                if (isValid) binding.etEmail.requestFocus()
                isValid = false
            }
        }

        // Validate password
        when {
            password.isEmpty() -> {
                binding.tilPassword.error = "Password is required"
                if (isValid) binding.etPassword.requestFocus()
                isValid = false
            }
            password.length < 6 -> {
                binding.tilPassword.error = "Password must be at least 6 characters"
                if (isValid) binding.etPassword.requestFocus()
                isValid = false
            }
            !password.matches(Regex(".*[A-Za-z].*")) -> {
                binding.tilPassword.error = "Password must contain at least one letter"
                if (isValid) binding.etPassword.requestFocus()
                isValid = false
            }
        }

        // Validate address
        when {
            address.isEmpty() -> {
                binding.tilAddress.error = "Business address is required"
                if (isValid) binding.etAddress.requestFocus()
                isValid = false
            }
            address.length < 10 -> {
                binding.tilAddress.error = "Please enter a complete address"
                if (isValid) binding.etAddress.requestFocus()
                isValid = false
            }
        }

        return isValid
    }

    private fun observeViewModel() {
        viewModel.authStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                    // Clear any previous errors
                    binding.tilBusinessName.error = null
                    binding.tilOwnerName.error = null
                    binding.tilEmail.error = null
                    binding.tilPassword.error = null
                    binding.tilAddress.error = null
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Snackbar.make(binding.root, resource.data ?: "Registration successful", Snackbar.LENGTH_SHORT).show()
                    viewModel.logout() // Sign out so they can login
                    startActivity(Intent(this, AdminLogin::class.java))
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true

                    // Show error in appropriate field or as Snackbar for general errors
                    val errorMessage = resource.message ?: "Registration failed"
                    when {
                        errorMessage.contains("business name", ignoreCase = true) -> {
                            binding.tilBusinessName.error = errorMessage
                            binding.etBusinessName.requestFocus()
                        }
                        errorMessage.contains("owner", ignoreCase = true) -> {
                            binding.tilOwnerName.error = errorMessage
                            binding.etOwnerName.requestFocus()
                        }
                        errorMessage.contains("email", ignoreCase = true) -> {
                            binding.tilEmail.error = errorMessage
                            binding.etEmail.requestFocus()
                        }
                        errorMessage.contains("password", ignoreCase = true) -> {
                            binding.tilPassword.error = errorMessage
                            binding.etPassword.requestFocus()
                        }
                        errorMessage.contains("address", ignoreCase = true) -> {
                            binding.tilAddress.error = errorMessage
                            binding.etAddress.requestFocus()
                        }
                        else -> {
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}