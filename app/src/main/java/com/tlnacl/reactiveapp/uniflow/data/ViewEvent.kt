package com.tlnacl.reactiveapp.uniflow.data

/**
 * Data Flow UI Event
 */
open class ViewEvent : ViewData {
    object Loading : ViewEvent() {
        override fun toString(): String = "UIEvent.Loading"
    }

    object Success : ViewEvent() {
        override fun toString(): String = "UIEvent.Success"
    }

    data class Error(val message: String? = null, val error: UIError? = null, val state: ViewState? = null) : ViewEvent() {
        constructor(message: String? = null) : this(message, null as? UIError)
        constructor(message: String? = null, error: Throwable? = null, state: ViewState? = null) : this(message, error?.toUIError(), state)
    }

    data class BadOrWrongState(val currentState: ViewState) : ViewEvent()
    data class StateUpdate(val currentState: ViewState) : ViewEvent()
}
