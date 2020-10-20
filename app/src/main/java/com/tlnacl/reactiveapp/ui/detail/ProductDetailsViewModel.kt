package com.tlnacl.reactiveapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.ProductDetail
import com.tlnacl.reactiveapp.uniflow.DataFlowBaseViewModel
import com.tlnacl.reactiveapp.uniflow.data.UIState
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(private val api: ProductBackendApiDecorator): DataFlowBaseViewModel() {

    fun getDetail(productId: Int) = action(
            onAction = {
                val product = api.getProduct(productId)
                setState(ProductDetailsViewState(ProductDetail(product, false)))
            },
            onError = { error,_ -> setState { UIState.Failed("getProduct failed", error) }}
    )
}