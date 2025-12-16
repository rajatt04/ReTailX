package com.rajatt7z.retailx.models

import com.google.gson.annotations.SerializedName

data class ImgBBResponse(
    @SerializedName("data") val data: ImgBBData?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

data class ImgBBData(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("display_url") val displayUrl: String,
    @SerializedName("delete_url") val deleteUrl: String
)
