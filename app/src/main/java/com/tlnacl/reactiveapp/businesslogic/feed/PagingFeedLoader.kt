package com.tlnacl.reactiveapp.businesslogic.feed

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.delay
import javax.inject.Inject

class PagingFeedLoader @Inject
constructor(private val backend: ProductBackendApiDecorator) {
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
