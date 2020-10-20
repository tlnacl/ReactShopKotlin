package com.tlnacl.reactiveapp.uniflow

import com.tlnacl.reactiveapp.uniflow.data.ViewState
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Unidirectional Data Flow
 */
interface DataFlow {
    fun getCurrentState(): ViewState
    fun <T : ViewState> getCurrentStateOrNull(stateClass: KClass<T>): T?
    fun action(onAction: ActionFunction<ViewState>): ActionFlow
    fun action(onAction: ActionFunction<ViewState>, onError: ActionErrorFunction): ActionFlow
    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow
    fun <T : ViewState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow
    suspend fun onError(error: Exception, currentState: ViewState, flow: ActionFlow) {
        error.printStackTrace()
        Timber.w("Uncaught error: $error")
        throw error
    }
}

inline fun <reified T : ViewState> DataFlow.getCurrentStateOrNull(): T? = getCurrentStateOrNull(T::class)
inline fun <reified T : ViewState> DataFlow.actionOn(noinline onAction: ActionFunction<T>): ActionFlow = actionOn(T::class, onAction)
inline fun <reified T : ViewState> DataFlow.actionOn(noinline onAction: ActionFunction<T>, noinline onError: ActionErrorFunction): ActionFlow = actionOn(T::class, onAction, onError)