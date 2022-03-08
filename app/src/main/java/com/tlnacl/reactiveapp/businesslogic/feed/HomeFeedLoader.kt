package com.tlnacl.reactiveapp.businesslogic.feed

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Builds the HomeFeed
 */
class HomeFeedLoader @Inject
constructor(private val groupedLoader: GroupedPagedFeedLoader,
            private val backendApi: ProductBackendApiDecorator) {

    /**
     * Typically triggered with a pull-to-refresh
     */
    suspend fun loadNewestPage(): List<FeedItem> {
        delay(1000)
        return groupedLoader.newestPage()
    }

    /**
     * Loads the first page
     */
    suspend fun loadFirstPage(): List<FeedItem> {
        delay(1000)
        return groupedLoader.groupedFirstPage()
    }

    /**
     * loads the next page (pagination)
     */
    suspend fun loadNextPage(): List<FeedItem> {
        delay(1000)
        return groupedLoader.groupedNextPage()
    }

    /**
     * Loads all items of  a given category
     *
     * @param categoryName the category name
     */
    suspend fun loadProductsOfCategory(categoryName: String): List<Product> {
        delay(1000)
        return backendApi.getAllProductsOfCategory(categoryName)
    }
}
