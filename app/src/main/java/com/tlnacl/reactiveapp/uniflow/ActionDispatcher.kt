package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.launchOnIO
import com.tlnacl.reactiveapp.uniflow.data.ViewData
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KClass

class ActionDispatcher(
        private val coroutineScope: CoroutineScope,
        private val dataStore: UIDataStore,
        private val dataFlow: DataFlow
) {
    fun getCurrentState(): ViewState = dataStore.currentState

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewState> getCurrentStateOrNull(): T? = getCurrentState() as? T

    fun action(onAction: ActionFunction<ViewState>): ActionFlow = action(onAction) { error, state -> dataFlow.onError(error, state, this) }

    fun action(onAction: ActionFunction<ViewState>, onError: ActionErrorFunction): ActionFlow = ActionFlow(onAction, onError).also {
        coroutineScope.launchOnIO {
            reduceAction(it)
        }
    }

    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionOn(stateClass, onAction) { error, state -> dataFlow.onError(error, state, this) }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow {
        val currentState = getCurrentState()
        return if (stateClass.isInstance(currentState)) {
            val action = ActionFlow(onAction as ActionFunction<ViewState>, onError)
            coroutineScope.launchOnIO {
                reduceAction(action)
            }
            action
        } else {
            action { sendEvent { ViewEvent.BadOrWrongState(currentState) } }
        }
    }

    private suspend fun reduceAction(action: ActionFlow) {
        val currentState: ViewState = dataStore.currentState
        try {
            val onSuccess = action.onSuccess
            flow<ViewData> {
                action.flow = this
                onSuccess(action, currentState)
            }.collect { dataUpdate ->
                dataStore.pushNewData(dataUpdate)
            }
        } catch (e: Exception) {
            val onError = action.onError
            flow<ViewData> {
                action.flow = this
                onError(action, e, currentState)
            }.collect { dataUpdate ->
                dataStore.pushNewData(dataUpdate)
            }
        }
    }
}