package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.ui.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 27/06/17.
 */
class HomePresenter @Inject constructor(val feedLoader: HomeFeedLoader) : BasePresenter<HomeView>() {
    private var startDisposables = CompositeDisposable()


    fun handleUiEvent(homeUiEvent: HomeUiEvent){
        when(homeUiEvent){
            is HomeUiEvent.LoadFirstPage -> loadFirstPage()
        }
    }

    private fun loadFirstPage() {
        feedLoader.loadFirstPage()
                .doOnNext { Timber.d("feedItems:" + it) }
                .map { feedItems -> HomeViewState(data = feedItems)}
                .onErrorReturn { HomeViewState(firstPageError = it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith (HomeViewState(loadingFirstPage = true))
                .subscribe { mvpView?.render(it) }

    }

    //TODO can be one more layer as HomeUiEvent to Action
//    ObservableTransformer<HomeUiEvent,HomeViewState> loadFirstPage =
//        actions -> actions
}