package com.tlnacl.reactiveapp

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A kotlin delegated property that ties the value to the lifecycle of a fragments view.
 * When the view is attached and destroyed the value is created and destroyed with it.
 */
class FragmentAutoClearingValue<T>(
    val fragment: Fragment,
    val viewFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                val viewLifecycleOwnerLiveDataObserver =
                    Observer<LifecycleOwner?> {
                        if (it == null) {
                            // View lifecycle null signifies a `onDestroyView()` call at the fragment level
                            // the `lifecycle.onDestroy` itself can be missed in certain quirky conditions
                            binding = null
                        }
                    }

                override fun onCreate(owner: LifecycleOwner) {
                    fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
                }
            }
        )
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get auto clearing values when Fragment views are destroyed.")
        }

        return viewFactory(thisRef.requireView()).also { this.binding = it }
    }
}
