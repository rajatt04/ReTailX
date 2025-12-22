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
import android.os.Handler
import android.os.Looper
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.HorizontalScrollView
import androidx.core.animation.doOnEnd

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    // Auto-scroll variables
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val totalPages = 4
    private val scrollDelay = 3000L // 3 seconds per card
    private var isAutoScrolling = true

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
        setupFeatureCardsAnimation()
    }

    private fun setupFeatureCardsAnimation() {
        // Initial card entrance animations
        animateCardsEntrance()

        // Setup auto-scroll
        binding.featureCardsScrollView.post {
            startAutoScroll()
        }

        // Stop auto-scroll when user manually scrolls
        binding.featureCardsScrollView.setOnTouchListener { _, _ ->
            isAutoScrolling = false
            autoScrollHandler.removeCallbacksAndMessages(null)
            // Resume after 5 seconds of inactivity
            autoScrollHandler.postDelayed({
                isAutoScrolling = true
                startAutoScroll()
            }, 5000)
            false
        }

        // Setup card click listeners
        setupCardClickListeners()
    }

    private fun animateCardsEntrance() {
        val cards = listOf(
            binding.cardAI,
            binding.cardInventory,
            binding.cardSync,
            binding.cardDesign
        )

        val icons = listOf(
            binding.iconAI,
            binding.iconInventory,
            binding.iconSync,
            binding.iconDesign
        )

        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.scaleX = 0.8f
            card.scaleY = 0.8f
            card.translationY = 100f

            card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay((index * 150).toLong())
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Animate icons with floating effect
        icons.forEachIndexed { index, icon ->
            icon.postDelayed({
                startIconFloatAnimation(icon)
            }, (index * 150 + 600).toLong())
        }
    }

    private fun startIconFloatAnimation(icon: View) {
        val translationY = ObjectAnimator.ofFloat(icon, "translationY", 0f, -12f, 0f)
        val rotation = ObjectAnimator.ofFloat(icon, "rotation", 0f, 5f, -5f, 0f)

        AnimatorSet().apply {
            playTogether(translationY, rotation)
            duration = 3000
            interpolator = AccelerateDecelerateInterpolator()
            doOnEnd {
                if (icon.isAttachedToWindow) {
                    start()
                }
            }
            start()
        }
    }

    private fun startAutoScroll() {
        if (!isAutoScrolling) return

        autoScrollHandler.postDelayed({
            if (!isAutoScrolling) return@postDelayed

            currentPage = (currentPage + 1) % totalPages

            val scrollView = binding.featureCardsScrollView
            val cardWidth = 280 + 16 // card width + margin
            val targetScroll = (currentPage * cardWidth * resources.displayMetrics.density).toInt()

            // Smooth scroll animation
            ObjectAnimator.ofInt(scrollView, "scrollX", scrollView.scrollX, targetScroll).apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }

            // Update page indicators
            updatePageIndicators(currentPage)

            // Continue auto-scrolling
            startAutoScroll()
        }, scrollDelay)
    }

    private fun updatePageIndicators(page: Int) {
        val dots = listOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4
        )

        dots.forEachIndexed { index, dot ->
            dot.animate()
                .scaleX(if (index == page) 1.3f else 1f)
                .scaleY(if (index == page) 1.3f else 1f)
                .alpha(if (index == page) 1f else 0.3f)
                .setDuration(300)
                .start()
        }
    }

    private fun setupCardClickListeners() {
        val cards = listOf(
            binding.cardAI,
            binding.cardInventory,
            binding.cardSync,
            binding.cardDesign
        )

        cards.forEach { card ->
            card.setOnClickListener { view ->
                // Pulse animation on click
                view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .scaleX(1.05f)
                            .scaleY(1.05f)
                            .setDuration(100)
                            .withEndAction {
                                view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start()
                            }
                            .start()
                    }
                    .start()

                // Show toast for demonstration
                val cardName = when (view.id) {
                    R.id.cardAI -> "AI Sales Prediction"
                    R.id.cardInventory -> "Inventory Control"
                    R.id.cardSync -> "Firebase Sync"
                    R.id.cardDesign -> "Material 3 Design"
                    else -> "Feature"
                }
                Toast.makeText(this, "$cardName feature", Toast.LENGTH_SHORT).show()
            }
        }
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
                    Toast.makeText(this, "Session expired or invalid", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop auto-scroll when activity is not visible
        isAutoScrolling = false
        autoScrollHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        // Resume auto-scroll when activity becomes visible
        isAutoScrolling = true
        startAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        autoScrollHandler.removeCallbacksAndMessages(null)
    }
}