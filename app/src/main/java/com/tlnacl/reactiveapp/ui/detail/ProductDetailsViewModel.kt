package com.tlnacl.reactiveapp.ui.detail

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.ProductDetail
import com.tlnacl.reactiveapp.uniflow.DataFlowBaseViewModel
import com.tlnacl.reactiveapp.uniflow.data.UIState
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(private val api: ProductBackendApiDecorator)
    : DataFlowBaseViewModel(defaultState = UIState.Loading) {

    fun getDetail(productId: Int) = action(
            onAction = {
                val product = api.getProduct(productId)
                setState(ProductDetailsViewState(ProductDetail(product, false)))
            },
            onError = { error, _ -> setState { UIState.Failed("getProduct failed", error) } }
    )
}