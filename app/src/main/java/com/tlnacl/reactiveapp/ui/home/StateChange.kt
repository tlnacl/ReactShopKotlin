package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.model.FeedItem

/**
 * Created by tlnacl on 3/07/17.
 */
sealed class StateChange {
    object FirstPageLoading : StateChange()
    data class FirstPageLoaded(val data: List<FeedItem>) : StateChange()
    data class FirstPageError(val error: Throwable) : StateChange()
    object NextPageLoading : StateChange()
    data class NextPageLoaded(val data: List<FeedItem>) : StateChange()
    data class NextPageError(val error: Throwable) : StateChange()
    object PullToRefreshLoading : StateChange()
    data class PullToRefreshLoaded(val data: List<FeedItem>) : StateChange()
    data class PullToRefreshError(val error: Throwable) : StateChange()
    data class ProductsOfCategoryLoading(val categoryName: String) : StateChange()
    data class ProductsOfCategoryLoaded(val categoryName: String, val data: List<FeedItem>) : StateChange()
    data class ProductsOfCategoryError(val categoryName: String, val error: Throwable) : StateChange()
}