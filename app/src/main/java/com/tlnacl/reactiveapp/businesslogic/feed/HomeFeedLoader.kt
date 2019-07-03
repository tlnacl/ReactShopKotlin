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
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.Product
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Builds the HomeFeed
 *
 * @author Hannes Dorfmann
 */
class HomeFeedLoader @Inject
constructor(private val groupedLoader: GroupedPagedFeedLoader,
            private val backendApi: ProductBackendApiDecorator) {

    /**
     * Typically triggered with a pull-to-refresh
     */
    fun loadNewestPage(): Observable<List<FeedItem>> {
        return groupedLoader.newestPage.delay(1, TimeUnit.SECONDS)
    }

    /**
     * Loads the first page
     */
    fun loadFirstPage(): Observable<List<FeedItem>> {
        return groupedLoader.groupedFirstPage.delay(1, TimeUnit.SECONDS)
    }

    /**
     * loads the next page (pagination)
     */
    fun loadNextPage(): Observable<List<FeedItem>> {
        return groupedLoader.groupedNextPage.delay(1, TimeUnit.SECONDS)
    }

    /**
     * Loads all items of  a given category
     *
     * @param categoryName the category name
     */
    fun loadProductsOfCategory(categoryName: String): Observable<List<Product>> {
        return backendApi.getAllProductsOfCategory(categoryName).delay(1, TimeUnit.SECONDS)
    }
}
