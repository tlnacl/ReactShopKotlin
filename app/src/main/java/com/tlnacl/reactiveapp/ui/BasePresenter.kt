package com.tlnacl.reactiveapp.ui

/**
 * Created by tomt on 23/06/17.
 */
abstract class BasePresenter<T : MvpView> {

    var mvpView: T? = null

    fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    fun detachView() {
        mvpView = null
    }

    val isViewAttached: Boolean
        get() = mvpView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    class MvpViewNotAttachedException : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")
}