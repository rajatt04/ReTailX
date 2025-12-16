package com.rajatt7z.retailx.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rajatt7z.retailx.models.Product
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    suspend fun addProduct(product: Product): String {
        val document = productsCollection.document()
        product.id = document.id
        document.set(product).await()
        return product.id
    }

    suspend fun updateProduct(product: Product) {
        productsCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete().await()
    }

    suspend fun getProduct(productId: String): Product? {
        return productsCollection.document(productId).get().await().toObject(Product::class.java)
    }

    suspend fun getAllProducts(): List<Product> {
        return productsCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().toObjects(Product::class.java)
    }

    private val imgBBService: ImgBBService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(ImgBBService::class.java)
    }

    suspend fun uploadImage(context: android.content.Context, imageUri: Uri): String {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            val stream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, stream)
            val byteArray = stream.toByteArray()
            val requestBody = okhttp3.RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
            val body = okhttp3.MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

            val response = imgBBService.uploadImage("2014858c52b15c2bec7584982b061739", body)
            if (response.success && response.data != null) {
                response.data.url
            } else {
                throw Exception("ImgBB Upload Failed: ${response.status}")
            }
        }
    }
}
