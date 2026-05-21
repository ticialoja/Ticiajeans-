package com.example.model

data class Product(
    val id: String,
    val title: String,
    val url: String,
    val imageUrl: String,
    val price: String,
    val originalPrice: String? = null,
    val isSale: Boolean = false,
    val category: String = "Geral",
    val description: String = ""
) {
    // Utility to get direct clean image URL
    val cleanImageUrl: String
        get() = when {
            imageUrl.startsWith("//") -> "https:$imageUrl"
            imageUrl.isEmpty() -> "https://dcdn-us.mitiendanube.com/stores/001/170/537/themes/common/logo-1459155799-1686994540-aed96c7348194011a4480ffbef02274e1686994540.jpg"
            else -> imageUrl
        }

    val cleanProductUrl: String
        get() = when {
            url.startsWith("//") -> "https:$url"
            url.startsWith("/") -> "https://ticia.lojavirtualnuvem.com.br$url"
            else -> url
        }
}

data class Category(
    val name: String,
    val url: String
)
