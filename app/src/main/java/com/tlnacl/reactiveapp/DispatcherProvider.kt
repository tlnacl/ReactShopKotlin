package com.tlnacl.reactiveapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * use DispatcherProvider instead of hard code dispatcher for unit test
 */
interface DispatcherProvider {

    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

class DefaultDispatcherProvider : DispatcherProvider
object AppCoroutineDispatcher {
    var dispatcher: DispatcherProvider = DefaultDispatcherProvider()
}
