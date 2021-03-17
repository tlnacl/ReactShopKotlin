package com.tlnacl.reactiveapp.dataflow

import com.tlnacl.reactiveapp.dataflow.data.ViewEvent
import com.tlnacl.reactiveapp.dataflow.data.ViewState

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