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

package com.tlnacl.reactiveapp.businesslogic.http

import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

/**
 * Since this app only has a static backend providing some static json responses,
 * we have to calculate some things locally on the app users device, that otherwise would be done
 * on
 * a real backend server.
 *
 * All app components should interact with this decorator class and not with the real retrofit
 * interface.
 *
 * @author Hannes Dorfmann
 */
class ProductBackendApiDecorator(private val api: ProductBackendApi) {

    /**
     * Get a list with all products from backend, in parallel
     */
    suspend fun getAllProducts(): List<Product> = coroutineScope{
//        (0..3).fold(ArrayList<Product>(), {
//            acc, i -> suspend {
//            val product = async { getProducts(i) }
//            acc.addAll(product.await())
//            acc
//        } })
        val productList = ArrayList<Product>()
        val p0 = async { getProducts(0) }
        val p1 = async { getProducts(1) }
        val p2 = async { getProducts(2) }
        val p3 = async { getProducts(3) }

        productList.addAll(p0.await())
        productList.addAll(p1.await())
        productList.addAll(p2.await())
        productList.addAll(p3.await())
        productList
    }

    /**
     * Get a list with all categories
     */
    suspend fun getAllCategories(): List<String> = coroutineScope {
        getAllProducts().map { product ->
            product.category
        }.toHashSet().toList()
    }

    suspend fun getProducts(pagination: Int): List<Product> {
        return api.getProducts(pagination)
    }

    /**
     * Get all products of a certain category
     *
     * @param categoryName The name of the category
     */
    suspend fun getAllProductsOfCategory(categoryName: String): List<Product> {
        return getAllProducts()
                .filter { product -> product.category == categoryName }
    }

    /**
     * Get the product with the given id
     *
     * @param productId The product id
     */
    suspend fun getProduct(productId: Int): Product? {
        return getAllProducts().find { it.id == productId }
    }
}
