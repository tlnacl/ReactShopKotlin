package com.tlnacl.reactiveapp.uniflow.data

import java.lang.Exception

/**
 * Data Flow UI State
 */
open class UIState : UIData {
    object Empty : UIState() {
        override fun toString(): String = "UIState.Empty"
    }

    object Loading : UIState() {
        override fun toString(): String = "UIState.Loading"
    }

    object Success : UIState() {
        override fun toString(): String = "UIState.Success"
    }

    data class Failed(val message: String? = null, val error: UIError? = null, val state: UIState? = null) : UIState() {
        constructor(message: String? = null) : this(message, null as UIError)
        constructor(message: String? = null, state: UIState? = null) : this(message, null as UIError, state)
        constructor(message: String? = null, error: Throwable? = null, state: UIState? = null) : this(message, error?.toUIError(), state)
        constructor(message: String? = null, error: Exception? = null, state: UIState? = null) : this(message, error?.toUIError(), state)
    }
}


