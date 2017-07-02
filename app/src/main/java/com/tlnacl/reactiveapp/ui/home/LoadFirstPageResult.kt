package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.model.FeedItem

/**
 * Created by tlnacl on 2/07/17.
 */
sealed class LoadFirstPageResult{
    object Loading : LoadFirstPageResult()
    data class Success(val data: List<FeedItem>) : LoadFirstPageResult()
    object Error : LoadFirstPageResult()
}