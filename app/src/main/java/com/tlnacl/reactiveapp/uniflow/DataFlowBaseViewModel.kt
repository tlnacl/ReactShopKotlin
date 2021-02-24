package com.tlnacl.reactiveapp.uniflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlnacl.reactiveapp.AppCoroutineDispatcher
import com.tlnacl.reactiveapp.DispatcherProvider
import com.tlnacl.reactiveapp.uniflow.data.ViewState
import kotlin.reflect.KClass

abstract class DataFlowBaseViewModel(
        defaultState: ViewState = ViewState.Empty,
        val dispatcherProvider: DispatcherProvider = AppCoroutineDispatcher.dispatcher
) : ViewModel(), DataFlow {
    private val tag = this.toString()
    val dataPublisher: LiveDataPublisher = LiveDataPublisher(defaultState, dispatcherProvider)
    private val actionDispatcher: ActionDispatcher
        get() = ActionDispatcher(viewModelScope, dataPublisher, this, dispatcherProvider, tag)

    final override fun getCurrentState() = actionDispatcher.getCurrentState()
    final override fun action(onAction: ActionFunction<ViewState>): ActionFlow = actionDispatcher.action(onAction)
    final override fun action(onAction: ActionFunction<ViewState>, onError: ActionErrorFunction): ActionFlow = actionDispatcher.action(onAction, onError)
    final override fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionDispatcher.actionOn(stateClass, onAction)
    final override fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow = actionDispatcher.actionOn(stateClass, onAction, onError)

}