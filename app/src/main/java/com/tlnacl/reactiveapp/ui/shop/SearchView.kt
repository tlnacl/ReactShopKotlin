package com.tlnacl.reactiveapp.ui.shop

import com.tlnacl.reactiveapp.ui.MvpView

/**
 * Created by tomt on 21/06/17.
 */
interface SearchView : MvpView {
    fun render(searchViewState: SearchViewState)
}