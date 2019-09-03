package com.tlnacl.reactiveapp.businesslogic

import com.tlnacl.reactiveapp.businesslogic.model.Product
import java.util.*

/**
 * Holds a list of items that has been added to the shopping cart
 *
 * @author Hannes Dorfmann
 */
class ShoppingCart {
    private val itemsInShoppingCart = BehaviorSubject.createDefault(emptyList<Product>())

    /**
     * An observable to observe the items in the shopping cart
     */
    fun itemsInShoppingCart(): Observable<List<Product>> {
        return itemsInShoppingCart
    }

    /**
     * Adds a product to the shopping cart
     */
    fun addProduct(product: Product): Completable {
        val updatedShoppingCart = ArrayList<Product>()
        updatedShoppingCart.addAll(itemsInShoppingCart.value!!)
        updatedShoppingCart.add(product)
        itemsInShoppingCart.onNext(updatedShoppingCart)
        return Completable.complete()
    }

    /**
     * Remove a product to the shopping cart
     */
    fun removeProduct(product: Product): Completable {
        val updatedShoppingCart = ArrayList<Product>()
        updatedShoppingCart.addAll(itemsInShoppingCart.value!!)
        updatedShoppingCart.remove(product)
        itemsInShoppingCart.onNext(updatedShoppingCart)
        return Completable.complete()
    }

    /**
     * Remove a list of Products from the shopping cart
     */
    fun removeProducts(products: List<Product>): Completable {
        val updatedShoppingCart = ArrayList<Product>()
        updatedShoppingCart.addAll(itemsInShoppingCart.value!!)
        updatedShoppingCart.removeAll(products)
        itemsInShoppingCart.onNext(updatedShoppingCart)
        return Completable.complete()
    }
}
