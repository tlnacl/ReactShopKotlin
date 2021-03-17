package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.dataflow.data.ViewEvent

sealed class HomeViewEvent : ViewEvent() {
    object PullToRefreshSuccess : HomeViewEvent()
    data class Error(val error: Throwable) : HomeViewEvent()
}