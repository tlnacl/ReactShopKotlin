package com.tlnacl.reactiveapp

import kotlinx.coroutines.CoroutineDispatcher

object DispatcherProvider {
    var dispatchers: DispatcherConfiguration = ApplicationDispatchers()
}

interface DispatcherConfiguration {
    fun main(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}

class ApplicationDispatchers : DispatcherConfiguration {
    override fun main() = kotlinx.coroutines.Dispatchers.Main
    override fun default() = kotlinx.coroutines.Dispatchers.Default
    override fun io() = kotlinx.coroutines.Dispatchers.IO
}
