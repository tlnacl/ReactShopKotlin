package com.tlnacl.reactiveapp.ui.detail

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.ProductDetail
import com.tlnacl.reactiveapp.ui.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by tlnacl on 11/07/17.
 */
class ProductDetailsPresenter @Inject constructor(private val api: ProductBackendApiDecorator) : BasePresenter<ProductDetailsView>() {
    fun initState() {
        mvpView?.render(ProductDetailsViewState.Loading)
    }

    fun handleUiEvent(productId: Int) {
        launch {
            try {
                val product = api.getProduct(productId)
                mvpView?.render(ProductDetailsViewState.Data(ProductDetail(product, false)))
            } catch (e : Exception) {
                mvpView?.render(ProductDetailsViewState.Error(e))
            }

        }
    }
}