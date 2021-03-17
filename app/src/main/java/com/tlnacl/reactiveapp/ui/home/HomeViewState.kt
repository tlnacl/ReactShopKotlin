package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.dataflow.data.ViewState

/**
 * Created by tomt on 27/06/17.
 */
data class HomeViewState(val loadingNextPage: Boolean = false,
                         val loadingPullToRefresh: Boolean = false,
                         val data: List<FeedItem> = emptyList()) : ViewState()