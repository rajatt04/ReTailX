package com.rajatt7z.retailx.models

data class Employee(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "", // Store Manager, Inventory Manager, Sales Executive
    val permissions: String = "", // Viewer, Editor
    val userType: String = "employee",
    val createdAt: Long = 0L
)
