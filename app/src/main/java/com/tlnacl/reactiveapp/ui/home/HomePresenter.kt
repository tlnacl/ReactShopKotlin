package com.tlnacl.reactiveapp.ui.home

import androidx.core.util.Pair
import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import com.tlnacl.reactiveapp.ui.BasePresenter
import io.reactivex.Observable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.collect
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 27/06/17.
 */
class HomePresenter @Inject constructor(val feedLoader: HomeFeedLoader) : BasePresenter<HomeView>() {
    //    private val changeRelay = PublishRelay.create<StateChange>()
    private val changeChannel = Channel<StateChange>()

    private val _events = Channel<HomeUiEvent>(RENDEZVOUS)
    public val events: SendChannel<HomeUiEvent> get() = _events
//    val stateLiveData: MutableLiveData<StateChange> by lazy {
//        MutableLiveData<StateChange>()
//    }

    init {
        Timber.i("init HomePresenter")
        launch { processStateChange() }
    }

    private suspend fun processStateChange() {
        // TODO currentState as var is not feeling good
        var currentState = HomeViewState(loadingFirstPage = true)
        for (stateChange in changeChannel) {
            val homeViewState = when (stateChange) {
                is StateChange.FirstPageLoading -> HomeViewState(loadingFirstPage = true)
                is StateChange.FirstPageLoaded -> HomeViewState(data = stateChange.data)
                is StateChange.FirstPageError -> HomeViewState(firstPageError = stateChange.error)
                is StateChange.ProductsOfCategoryLoading -> handleProductsOfCategoryLoading(currentState, stateChange)
                is StateChange.ProductsOfCategoryLoaded -> handleProductsOfCategoryLoaded(currentState, stateChange)
                is StateChange.ProductsOfCategoryError -> handleProductsOfCategoryError(currentState, stateChange)
                is StateChange.NextPageLoading -> currentState.copy(loadingNextPage = true, nextPageError = null)
                is StateChange.NextPageLoaded -> handleNextPageLoaded(currentState, stateChange)
                is StateChange.NextPageError -> currentState.copy(loadingNextPage = false, nextPageError = stateChange.error)
                is StateChange.PullToRefreshLoading -> currentState.copy(loadingPullToRefresh = true, pullToRefreshError = null)
                is StateChange.PullToRefreshLoaded -> handlePullToRefreshLoaded(currentState, stateChange)
                is StateChange.PullToRefreshError -> currentState.copy(loadingPullToRefresh = false, pullToRefreshError = stateChange.error)
            }
            if (currentState != homeViewState) {
                currentState = homeViewState
                Timber.d("render:$currentState")
                mvpView?.render(currentState)
            }
        }
    }

    override fun attachView(mvpView: HomeView) {
        super.attachView(mvpView)
    }

    // TODO duplicate with handleUiEvent
    fun onUiEvent(event: HomeUiEvent) {
        launch {
            Timber.i("homeUiEvent:$event")
            when (event) {
                is HomeUiEvent.LoadFirstPage -> loadFirstPage()
                is HomeUiEvent.LoadAllProductsFromCategory -> loadAllProductsFromCategory(event.categoryName)
                is HomeUiEvent.LoadNextPage -> loadNextPage()
                is HomeUiEvent.PullToRefresh -> pullToRefresh()
                else -> pullToRefresh()
            }
        }
    }

//    fun CoroutineScope.onUiEvents(events: ReceiveChannel<HomeUiEvent>) =
//            launch {
//                events.consumeEach { event ->
//                    Timber.i("homeUiEvent:$event")
//                    when (event) {
//                        is HomeUiEvent.LoadFirstPage -> loadFirstPage()
//                        is HomeUiEvent.LoadAllProductsFromCategory -> loadAllProductsFromCategory(event.categoryName)
//                        is HomeUiEvent.LoadNextPage -> loadNextPage()
//                        is HomeUiEvent.PullToRefresh -> pullToRefresh()
//                        else -> pullToRefresh()
//                    }
//                }
//            }

    suspend fun handleUiEvent(homeUiEventObservable: Observable<HomeUiEvent>) {
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

    private suspend fun loadFirstPage() {
        Timber.d("loadFirstPage")
        try {
            changeChannel.send(StateChange.FirstPageLoading)
            changeChannel.send(StateChange.FirstPageLoaded(feedLoader.loadFirstPage()))
        } catch (e: Exception) {
            changeChannel.send(StateChange.FirstPageError(e))
        }
    }

    private suspend fun loadAllProductsFromCategory(categoryName: String) {
        Timber.d("loadAllProductsFromCategory")
        try {
            changeChannel.send(StateChange.ProductsOfCategoryLoading(categoryName))
            val productsOfCategory = feedLoader.loadProductsOfCategory(categoryName)
            changeChannel.send(StateChange.ProductsOfCategoryLoaded(categoryName, productsOfCategory))
        } catch (e: Exception) {
            changeChannel.send(StateChange.ProductsOfCategoryError(categoryName, e))
        }
    }

    private suspend fun loadNextPage() {
        try {
            changeChannel.send(StateChange.NextPageLoading)
            changeChannel.send(StateChange.NextPageLoaded(feedLoader.loadNextPage()))
        } catch (e: Exception) {
            changeChannel.send(StateChange.NextPageError(e))
        }
    }

    private suspend fun pullToRefresh() {
        try {
            changeChannel.send(StateChange.PullToRefreshLoading)
            changeChannel.send(StateChange.NextPageLoaded(feedLoader.loadNewestPage()))
        } catch (e: Exception) {
            changeChannel.send(StateChange.PullToRefreshError(e))
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