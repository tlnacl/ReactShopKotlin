package com.tlnacl.reactiveapp

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * For Fragments, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */

inline fun <reified VM : ViewModel> Fragment.provideViewModel(provider: ViewModelProvider.Factory): VM {
    return ViewModelProvider(this, provider).get(VM::class.java)
}