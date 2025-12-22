package com.rajatt7z.retailx.models

import com.google.firebase.Timestamp

data class Product(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var stock: Int = 0,
    var category: String = "",
    var imageUrls: List<String> = emptyList(),
    var createdAt: Timestamp = Timestamp.now()
) {
    @get:com.google.firebase.firestore.Exclude
    var isDraft: Boolean = false
}
