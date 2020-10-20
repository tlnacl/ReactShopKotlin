package com.tlnacl.reactiveapp.uniflow.data

/**
 * Data Flow Event wrapper
 */
data class Event<out T : ViewEvent>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and put the event has handled
     */
    fun take(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Return and execute code on given value
     * put the event has handled
     */
    fun take(code: (T) -> Unit) {
        take()?.let { code(it) }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = content
}
