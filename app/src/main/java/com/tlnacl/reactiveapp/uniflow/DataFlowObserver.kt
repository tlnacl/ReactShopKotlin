package com.tlnacl.reactiveapp.uniflow

import androidx.lifecycle.LifecycleOwner
import com.tlnacl.reactiveapp.uniflow.data.Event
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState

/**
 * DataFlow Observers for states & events
 */

fun LifecycleOwner.onStates(vm: DataFlowBaseViewModel, handleStates: (ViewState) -> Unit) {
    vm.dataPublisher.states.observe(this, { state: ViewState? -> state?.let { handleStates(state) } })
}

//In case we need to peek event already handled
fun LifecycleOwner.onRawEvents(vm: DataFlowBaseViewModel, handleEvents: (Event<*>) -> Unit) {
    vm.dataPublisher.events.observe(this, { event -> event?.let { handleEvents(event) } })
}

fun LifecycleOwner.onEvents(vm: DataFlowBaseViewModel, handleEvents: (ViewEvent) -> Unit) {
    vm.dataPublisher.events.observe(this, { event -> event?.take()?.let { handleEvents(it) } })
}