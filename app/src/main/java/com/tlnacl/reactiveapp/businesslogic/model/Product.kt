package com.tlnacl.reactiveapp.businesslogic.model

data class Product(
    val id: Int,
    val image: String,
    val name: String,
    val category: String,
    val description: String,
    val price: Double
) : FeedItem