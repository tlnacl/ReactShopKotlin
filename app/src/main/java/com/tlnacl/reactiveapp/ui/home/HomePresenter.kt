package com.tlnacl.reactiveapp.ui.home

import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.ui.BasePresenter
import io.reactivex.Observable
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
    private val homeViewState = HomeViewState(loadingFirstPage = true)


    fun transferEvent(homeUiEvent: HomeUiEvent):Observable<HomeViewState>{
        when(homeUiEvent){
            is HomeUiEvent.LoadFirstPage -> return loadFirstPage()
            is HomeUiEvent.LoadAllProductsFromCategory -> return loadAllProductsFromCategory(homeUiEvent.categoryName)
            else -> { return Observable.just(HomeViewState())}
        }
    }

    private fun loadAllProductsFromCategory(categoryName: String): Observable<HomeViewState> {
        return feedLoader.loadProductsOfCategory(categoryName)
                .map { feedItems -> HomeViewState(data = feedItems) }
                .onErrorReturn { HomeViewState(firstPageError = it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith (HomeViewState(loadingFirstPage = true))
    }

    fun handleUiEvent(homeUiEvent: Observable<HomeUiEvent>){
        homeUiEvent.flatMap { transferEvent(it) }
                .subscribe { mvpView?.render(it) }
        }


    private fun loadFirstPage():Observable<HomeViewState> {
        return feedLoader.loadFirstPage()
                .doOnNext { Timber.d("feedItems:" + it) }
                .map { feedItems -> HomeViewState(data = feedItems)}
                .onErrorReturn { HomeViewState(firstPageError = it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith (HomeViewState(loadingFirstPage = true))

    }

    //TODO can be one more layer as HomeUiEvent to Action
//    ObservableTransformer<HomeUiEvent,HomeViewState> loadFirstPage =
//        actions -> actions
}