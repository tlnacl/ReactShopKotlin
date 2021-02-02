package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState

typealias ActionFunction<T> = suspend ActionFlow.(T) -> (Unit)
typealias ActionErrorFunction = suspend ActionFlow.(Exception, ViewState) -> (Unit)

class ActionFlow(
        val onAction: ActionFunction<ViewState>,
        val onError: ActionErrorFunction,
        private val dataStore: ViewDataStore
) {
    suspend fun setState(state: ViewState) {
        dataStore.pushNewData(state)
    }

    suspend fun setState(state: () -> ViewState) {
        dataStore.pushNewData(state())
    }

    suspend fun setStateAsync(state: suspend () -> ViewState) {
        dataStore.pushNewData(state())
    }

    suspend fun sendEvent(event: ViewEvent) {
        dataStore.pushNewData(event)
    }

    suspend fun sendEvent(event: () -> ViewEvent) {
        dataStore.pushNewData(event())
    }
}