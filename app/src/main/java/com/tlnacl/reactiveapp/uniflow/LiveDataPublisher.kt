package com.tlnacl.reactiveapp.uniflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tlnacl.reactiveapp.onMain
import com.tlnacl.reactiveapp.uniflow.data.Event
import com.tlnacl.reactiveapp.uniflow.data.UIEvent
import com.tlnacl.reactiveapp.uniflow.data.UIState

class LiveDataPublisher(defaultState: UIState) {
    private val _states = MutableLiveData<UIState>()
    private val _events = MutableLiveData<Event<UIEvent>>()

    //For immutable
    val states: LiveData<UIState> = _states
    val events: LiveData<Event<UIEvent>> = _events

    init {
        _states.value = defaultState
    }

    suspend fun publishState(state: UIState) {
        onMain(immediate = true) {
            _states.value = state
        }
    }

    suspend fun publishEvent(event: UIEvent) {
        onMain(immediate = true) {
            _events.value = Event(event)
        }
    }
}