package com.rajatt7z.retailx.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rajatt7z.retailx.databinding.ItemEmployeeBinding
import com.rajatt7z.retailx.models.Employee

class EmployeeAdapter(
    private val onEmployeeClick: (Employee) -> Unit,
    private val onDeleteClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = differ.currentList[position]
        holder.binding.apply {
            tvEmployeeName.text = employee.name
            tvEmployeeRole.text = employee.role
            tvEmployeePhone.text = employee.phone
            
            root.setOnClickListener {
                onEmployeeClick(employee)
            }
            
            btnDelete.setOnClickListener {
                onDeleteClick(employee)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
