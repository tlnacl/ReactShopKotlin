package com.tlnacl.reactiveapp.uniflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tlnacl.reactiveapp.onMain
import com.tlnacl.reactiveapp.uniflow.data.Event
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState

class LiveDataPublisher(defaultState: ViewState) {
    private val _states = MutableLiveData<ViewState>()
    private val _events = MutableLiveData<Event<ViewEvent>>()

    //For immutable
    val states: LiveData<ViewState> = _states
    val events: LiveData<Event<ViewEvent>> = _events

    init {
        _states.value = defaultState
    }

    suspend fun publishState(state: ViewState) {
        onMain(immediate = true) {
            _states.value = state
        }
    }

    suspend fun publishEvent(event: ViewEvent) {
        onMain(immediate = true) {
            _events.value = Event(event)
        }
    }
}