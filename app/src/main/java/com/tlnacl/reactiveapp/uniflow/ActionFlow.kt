package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.ViewData
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState
import kotlinx.coroutines.flow.FlowCollector

typealias ActionFunction<T> = suspend ActionFlow.(T) -> (Unit)
typealias ActionErrorFunction = suspend ActionFlow.(Exception, ViewState) -> (Unit)

class ActionFlow(
        val onSuccess: ActionFunction<ViewState>,
        val onError: ActionErrorFunction
) {
    internal lateinit var flow: FlowCollector<ViewData>

    suspend fun setState(state: ViewState) {
        flow.emit(state)
    }

    suspend fun setState(state: () -> ViewState) {
        flow.emit(state())
    }

    suspend fun setStateAsync(state: suspend () -> ViewState) {
        flow.emit(state())
    }

    suspend fun sendEvent(event: ViewEvent) {
        flow.emit(event)
    }

    suspend fun sendEvent(event: () -> ViewEvent) {
        flow.emit(event())
    }
}