package com.tlnacl.reactiveapp.businesslogic.model

data class ProductDetail(
    val product: Product,
    val isInShoppingCart: Boolean = false
)