package com.tlnacl.reactiveapp.businesslogic.feed

import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import java.util.*
import javax.inject.Inject

/**
 * Takes a [PagingFeedLoader] but groups the resulting list of products into categories.
 * Assumption: Since we support pagination, we assume that there are not items of the same group on
 * different pages in the data feed retrieved from backend
 */
class GroupedPagedFeedLoader @Inject
constructor(private val feedLoader: PagingFeedLoader) {
    private val collapsedGroupProductItemCount = 3

    suspend fun groupedFirstPage(): List<FeedItem> = groupedNextPage()

    suspend fun groupedNextPage(): List<FeedItem> = groupByCategory(feedLoader.nextPage())

    suspend fun newestPage(): List<FeedItem> = groupByCategory(feedLoader.newestPage())

    private fun groupByCategory(originalListToGroup: List<Product>): List<FeedItem> {
        return originalListToGroup.groupBy { it.category }
            .map {
                val groupName = it.key
                val productsInGroup = it.value
                val items = ArrayList<FeedItem>()
                items.add(SectionHeader(groupName))
                if (collapsedGroupProductItemCount < productsInGroup.size) {
                    for (i in 0 until collapsedGroupProductItemCount) {
                        items.add(productsInGroup[i])
                    }
                    items.add(
                        AdditionalItemsLoadable(
                            productsInGroup.size - collapsedGroupProductItemCount,
                            groupName, false, null
                        )
                    )
                } else {
                    items.addAll(productsInGroup)
                }
                items
            }.flatten()
    }
}
