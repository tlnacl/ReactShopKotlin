package com.tlnacl.reactiveapp.ui.home

import android.support.v4.util.Pair
import com.jakewharton.rxrelay2.PublishRelay
import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
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
    private val changeRelay = PublishRelay.create<StateChange>()

    override fun attachView(mvpView: HomeView) {
        super.attachView(mvpView)
        startDisposables.add(changeRelay
                .scan(HomeViewState(loadingFirstPage = true), { homeViewState, stateChange ->
                    when (stateChange) {
                        is StateChange.FirstPageLoading -> HomeViewState(loadingFirstPage = true)
                        is StateChange.FirstPageLoaded -> HomeViewState(data = stateChange.data)
                        is StateChange.FirstPageError -> HomeViewState(firstPageError = stateChange.error)
                        is StateChange.ProductsOfCategoryLoading -> handleProductsOfCategoryLoading(homeViewState, stateChange)
                        is StateChange.ProductsOfCategoryLoaded -> handleProductsOfCategoryLoaded(homeViewState, stateChange)
                        is StateChange.ProductsOfCategoryError -> handleProductsOfCategoryError(homeViewState, stateChange)
                        else -> homeViewState//just handle 2 case now
                    }

                })
                .doOnNext { Timber.d("changeRelay:" + it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mvpView?.render(it) })
    }

    private fun handleProductsOfCategoryError(homeViewState: HomeViewState, stateChange: StateChange.ProductsOfCategoryError): HomeViewState {
        val found = findAdditionalItems(stateChange.categoryName, homeViewState.data)
        val foundItem = found.second
        val toInsert = AdditionalItemsLoadable(foundItem.moreItemsAvailableCount,
                foundItem.categoryName, false,
                stateChange.error)

        val data = ArrayList<FeedItem>(homeViewState.data.size)
        data.addAll(homeViewState.data)
        data[found.first] = toInsert
        return homeViewState.copy(data = data)
    }

    private fun handleProductsOfCategoryLoaded(homeViewState: HomeViewState, stateChange: StateChange.ProductsOfCategoryLoaded): HomeViewState {
        val found = findAdditionalItems(stateChange.categoryName, homeViewState.data)

        val data = ArrayList<FeedItem>(homeViewState.data.size + stateChange.data.size)
        data.addAll(homeViewState.data)

        // Search for the section header
        var sectionHeaderIndex = -1
        for (i in found.first downTo 0) {
            val item = homeViewState.data[i]
            if (item is SectionHeader && item.name
                    .equals(stateChange.categoryName)) {
                sectionHeaderIndex = i
                break
            }

            // Remove all items of that category. The new list of products will be added afterwards
            data.removeAt(i)
        }

        if (sectionHeaderIndex < 0) {
            throw RuntimeException("Couldn't find the section header for category " + stateChange.categoryName)
        }

        data.addAll(sectionHeaderIndex + 1, stateChange.data)
        return homeViewState.copy(data = data)
    }

    private fun handleProductsOfCategoryLoading(homeViewState: HomeViewState, stateChange: StateChange.ProductsOfCategoryLoading): HomeViewState {
        val found = findAdditionalItems(stateChange.categoryName, homeViewState.data)
        val foundItem = found.second
        val toInsert = AdditionalItemsLoadable(foundItem.moreItemsAvailableCount,
                foundItem.categoryName, true, null)

        val data = ArrayList<FeedItem>(homeViewState.data.size)
        data.addAll(homeViewState.data)
        data[found.first] = toInsert
        return homeViewState.copy(data = data)
    }

    private fun loadAllProductsFromCategory(categoryName: String): Observable<StateChange> {
        return feedLoader.loadProductsOfCategory(categoryName)
                .map<StateChange> { StateChange.ProductsOfCategoryLoaded(categoryName, it) }
                .onErrorReturn { StateChange.ProductsOfCategoryError(categoryName, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(StateChange.ProductsOfCategoryLoading(categoryName))
    }

    fun handleUiEvent(homeUiEventObservable: Observable<HomeUiEvent>) {
        homeUiEventObservable.flatMap { homeUiEvent ->
            when (homeUiEvent) {
                is HomeUiEvent.LoadFirstPage -> loadFirstPage()
                is HomeUiEvent.LoadAllProductsFromCategory -> loadAllProductsFromCategory(homeUiEvent.categoryName)
                else -> loadFirstPage()
            }
        }.subscribe(changeRelay)
    }

    private fun loadFirstPage(): Observable<StateChange> {
        Timber.d("loadFirstPage")
        return feedLoader.loadFirstPage()
                .doOnNext { Timber.d("feedItems:" + it) }
                .map<StateChange> { StateChange.FirstPageLoaded(it) }
                .onErrorReturn { StateChange.FirstPageError(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(StateChange.FirstPageLoading)


    }

    /**
     * find the [AdditionalItemsLoadable] for the given category name

     * @param categoryName The name of the category
     * *
     * @param items the list of feeditems
     */
    private fun findAdditionalItems(categoryName: String,
                                    items: List<FeedItem>): Pair<Int, AdditionalItemsLoadable> {
        val size = items.size
        for (i in 0..size - 1) {
            val item = items[i]
            if (item is AdditionalItemsLoadable && item.categoryName.equals(categoryName)) {
                return Pair.create(i, item)
            }
        }

        throw RuntimeException("No "
                + AdditionalItemsLoadable::class.java.simpleName
                + " has been found for category = "
                + categoryName)
    }


    //each feedLoader return Result like LoadFirstPageResult which can be used in scan to change HomeViewState
//    private fun loadFirsPageTransformer() {
//        ObservableTransformer<HomeUiEvent.LoadFirstPage, LoadFirstPageResult> loadFirstPage =
//            actions -> actions.flatMap()
//    }
//    fun mergeChanges(homeUiEvent: HomeUiEvent){
//        Observable.merge { homeUiEvent is HomeUiEvent.LoadFirstPage }
//    }

    //TODO can be one more layer as HomeUiEvent to Action
//    ObservableTransformer<HomeUiEvent,HomeViewState> loadFirstPage =
//        actions -> actions
}