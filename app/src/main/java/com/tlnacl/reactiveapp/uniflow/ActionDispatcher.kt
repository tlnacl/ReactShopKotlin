package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState
import timber.log.Timber
import kotlin.reflect.KClass

class ActionDispatcher(
        private val coroutineScope: CoroutineScope,
        private val dataStore: ViewDataStore,
        private val dataFlow: DataFlow,
        private val dispatcherProvider: DispatcherProvider,
        val tag: String
) {
    fun getCurrentState(): ViewState = dataStore.currentState

    fun action(onAction: ActionFunction<ViewState>): ActionFlow = action(onAction) { error, state -> dataFlow.onError(error, state, this) }

    fun action(onAction: ActionFunction<ViewState>, onError: ActionErrorFunction): ActionFlow {
        return actionOn(ViewState::class, onAction, onError)
    }

    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionOn(stateClass, onAction) { error, state -> dataFlow.onError(error, state, this) }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow {
        val currentState = getCurrentState()
        return if (stateClass.isInstance(currentState)) {
            val action = ActionFlow(onAction as ActionFunction<ViewState>, onError, dataStore)
            reduceAction(action)
            action
        } else {
            action { sendEvent { ViewEvent.BadOrWrongState(currentState) } }
        }
    }

    private fun reduceAction(action: ActionFlow) {
        Timber.v("$tag - action: $action")
        val currentState: ViewState = dataStore.currentState
        coroutineScope.launch(dispatcherProvider.io()) {
            try {
                action.onAction(action, currentState)
            } catch (e: Exception) {
                action.onError(action, e, currentState)
            }
        }
    }
}