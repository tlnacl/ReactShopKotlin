package com.tlnacl.reactiveapp.uniflow.data

/**
 * Data Flow UI Event
 */
open class UIEvent : UIData {
    object Loading : UIEvent() {
        override fun toString(): String = "UIEvent.Loading"
    }

    object Success : UIEvent() {
        override fun toString(): String = "UIEvent.Success"
    }

    data class Error(val message: String? = null, val error: UIError? = null, val state: UIState? = null) : UIEvent() {
        constructor(message: String? = null) : this(message, null as? UIError)
        constructor(message: String? = null, error: Throwable? = null, state: UIState? = null) : this(message, error?.toUIError(), state)
    }

    data class BadOrWrongState(val currentState: UIState) : UIEvent()
    data class StateUpdate(val currentState: UIState) : UIEvent()
}
