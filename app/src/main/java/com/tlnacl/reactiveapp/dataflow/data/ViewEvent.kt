package com.tlnacl.reactiveapp.dataflow.data

import androidx.annotation.StringRes

open class ViewEvent : ViewData {
    object Loading : ViewEvent() {
        override fun toString(): String = "ViewEvent.Loading"
    }

    object Success : ViewEvent() {
        override fun toString(): String = "ViewEvent.Success"
    }

    data class Error(@StringRes val stringRes: Int? = null, val error: Throwable? = null, val state: ViewState? = null) : ViewEvent() {
        constructor(error: Throwable) : this(null, error, null)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Error

            if (stringRes != other.stringRes) return false
            if (error?.javaClass != other.error?.javaClass) return false
            if (state != other.state) return false

            return true
        }

        override fun hashCode(): Int {
            var result = stringRes ?: 0
            result = 31 * result + (error?.hashCode() ?: 0)
            result = 31 * result + (state?.hashCode() ?: 0)
            return result
        }
    }

    data class BadOrWrongState(val currentState: ViewState) : ViewEvent()
    data class StateUpdate(val currentState: ViewState) : ViewEvent()
}
