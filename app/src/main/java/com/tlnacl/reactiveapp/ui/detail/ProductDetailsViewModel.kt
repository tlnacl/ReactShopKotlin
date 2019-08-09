package com.tlnacl.reactiveapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.ProductDetail
import com.tlnacl.reactiveapp.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
class ProductDetailsViewModel @Inject constructor(private val api: ProductBackendApiDecorator): BaseViewModel() {
    private val productDetailsLD = MutableLiveData<ProductDetailsViewState>()

    fun getProductDetails(): LiveData<ProductDetailsViewState> {
        return productDetailsLD
    }

    // TODO change to viewEvent
    fun doAction(productId: Int) {
        productDetailsLD.value = ProductDetailsViewState.Loading
        uiScope.launch {
            try {
                val product = api.getProduct(productId)
                productDetailsLD.value = ProductDetailsViewState.Data(ProductDetail(product, false))
            } catch (e : Exception) {
                productDetailsLD.value = ProductDetailsViewState.Error(e)
            }
        }
    }
}