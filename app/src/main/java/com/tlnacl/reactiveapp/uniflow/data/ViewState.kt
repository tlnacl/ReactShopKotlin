package com.tlnacl.reactiveapp.uniflow.data

open class ViewState : ViewData {
    object Empty : ViewState() {
        override fun toString(): String = "ViewState.Empty"
    }

    object Loading : ViewState() {
        override fun toString(): String = "ViewState.Loading"
    }

    object Success : ViewState() {
        override fun toString(): String = "ViewState.Success"
    }

    object Failed : ViewState() {
        override fun toString(): String = "ViewState.Failed"
    }
}


