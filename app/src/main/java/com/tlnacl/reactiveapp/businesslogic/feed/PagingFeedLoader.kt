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

package com.tlnacl.reactiveapp.businesslogic.feed

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * @author Hannes Dorfmann
 */

class PagingFeedLoader(private val backend: ProductBackendApiDecorator) {
    private var currentPage = 1
    private var endReached = false
    private var newestPageLoaded = false

    suspend fun newestPage(): List<Product> {
        return if (newestPageLoaded) {
            delay(1000)
            emptyList()
        } else {
            newestPageLoaded = true
            backend.getProducts(0)
        }

    }

    suspend fun nextPage(): List<Product> {
        // I know, it's not a pure function nor elegant code
        // but that is not the purpose of this demo.
        // This code should be understandable by everyone.

        return if (endReached) {
            emptyList()
        } else {

            val products = backend.getProducts(currentPage)
            currentPage++
            if (products.isEmpty()) {
                endReached = true
            }
            products
        }
    }
}
