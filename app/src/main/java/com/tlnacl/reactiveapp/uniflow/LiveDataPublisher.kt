package com.tlnacl.reactiveapp.uniflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tlnacl.reactiveapp.DispatcherProvider
import com.tlnacl.reactiveapp.uniflow.data.Event
import com.tlnacl.reactiveapp.uniflow.data.ViewEvent
import com.tlnacl.reactiveapp.uniflow.data.ViewState
import kotlinx.coroutines.withContext

class LiveDataPublisher(defaultState: ViewState, val dispatcherProvider: DispatcherProvider) {
    private val _states = MutableLiveData<ViewState>()
    private val _events = MutableLiveData<Event<ViewEvent>>()

    //For immutable
    val states: LiveData<ViewState> = _states
    val events: LiveData<Event<ViewEvent>> = _events

    var currentState: ViewState = defaultState
        private set

    init {
        _states.value = defaultState
    }

    suspend fun publishState(state: ViewState) {
        withContext(dispatcherProvider.main()) {
            currentState = state
            _states.value = state
        }
    }

    suspend fun publishEvent(event: ViewEvent) {
        withContext(dispatcherProvider.main()) {
            _events.value = Event(event)
        }
    }
}