package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.ui.MvpView

/**
 * Created by tomt on 27/06/17.
 */
interface HomeView: MvpView {
    fun render(homeViewState: HomeViewState)
}