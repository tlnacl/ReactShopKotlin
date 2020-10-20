package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.UIState
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Unidirectional Data Flow
 */
interface DataFlow {
    fun getCurrentState(): UIState
    fun <T : UIState> getCurrentStateOrNull(stateClass: KClass<T>): T?
    fun action(onAction: ActionFunction<UIState>): ActionFlow
    fun action(onAction: ActionFunction<UIState>, onError: ActionErrorFunction): ActionFlow
    fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow
    fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow
    suspend fun onError(error: Exception, currentState: UIState, flow: ActionFlow) {
        error.printStackTrace()
        Timber.w("Uncaught error: $error")
        throw error
    }
}

inline fun <reified T : UIState> DataFlow.getCurrentStateOrNull(): T? = getCurrentStateOrNull(T::class)
inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction<T>): ActionFlow = actionOn(T::class, onAction)
inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction<T>, noinline onError: ActionErrorFunction): ActionFlow = actionOn(T::class, onAction, onError)