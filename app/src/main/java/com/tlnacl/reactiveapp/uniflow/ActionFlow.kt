package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.UIData
import com.tlnacl.reactiveapp.uniflow.data.UIEvent
import com.tlnacl.reactiveapp.uniflow.data.UIState
import kotlinx.coroutines.flow.FlowCollector

typealias ActionFunction<T> = suspend ActionFlow.(T) -> (Unit)
typealias ActionErrorFunction = suspend ActionFlow.(Exception, UIState) -> (Unit)

class ActionFlow(
        val onSuccess: ActionFunction<UIState>,
        val onError: ActionErrorFunction
) {
    internal lateinit var flow: FlowCollector<UIData>

    suspend fun setState(state: UIState) {
        flow.emit(state)
    }

    suspend fun setState(state: () -> UIState) {
        flow.emit(state())
    }

    suspend fun setStateAsync(state: suspend () -> UIState) {
        flow.emit(state())
    }

    suspend fun sendEvent(event: UIEvent) {
        flow.emit(event)
    }

    suspend fun sendEvent(event: () -> UIEvent) {
        flow.emit(event())
    }
}