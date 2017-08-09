package com.tlnacl.reactiveapp.ui.detail

import com.tlnacl.reactiveapp.ui.MvpView

/**
 * Created by tlnacl on 11/07/17.
 */
interface ProductDetailsView : MvpView {
    fun render(productDetailsViewState: ProductDetailsViewState)
}