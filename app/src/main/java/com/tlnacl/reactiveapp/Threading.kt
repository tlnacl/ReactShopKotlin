package com.tlnacl.reactiveapp

import kotlinx.coroutines.*


/**
 * Execute job in Main thread
 */
fun CoroutineScope.launchOnMain(block: suspend CoroutineScope.() -> Unit) = launch(DispatcherProvider.dispatchers.main(), block = block)

/**
 * Execute job in  Default Thread
 */
fun CoroutineScope.launchOnDefault(block: suspend CoroutineScope.() -> Unit) = launch(DispatcherProvider.dispatchers.default(), block = block)

/**
 * Execute job in  IO Thread
 */
fun CoroutineScope.launchOnIO(block: suspend CoroutineScope.() -> Unit) = launch(DispatcherProvider.dispatchers.io(), block = block)

/**
 * Switch current execution context to Main thread
 */
suspend fun <T> onMain(immediate: Boolean = false, block: suspend CoroutineScope.() -> T) = withContext(getMainOrImmediateDispatcher(immediate), block = block)

private fun getMainOrImmediateDispatcher(immediate: Boolean) =
        if (immediate && DispatcherProvider.dispatchers.main() is MainCoroutineDispatcher) (DispatcherProvider.dispatchers.main() as MainCoroutineDispatcher).immediate else DispatcherProvider.dispatchers.main()

/**
 * Switch current execution context to Default Thread
 */
suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T) = withContext(DispatcherProvider.dispatchers.default(), block = block)

/**
 * Switch current execution context to IO Thread
 */
suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) = withContext(DispatcherProvider.dispatchers.io(), block = block)