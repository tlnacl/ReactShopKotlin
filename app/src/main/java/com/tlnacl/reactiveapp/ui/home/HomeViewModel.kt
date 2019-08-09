package com.tlnacl.reactiveapp.ui.home

import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import com.tlnacl.reactiveapp.ui.BaseViewModel
import io.reactivex.Observable
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.collect
import timber.log.Timber
import javax.inject.Inject

/**
 *
 */
class HomeViewModel @Inject constructor(val feedLoader: HomeFeedLoader) : BaseViewModel() {
    private val homeLiveData = MutableLiveData<HomeViewState>()

    private var currentViewState = HomeViewState(loadingFirstPage = true)
        set(value) {
            field = value
            homeLiveData.value = value
        }

    fun getHomeLiveData(): LiveData<HomeViewState> {
        return homeLiveData
    }

    private fun processStateChange(stateChange: StateChange) {
        val homeViewState = when (stateChange) {
            is StateChange.FirstPageLoading -> HomeViewState(loadingFirstPage = true)
            is StateChange.FirstPageLoaded -> HomeViewState(data = stateChange.data)
            is StateChange.FirstPageError -> HomeViewState(firstPageError = stateChange.error)
            is StateChange.ProductsOfCategoryLoading -> handleProductsOfCategoryLoading(currentViewState, stateChange)
            is StateChange.ProductsOfCategoryLoaded -> handleProductsOfCategoryLoaded(currentViewState, stateChange)
            is StateChange.ProductsOfCategoryError -> handleProductsOfCategoryError(currentViewState, stateChange)
            is StateChange.NextPageLoading -> currentViewState.copy(loadingNextPage = true, nextPageError = null)
            is StateChange.NextPageLoaded -> handleNextPageLoaded(currentViewState, stateChange)
            is StateChange.NextPageError -> currentViewState.copy(loadingNextPage = false, nextPageError = stateChange.error)
            is StateChange.PullToRefreshLoading -> currentViewState.copy(loadingPullToRefresh = true, pullToRefreshError = null)
            is StateChange.PullToRefreshLoaded -> handlePullToRefreshLoaded(currentViewState, stateChange)
            is StateChange.PullToRefreshError -> currentViewState.copy(loadingPullToRefresh = false, pullToRefreshError = stateChange.error)
        }
        if (currentViewState != homeViewState) {
            currentViewState = homeViewState
            Timber.d("render:$currentViewState")
        }
    }

    fun handleUiEvent(homeUiEventObservable: Observable<HomeUiEvent>) {
        uiScope.launch {
            homeUiEventObservable.collect { homeUiEvent ->
                Timber.i("homeUiEvent:$homeUiEvent")
                when (homeUiEvent) {
                    is HomeUiEvent.LoadFirstPage -> loadFirstPage()
                    is HomeUiEvent.LoadAllProductsFromCategory -> loadAllProductsFromCategory(homeUiEvent.categoryName)
                    is HomeUiEvent.LoadNextPage -> loadNextPage()
                    is HomeUiEvent.PullToRefresh -> pullToRefresh()
                    else -> pullToRefresh()
                }
            }
        }
    }

    private suspend fun loadFirstPage() {
        Timber.d("loadFirstPage")
        try {
            processStateChange(StateChange.FirstPageLoading)
            processStateChange(StateChange.FirstPageLoaded(feedLoader.loadFirstPage()))
        } catch (e: Exception) {
            processStateChange(StateChange.FirstPageError(e))
        }
    }

    private suspend fun loadAllProductsFromCategory(categoryName: String) {
        Timber.d("loadAllProductsFromCategory")
        try {
            processStateChange(StateChange.ProductsOfCategoryLoading(categoryName))
            val productsOfCategory = feedLoader.loadProductsOfCategory(categoryName)
            processStateChange(StateChange.ProductsOfCategoryLoaded(categoryName, productsOfCategory))
        } catch (e: Exception) {
            processStateChange(StateChange.ProductsOfCategoryError(categoryName, e))
        }
    }

    private suspend fun loadNextPage() {
        try {
            processStateChange(StateChange.NextPageLoading)
            processStateChange(StateChange.NextPageLoaded(feedLoader.loadNextPage()))
        } catch (e: Exception) {
            processStateChange(StateChange.NextPageError(e))
        }
    }

    private suspend fun pullToRefresh() {
        try {
            processStateChange(StateChange.PullToRefreshLoading)
            processStateChange(StateChange.NextPageLoaded(feedLoader.loadNewestPage()))
        } catch (e: Exception) {
            processStateChange(StateChange.PullToRefreshError(e))
        }
    }

    private fun handleNextPageLoaded(homeViewState: HomeViewState, stateChange: StateChange.NextPageLoaded): HomeViewState {
        val data = ArrayList<FeedItem>()
        data.addAll(homeViewState.data)
        data.addAll(stateChange.data)
        return homeViewState.copy(data = data, loadingNextPage = false, nextPageError = null)
    }

    private fun handlePullToRefreshLoaded(homeViewState: HomeViewState, stateChange: StateChange.PullToRefreshLoaded): HomeViewState {
        val data = ArrayList<FeedItem>()
        data.addAll(stateChange.data)
        data.addAll(homeViewState.data)
        return homeViewState.copy(data = data, loadingPullToRefresh = false, pullToRefreshError = null)
    }

    private fun handleProductsOfCategoryError(homeViewState: HomeViewState, stateChange: StateChange.ProductsOfCategoryError): HomeViewState {
        val found = findAdditionalItems(stateChange.categoryName, homeViewState.data)
        val foundItem = found.second
        val toInsert = AdditionalItemsLoadable(foundItem!!.moreItemsAvailableCount,
                foundItem.categoryName, false,
                stateChange.error)

        val data = ArrayList<FeedItem>(homeViewState.data.size)
        data.addAll(homeViewState.data)
        data[found.first!!] = toInsert
        return homeViewState.copy(data = data)
    }

    private fun handleProductsOfCategoryLoaded(homeViewState: HomeViewState, stateChange: StateChange.ProductsOfCategoryLoaded): HomeViewState {
        val found = findAdditionalItems(stateChange.categoryName, homeViewState.data)

        val data = ArrayList<FeedItem>(homeViewState.data.size + stateChange.data.size)
        data.addAll(homeViewState.data)

        // Search for the section header
        var sectionHeaderIndex = -1
        for (i in found.first!! downTo 0) {
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
        val toInsert = AdditionalItemsLoadable(foundItem!!.moreItemsAvailableCount,
                foundItem.categoryName, true, null)

        val data = ArrayList<FeedItem>(homeViewState.data.size)
        data.addAll(homeViewState.data)
        data[found.first!!] = toInsert
        return homeViewState.copy(data = data)
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
        for (i in 0 until size) {
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
}