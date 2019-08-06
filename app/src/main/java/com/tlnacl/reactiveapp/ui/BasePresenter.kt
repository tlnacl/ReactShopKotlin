package com.tlnacl.reactiveapp.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by tomt on 23/06/17.
 */
abstract class BasePresenter<T : MvpView> : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var mvpView: T? = null

    open fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    fun detachView() {
        job.cancel()
        mvpView = null
    }

    val isViewAttached: Boolean
        get() = mvpView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    class MvpViewNotAttachedException : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")
}