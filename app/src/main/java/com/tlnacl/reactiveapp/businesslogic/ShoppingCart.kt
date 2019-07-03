/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tlnacl.reactiveapp.businesslogic

import com.tlnacl.reactiveapp.businesslogic.model.Product
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
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
