package com.tlnacl.reactiveapp.ui.home

/**
 * Created by tomt on 27/06/17.
 */
sealed class HomeUiEvent {
    object LoadFirstPage : HomeUiEvent()
    object LoadNextPage : HomeUiEvent()
    object PullToRefresh : HomeUiEvent()
    object NoChange: HomeUiEvent()
    data class LoadAllProductsFromCategory(val categoryName: String) : HomeUiEvent()
}