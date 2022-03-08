package com.tlnacl.reactiveapp.ui.home

import androidx.core.util.Pair
import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import com.tlnacl.reactiveapp.dataflow.DataFlowBaseViewModel
import com.tlnacl.reactiveapp.dataflow.actionOn
import com.tlnacl.reactiveapp.dataflow.data.ViewState
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val feedLoader: HomeFeedLoader) : DataFlowBaseViewModel(defaultState = ViewState.Loading) {
    fun loadFirstPage() = action(
            onAction = {
                setState(HomeViewState(data = feedLoader.loadFirstPage()))
            },
            onError = { error, _ -> setState(ViewState.Failed) }
    )

    fun loadAllProductsFromCategory(categoryName: String) = actionOn<HomeViewState>(
            onAction = { currentState ->
                setState { handleProductsOfCategoryLoading(currentState, categoryName) }
                val products = feedLoader.loadProductsOfCategory(categoryName)
                setState { handleProductsOfCategoryLoaded(currentState, categoryName, products) }
            },
            onError = { error, currentState ->
                when(currentState) {
                    is HomeViewState -> {
                        val found = findAdditionalItems(categoryName, currentState.data)
                        val foundItem = found.second
                        val toInsert = AdditionalItemsLoadable(foundItem!!.moreItemsAvailableCount, foundItem.groupName, false, error)

                        val data = ArrayList<FeedItem>(currentState.data.size)
                        data.addAll(currentState.data)
                        data[found.first!!] = toInsert
                        setState(currentState.copy(data = data))
                    }
                    else -> sendEvent(HomeViewEvent.Error(error))
                }
             })

    fun loadNextPage() = actionOn<HomeViewState>(
            onAction = { currentState ->
                setState { currentState.copy(loadingNextPage = true) }
                val items = feedLoader.loadNextPage()
                setState { handleNextPageLoaded(currentState, items) }
            },
            onError = { error, _ -> sendEvent(HomeViewEvent.Error(error)) })

    fun pullToRefresh() = actionOn<HomeViewState>(
            onAction = { currentState ->
                setState { currentState.copy(loadingPullToRefresh = true) }
                val items = feedLoader.loadNewestPage()
                setState { handlePullToRefreshLoaded(currentState, items) }
                sendEvent(HomeViewEvent.PullToRefreshSuccess)
            },
            onError = { error, _ -> sendEvent(HomeViewEvent.Error(error)) })

    private fun handleNextPageLoaded(homeViewState: HomeViewState, items: List<FeedItem>): HomeViewState {
        val data = ArrayList<FeedItem>()
        data.addAll(homeViewState.data)
        data.addAll(items)
        return homeViewState.copy(data = data, loadingNextPage = false)
    }

    private fun handlePullToRefreshLoaded(homeViewState: HomeViewState, items: List<FeedItem>): HomeViewState {
        val data = ArrayList<FeedItem>()
        data.addAll(items)
        data.addAll(homeViewState.data)
        return homeViewState.copy(data = data, loadingPullToRefresh = false)
    }

    private fun handleProductsOfCategoryLoaded(homeViewState: HomeViewState, categoryName: String, items: List<FeedItem>): HomeViewState {
        val found = findAdditionalItems(categoryName, homeViewState.data)

        val data = ArrayList<FeedItem>(homeViewState.data.size + items.size)
        data.addAll(homeViewState.data)

        // Search for the section header
        var sectionHeaderIndex = -1
        for (i in found.first!! downTo 0) {
            val item = homeViewState.data[i]
            if (item is SectionHeader && item.name == categoryName) {
                sectionHeaderIndex = i
                break
            }

            // Remove all items of that category. The new list of products will be added afterwards
            data.removeAt(i)
        }

        if (sectionHeaderIndex < 0) {
            throw RuntimeException("Couldn't find the section header for category $categoryName")
        }

        data.addAll(sectionHeaderIndex + 1, items)
        return homeViewState.copy(data = data)
    }

    private fun handleProductsOfCategoryLoading(homeViewState: HomeViewState, categoryName: String): HomeViewState {
        val found = findAdditionalItems(categoryName, homeViewState.data)
        val foundItem = found.second
        val toInsert = AdditionalItemsLoadable(foundItem!!.moreItemsAvailableCount,
                foundItem.groupName, true, null)

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
            if (item is AdditionalItemsLoadable && item.groupName == categoryName) {
                return Pair.create(i, item)
            }
        }

        throw RuntimeException("No "
                + AdditionalItemsLoadable::class.java.simpleName
                + " has been found for category = "
                + categoryName)
    }
}