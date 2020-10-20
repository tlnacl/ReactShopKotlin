package com.tlnacl.reactiveapp.uniflow

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.tlnacl.reactiveapp.uniflow.data.Event
import com.tlnacl.reactiveapp.uniflow.data.ViewState

/**
 * DataFlow Observers for states & events
 */

fun LifecycleOwner.onStates(vm: DataFlowBaseViewModel, handleStates: (ViewState) -> Unit) {
    vm.dataPublisher.states.observe(this, Observer { state: ViewState? -> state?.let { handleStates(state) } })
}

fun LifecycleOwner.onEvents(vm: DataFlowBaseViewModel, handleEvents: (Event<*>) -> Unit) {
    vm.dataPublisher.events.observe(this, Observer { event -> event?.let { handleEvents(event) } })
}