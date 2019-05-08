package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.model.FeedItem

/**
 * Created by tlnacl on 2/07/17.
 */
sealed class LoadCategoryResult{
    object Loading : LoadCategoryResult()
    data class Success(val data: List<FeedItem>) : LoadCategoryResult()
    object Error : LoadCategoryResult()
}