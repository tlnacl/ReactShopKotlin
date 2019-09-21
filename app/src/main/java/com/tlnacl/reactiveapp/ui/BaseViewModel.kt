package com.tlnacl.reactiveapp.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Good question Implement CoroutineScope or hold a CoroutineScope
 */
abstract class BaseViewModel : ViewModel() {
    // Job() or SupervisorJob()
    private val viewModelJob = Job()

    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}