package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState

typealias ActionFunction<T> = suspend ActionFlow.(T) -> (Unit)
typealias ActionErrorFunction = suspend ActionFlow.(Exception, ViewState) -> (Unit)

class ActionFlow(
        val onAction: ActionFunction<ViewState>,
        val onError: ActionErrorFunction,
        private val dataPublisher: LiveDataPublisher
) {
    suspend fun setState(state: ViewState) {
        dataPublisher.publishState(state)
    }

    suspend fun setState(state: () -> ViewState) {
        dataPublisher.publishState(state())
    }

    suspend fun sendEvent(event: ViewEvent) {
        dataPublisher.publishEvent(event)
    }

    suspend fun sendEvent(event: () -> ViewEvent) {
        dataPublisher.publishEvent(event())
    }
}