package com.tlnacl.reactiveapp.ui.detail

import com.tlnacl.reactiveapp.businesslogic.model.ProductDetail

/**
 * Created by tlnacl on 11/07/17.
 */
sealed class ProductDetailsViewState {
    object Loading : ProductDetailsViewState()
    data class Error(val error: Throwable) : ProductDetailsViewState()
    data class Data(val data: ProductDetail) : ProductDetailsViewState()
}