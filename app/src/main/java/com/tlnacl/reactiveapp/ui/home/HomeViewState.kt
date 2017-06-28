package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.model.FeedItem

/**
 * Created by tomt on 27/06/17.
 */
data class HomeViewState(val loadingFirstPage: Boolean = false,
                         val firstPageError: Throwable? = null,
                         val data: List<FeedItem> = emptyList(),
                         val loadingNextPage: Boolean = false,
                         val nextPageError: Throwable? = null,
                         val loadingPullToRefresh: Boolean = false,
                         val pullToRefreshError: Throwable? = null)