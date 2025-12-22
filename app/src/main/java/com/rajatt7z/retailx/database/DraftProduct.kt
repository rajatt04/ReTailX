package com.rajatt7z.retailx.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rajatt7z.retailx.models.Product

@Entity(tableName = "draft_products")
data class DraftProduct(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toProduct(): Product {
        val product = Product(
            id = id.toString(), // Using Room ID as temporary ID
            name = name,
            description = description,
            price = price,
            stock = stock,
            category = category,
            imageUrls = emptyList()
        )
        // Note: product.createdAt is Timestamp.now() by default which is fine
        product.isDraft = true
        return product
    }
}
