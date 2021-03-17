package com.tlnacl.reactiveapp.dataflow

import com.tlnacl.reactiveapp.DispatcherProvider
import com.tlnacl.reactiveapp.dataflow.data.ViewEvent
import com.tlnacl.reactiveapp.dataflow.data.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.reflect.KClass

class ActionDispatcher(
        private val coroutineScope: CoroutineScope,
        private val dataPublisher: LiveDataPublisher,
        private val dataFlow: DataFlow,
        private val dispatcherProvider: DispatcherProvider,
        val tag: String
) {
    fun getCurrentState(): ViewState = dataPublisher.currentState

    fun action(onAction: ActionFunction<ViewState>): ActionFlow = action(onAction) { error, state -> dataFlow.onError(error, state, this) }

    fun action(onAction: ActionFunction<ViewState>, onError: ActionErrorFunction): ActionFlow {
        return actionOn(ViewState::class, onAction, onError)
    }

    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionOn(stateClass, onAction) { error, state -> dataFlow.onError(error, state, this) }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow {
        val currentState = getCurrentState()
        return if (stateClass.isInstance(currentState)) {
            val action = ActionFlow(onAction as ActionFunction<ViewState>, onError, dataPublisher)
            reduceAction(action)
            action
        } else {
            action { sendEvent { ViewEvent.BadOrWrongState(currentState) } }
        }
    }

    private fun reduceAction(action: ActionFlow) {
        Timber.v("$tag - action: $action")
        val currentState: ViewState = getCurrentState()
        coroutineScope.launch(dispatcherProvider.io()) {
            try {
                action.onAction(action, currentState)
            } catch (e: Exception) {
                action.onError(action, e, currentState)
            }
        }
    }
}