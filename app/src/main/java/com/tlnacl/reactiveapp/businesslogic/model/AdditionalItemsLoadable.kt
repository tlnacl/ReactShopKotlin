package com.tlnacl.reactiveapp.businesslogic.model

/**
 * This is a indicator that also some more items are available that could be loaded
 */
data class AdditionalItemsLoadable(
    val moreItemsAvailableCount: Int,
    val groupName: String,
    val loading: Boolean = false,
    val loadingError: Throwable? = null
) : FeedItem