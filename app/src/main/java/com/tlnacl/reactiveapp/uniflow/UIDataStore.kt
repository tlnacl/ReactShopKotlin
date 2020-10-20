package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.ViewData
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState
import timber.log.Timber

class UIDataStore(private val publisher: LiveDataPublisher, defaultState: ViewState) {

    var currentState: ViewState = defaultState
        private set

    suspend fun pushNewData(viewData: ViewData) {
        Timber.d("push -> $viewData")
        when (viewData) {
            is ViewState -> {
                currentState = viewData
                publisher.publishState(viewData)
            }
            is ViewEvent -> {
                publisher.publishEvent(viewData)
            }
        }
    }
}