package com.tlnacl.reactiveapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tlnacl.reactiveapp.ui.BaseViewModel
import kotlinx.coroutines.launch

/**
 *
 */
class ProductDetailsViewModel : BaseViewModel() {
    private val productDetailsLD = MutableLiveData<ProductDetailsViewState>()

    fun getProductDetails(): LiveData<ProductDetailsViewState> {
        return productDetailsLD
    }

    // TODO change to viewEvent
    fun doAction(productId: Int) {
        uiScope.launch {
            productDetailsLD.
        }
    }
}