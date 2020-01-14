package com.tlnacl.reactiveapp.ui.search

import com.tlnacl.reactiveapp.businesslogic.searchengine.SearchEngine
import com.tlnacl.reactiveapp.ui.BasePresenter
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by tomt on 23/06/17.
 */
class SearchPresenter
@Inject constructor(val searchEngine: SearchEngine) : BasePresenter<SearchView>() {
    private val queryChannel = BroadcastChannel<String?>(CONFLATED)
    fun initState() {
        presenterScope.launch {
            queryChannel
                    .asFlow()
                    .filter { queryString -> queryString.isNullOrEmpty() || queryString.length > 3 }
                    .debounce(500)
                    .onEach {// mapLatest
                        if (it.isNullOrEmpty()) mvpView?.render(SearchViewState.SearchNotStartedYet)
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

    fun onUiEvent(query: String?) {
        presenterScope.launch {
            queryChannel.send(query)
        }

    }
}