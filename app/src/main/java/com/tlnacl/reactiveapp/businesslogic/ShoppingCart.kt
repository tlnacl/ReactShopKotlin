package com.tlnacl.reactiveapp.businesslogic

import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.*

class ShoppingCart {
    private val itemsInShoppingCart = ConflatedBroadcastChannel(emptyList<Product>())

    /**
     * An observable to observe the items in the shopping cart
     */
    fun itemsInShoppingCart(): Flow<List<Product>> {
        return itemsInShoppingCart.asFlow()
    }

    /**
     * Adds a product to the shopping cart
     */
    suspend fun addProduct(product: Product) {
        val updatedShoppingCart = ArrayList<Product>()
        updatedShoppingCart.addAll(itemsInShoppingCart.value)
        updatedShoppingCart.add(product)
        itemsInShoppingCart.send(updatedShoppingCart)
    }

    /**
     * Remove a product to the shopping cart
     */
    suspend fun removeProduct(product: Product) {
        val updatedShoppingCart = ArrayList<Product>()
        updatedShoppingCart.addAll(itemsInShoppingCart.value)
        updatedShoppingCart.remove(product)
        itemsInShoppingCart.send(updatedShoppingCart)
    }

    /**
     * Remove a list of Products from the shopping cart
     */
    suspend fun removeProducts(products: List<Product>) {
        val updatedShoppingCart = ArrayList<Product>()
        updatedShoppingCart.addAll(itemsInShoppingCart.value)
        updatedShoppingCart.removeAll(products)
        itemsInShoppingCart.send(updatedShoppingCart)
    }
}
