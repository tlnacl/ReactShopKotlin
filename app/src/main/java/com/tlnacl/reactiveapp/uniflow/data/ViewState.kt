package com.tlnacl.reactiveapp.uniflow.data

import java.lang.Exception

/**
 * Data Flow UI State
 */
open class ViewState : ViewData {
    object Empty : ViewState() {
        override fun toString(): String = "UIState.Empty"
    }

    object Loading : ViewState() {
        override fun toString(): String = "UIState.Loading"
    }

    object Success : ViewState() {
        override fun toString(): String = "UIState.Success"
    }

    data class Failed(val message: String? = null, val error: UIError? = null, val state: ViewState? = null) : ViewState() {
        constructor(message: String? = null) : this(message, null as UIError)
        constructor(message: String? = null, state: ViewState? = null) : this(message, null as UIError, state)
        constructor(message: String? = null, error: Throwable? = null, state: ViewState? = null) : this(message, error?.toUIError(), state)
        constructor(message: String? = null, error: Exception? = null, state: ViewState? = null) : this(message, error?.toUIError(), state)
    }
}


