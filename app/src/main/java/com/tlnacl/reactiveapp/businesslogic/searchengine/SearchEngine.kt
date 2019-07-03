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
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * With this class you can search for products
 *
 * @author Hannes Dorfmann
 */
class SearchEngine @Inject
constructor(private val backend: ProductBackendApiDecorator) {

    fun searchFor(searchQueryText: String): Observable<List<Product>> {

        if (searchQueryText == null) {
            return Observable.error(NullPointerException("SearchQueryText == null"))
        }

        return if (searchQueryText.isEmpty()) {
            Observable.error(IllegalArgumentException("SearchQueryTest is blank"))
        } else backend.allProducts
                .delay(1000, TimeUnit.MILLISECONDS)
                .flatMap { Observable.fromIterable(it) }
                .filter { product -> isProductMatchingSearchCriteria(product, searchQueryText) }
                .toList()
                .toObservable()

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
