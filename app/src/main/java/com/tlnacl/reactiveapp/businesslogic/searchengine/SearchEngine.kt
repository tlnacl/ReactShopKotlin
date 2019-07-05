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

package com.tlnacl.reactiveapp.businesslogic.searchengine

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * With this class you can search for products
 *
 * @author Hannes Dorfmann
 */
class SearchEngine @Inject
constructor(private val backend: ProductBackendApiDecorator) {

    suspend fun searchFor(searchQueryText: String): List<Product> {

        return if (searchQueryText.isEmpty()) {
            throw IllegalArgumentException("SearchQueryTest is blank")
        } else {
            delay(1000)
            backend.getAllProducts().filter { isProductMatchingSearchCriteria(it, searchQueryText) }
        }

    }

    /**
     * Filters those items that contains the search query text in name, description or category
     */
    private fun isProductMatchingSearchCriteria(product: Product, searchQueryText: String): Boolean {
        val words = searchQueryText.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (w in words) {
            if (product.name.contains(w)) return true
            if (product.description.contains(w)) return true
            if (product.category.contains(w)) return true
        }
        return false
    }
}
