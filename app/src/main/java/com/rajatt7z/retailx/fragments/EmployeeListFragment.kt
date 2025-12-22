package com.rajatt7z.retailx.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajatt7z.retailx.R
import com.rajatt7z.retailx.adapters.EmployeeAdapter
import com.rajatt7z.retailx.databinding.FragmentEmployeeListBinding
import com.rajatt7z.retailx.models.Employee
import com.rajatt7z.retailx.utils.Resource
import com.rajatt7z.retailx.viewmodel.AuthViewModel

class EmployeeListFragment : Fragment() {

    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var employeeAdapter: EmployeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        setupObservers()
        
        viewModel.fetchEmployees()
    }

    private fun setupRecyclerView() {
        employeeAdapter = EmployeeAdapter(
            onEmployeeClick = { employee ->
                showEditEmployeeDialog(employee)
            },
            onDeleteClick = { employee ->
                showDeleteConfirmationDialog(employee)
            }
        )
        binding.rvEmployeeList.apply {
            adapter = employeeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showDeleteConfirmationDialog(employee: Employee) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Employee")
            .setMessage("Are you sure you want to delete ${employee.name}? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteEmployee(employee.uid)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupListeners() {
        binding.fabAddEmployee.setOnClickListener {
            showAddEmployeeDialog()
        }
    }

    private fun setupObservers() {
        viewModel.employees.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = resource.data ?: emptyList()
                    employeeAdapter.differ.submitList(list)
                    binding.tvEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.authStatus.observe(viewLifecycleOwner) { resource ->
             when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, resource.data, Toast.LENGTH_SHORT).show()
                    viewModel.fetchEmployees() // Refresh list
                }
                is Resource.Error -> {
                     val msg = resource.message ?: "Error"
                     if (!msg.contains("Login", true)) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                     }
                }
                else -> {}
             }
        }
    }

    private fun showAddEmployeeDialog() {
        val dialog = android.app.Dialog(requireContext(), R.style.Theme_ReTailX_FullScreenDialog)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)

        val dialogBinding = com.rajatt7z.retailx.databinding.DialogAddEmployeeBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        dialogBinding.toolbar.title = "Add New Employee"
        dialogBinding.toolbar.setNavigationOnClickListener { dialog.dismiss() }

        // Setup Spinner
        val roles = arrayOf("Store Manager", "Inventory Manager", "Sales Executive")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        dialogBinding.spinnerRole.setAdapter(adapter)

        // Default permission
        dialogBinding.rgPermissions.check(R.id.rbViewer)

        dialogBinding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_save -> {
                    val name = dialogBinding.etEmployeeName.text.toString().trim()
                    val phone = dialogBinding.etEmployeePhone.text.toString().trim()
                    val email = dialogBinding.etEmployeeEmail.text.toString().trim()
                    val password = dialogBinding.etEmployeePassword.text.toString().trim()
                    val role = dialogBinding.spinnerRole.text.toString()
                    val permission = if (dialogBinding.rgPermissions.checkedButtonId == R.id.rbEditor) "Editor" else "Viewer"

                    if (validation(name, phone, email, password)) {
                        val userMap: HashMap<String, Any> = hashMapOf(
                            "name" to name,
                            "phone" to phone,
                            "email" to email,
                            "userType" to "employee",
                            "role" to role,
                            "permissions" to permission,
                            "createdAt" to System.currentTimeMillis()
                        )

                        viewModel.createEmployee(email, password, userMap)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Please check all fields", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        dialog.show()
    }

    private fun showEditEmployeeDialog(employee: Employee) {
        val dialog = android.app.Dialog(requireContext(), R.style.Theme_ReTailX_FullScreenDialog)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)

        val dialogBinding = com.rajatt7z.retailx.databinding.DialogAddEmployeeBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        dialogBinding.toolbar.title = "Edit Employee"
        dialogBinding.toolbar.setNavigationOnClickListener { dialog.dismiss() }

        // Setup Spinner
        val roles = arrayOf("Store Manager", "Inventory Manager", "Sales Executive")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        dialogBinding.spinnerRole.setAdapter(adapter)

        // Pre-fill data
        dialogBinding.etEmployeeName.setText(employee.name)
        dialogBinding.etEmployeePhone.setText(employee.phone)
        dialogBinding.etEmployeeEmail.setText(employee.email)
        dialogBinding.etEmployeePassword.hint = "Password"

        // Enable editing for Email and Password
        dialogBinding.etEmployeeEmail.isEnabled = true
        dialogBinding.etEmployeePassword.isEnabled = true

        val roleIndex = roles.indexOf(employee.role)
        if (roleIndex >= 0) dialogBinding.spinnerRole.setText(employee.role, false)

        if (employee.permissions == "Editor") {
            dialogBinding.rgPermissions.check(R.id.rbEditor)
        } else {
            dialogBinding.rgPermissions.check(R.id.rbViewer)
        }

        // Show warning toast once
        Toast.makeText(context, "Note: Changing Email/Password only updates the database record.", Toast.LENGTH_LONG).show()

        dialogBinding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_save -> {
                    val name = dialogBinding.etEmployeeName.text.toString().trim()
                    val phone = dialogBinding.etEmployeePhone.text.toString().trim()
                    val email = dialogBinding.etEmployeeEmail.text.toString().trim()
                    val password = dialogBinding.etEmployeePassword.text.toString().trim()
                    val role = dialogBinding.spinnerRole.text.toString()
                    val permission = if (dialogBinding.rgPermissions.checkedButtonId == R.id.rbEditor) "Editor" else "Viewer"

                    if (name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
                        val updates = hashMapOf<String, Any>(
                            "name" to name,
                            "phone" to phone,
                            "email" to email,
                            "role" to role,
                            "permissions" to permission
                        )

                        // Only include password if user typed something
                        if (password.isNotEmpty()) {
                            updates["password"] = password
                        }

                        viewModel.updateEmployee(employee.uid, updates)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Name, Phone and Email cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        dialog.show()
    }

    private fun validation(name: String, phone: String, email: String, password: String): Boolean {
         if (name.length < 3) return false
         if (phone.length < 10) return false
         if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
         if (password.length < 6) return false
         return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
