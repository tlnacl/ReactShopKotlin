package com.tlnacl.reactiveapp.businesslogic.searchengine

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * With this class you can search for products
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
