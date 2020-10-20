package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.UIData
import com.tlnacl.reactiveapp.uniflow.data.UIEvent
import com.tlnacl.reactiveapp.uniflow.data.UIState
import timber.log.Timber

class UIDataStore(private val publisher: LiveDataPublisher, defaultState: UIState) {

    var currentState: UIState = defaultState
        private set

    suspend fun pushNewData(uiData: UIData) {
        Timber.d("push -> $uiData")
        when (uiData) {
            is UIState -> {
                currentState = uiData
                publisher.publishState(uiData)
            }
            is UIEvent -> {
                publisher.publishEvent(uiData)
            }
        }
    }
}