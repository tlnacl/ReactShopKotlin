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
import io.reactivex.Observable
import io.reactivex.functions.Function4
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
class ProductBackendApiDecorator @Inject
constructor(private val api: ProductBackendApi) {

    /**
     * Get a list with all products from backend
     */
    val allProducts: Observable<List<Product>>
        get() = Observable.zip(getProducts(0), getProducts(1), getProducts(2), getProducts(3),
                Function4<List<Product>, List<Product>, List<Product>, List<Product>, List<Product>>
                { products0, products1, products2, products3 ->
                    val productList = ArrayList<Product>()
                    productList.addAll(products0)
                    productList.addAll(products1)
                    productList.addAll(products2)
                    productList.addAll(products3)
                    productList
                })

    /**
     * Get a list with all categories
     */
    val allCategories: Observable<List<String>>
        get() = allProducts.map { products ->
            val categories = HashSet<String>()
            for (p in products) {
                categories.add(p.category)
            }

            val result = ArrayList<String>(categories.size)
            result.addAll(categories)
            result
        }

    fun getProducts(pagination: Int): Observable<List<Product>> {
        return api.getProducts(pagination)
    }

    /**
     * Get all products of a certain category
     *
     * @param categoryName The name of the category
     */
    fun getAllProductsOfCategory(categoryName: String): Observable<List<Product>> {
        return allProducts.flatMap { Observable.fromIterable(it) }
                .filter { product -> product.category == categoryName }
                .toList()
                .toObservable()
    }

    /**
     * Get the product with the given id
     *
     * @param productId The product id
     */
    fun getProduct(productId: Int): Observable<Product> {
        return allProducts.flatMap { products -> Observable.fromIterable(products) }
                .filter { product -> product.id == productId }
                .take(1)
    }
}
