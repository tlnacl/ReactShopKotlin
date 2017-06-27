package com.tlnacl.reactiveapp.ui.shop

import com.tlnacl.reactiveapp.businesslogic.model.Product

/**
 * Created by tomt on 21/06/17.
 */
sealed class SearchViewState {
    object SearchNotStartedYet : SearchViewState()
    object Loading : SearchViewState()
    object EmptyResult : SearchViewState()
    data class SearchResult(val result: List<Product>) : SearchViewState()
    data class Error(val error: Throwable) : SearchViewState()
}