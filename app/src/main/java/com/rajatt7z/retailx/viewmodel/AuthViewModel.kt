package com.rajatt7z.retailx.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajatt7z.retailx.repository.AuthRepository
import com.rajatt7z.retailx.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authStatus = MutableLiveData<Resource<String>>()
    val authStatus: LiveData<Resource<String>> = _authStatus

    private val _userDetails = MutableLiveData<Resource<Map<String, Any>>>()
    val userDetails: LiveData<Resource<Map<String, Any>>> = _userDetails

    fun registerUser(email: String, password: String, userMap: HashMap<String, Any>) {
        _authStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.registerUser(email, password, userMap)
            _authStatus.value = result
        }
    }
    
    fun createEmployee(email: String, password: String, userMap: HashMap<String, Any>) {
        // Use the same authStatus to emit Loading and Success/Error
        // Since the UI will be listening to this when the dialog is open.
        _authStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.createEmployeeAccount(email, password, userMap)
            _authStatus.value = result
        }
    }

    fun loginUser(email: String, password: String, validationRole: String? = null) {
        _authStatus.value = Resource.Loading()
        viewModelScope.launch {
            val loginResult = repository.loginUser(email, password)
            if (loginResult is Resource.Success && validationRole != null) {
                // Login successful, now validate role
                val uid = repository.getCurrentUser()?.uid
                if (uid != null) {
                    val userDetailsResult = repository.getUserDetails(uid)
                    if (userDetailsResult is Resource.Success) {
                        val userType = userDetailsResult.data?.get("userType") as? String
                        if (userType == validationRole) {
                            _authStatus.value = loginResult // Role matches
                        } else {
                            // Role mismatch
                            repository.logout()
                            _authStatus.value = Resource.Error("Access Denied: This account is not a $validationRole account.")
                        }
                    } else {
                        // Failed to fetch details
                        repository.logout()
                        _authStatus.value = Resource.Error("Failed to verify account type.")
                    }
                } else {
                    _authStatus.value = Resource.Error("Authentication failed.")
                }
            } else {
                // Standard login or failure
                _authStatus.value = loginResult
            }
        }
    }

    fun fetchUserDetails(uid: String) {
        _userDetails.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getUserDetails(uid)
            _userDetails.value = result
        }
    }
    
    private val _employees = MutableLiveData<Resource<List<com.rajatt7z.retailx.models.Employee>>>()
    val employees: LiveData<Resource<List<com.rajatt7z.retailx.models.Employee>>> = _employees

    fun fetchEmployees() {
        _employees.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getEmployees()
            _employees.value = result
        }
    }

    fun updateEmployee(uid: String, updates: Map<String, Any>) {
        _authStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.updateEmployee(uid, updates)
            _authStatus.value = result
            // Refresh list after update
            if (result is Resource.Success) {
                fetchEmployees()
            }
        }
    }

    fun deleteEmployee(uid: String) {
        _authStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.deleteEmployee(uid)
            _authStatus.value = result
            if (result is Resource.Success) {
                fetchEmployees()
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}
