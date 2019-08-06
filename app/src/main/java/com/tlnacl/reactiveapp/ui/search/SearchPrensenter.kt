package com.tlnacl.reactiveapp.ui.search

import com.tlnacl.reactiveapp.businesslogic.searchengine.SearchEngine
import com.tlnacl.reactiveapp.ui.BasePresenter
import io.reactivex.Observable
import kotlinx.coroutines.rx2.collect
import javax.inject.Inject

/**
 * Created by tomt on 23/06/17.
 */
class SearchPrensenter
@Inject constructor(val searchEngine: SearchEngine) : BasePresenter<SearchView>() {
    fun initState() {
        mvpView?.render(SearchViewState.SearchNotStartedYet)
//        startDisposables.add(changeRequestRelay
////                .observeOn(Schedulers.io())
//                .subscribe(this::search))
    }

    suspend fun onUiEvent(query: Observable<String>) {
        query.collect {
            // TODO debounce
            if (it.isEmpty()) mvpView?.render(SearchViewState.SearchNotStartedYet)
            else {
                try {
                    mvpView?.render(SearchViewState.Loading)
                    val products = searchEngine.searchFor(it)
                    if (products.isEmpty()) mvpView?.render(SearchViewState.EmptyResult)
                    else mvpView?.render(SearchViewState.SearchResult(products))
                } catch (e: Exception) {
                    mvpView?.render(SearchViewState.Error(e))
                }

            }
        }
    }
}