package com.tlnacl.reactiveapp.ui.home

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 27/06/17.
 */
class HomeFragment : Fragment(), HomeView, ProductViewHolder.ProductClickedListener {
    @Inject lateinit var presenter: HomePresenter
    @BindView(R.id.swipeRefreshLayout) lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.recyclerView) lateinit var recyclerView: RecyclerView
    @BindView(R.id.loadingView) lateinit var loadingView: View
    @BindView(R.id.errorView) lateinit var errorView: TextView

    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as AndroidApplication).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_home, container, false)
        ButterKnife.bind(this, view!!)
        //? how about init state at attachview ??
        presenter.attachView(this)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var layoutManager = GridLayoutManager(activity, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                if (viewType == HomeAdapter.VIEW_TYPE_LOADING_MORE_NEXT_PAGE || viewType == HomeAdapter.VIEW_TYPE_SECTION_HEADER) {
                    return 2
                }
                return 1
            }
        }
        adapter = HomeAdapter(activity, this)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2,
                resources.getDimensionPixelSize(R.dimen.grid_spacing), true))

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        presenter.handleUiEvent(Observable.just(HomeUiEvent.LoadFirstPage))
        presenter.handleUiEvent(adapter.loadMoreItemsOfCategoryObservable().map { HomeUiEvent.LoadAllProductsFromCategory(it) })
        presenter.handleUiEvent(RxRecyclerView.scrollStateChanges(recyclerView)
                .filter { !adapter.isLoadingNextPage() }
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .filter { layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItems().size - 1 }
                .map { HomeUiEvent.LoadNextPage })
        presenter.handleUiEvent(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).map { HomeUiEvent.PullToRefresh })
    }

    override fun onProductClicked(product: Product) {
//        ProductDetailsActivity.start(activity, product)
    }

    override fun render(viewState: HomeViewState) {
        Timber.i("render %s", viewState)
        if (!viewState.loadingFirstPage && viewState.firstPageError == null) {
            renderShowData(viewState)
        } else if (viewState.loadingFirstPage) {
            renderFirstPageLoading()
        } else if (viewState.firstPageError != null) {
            renderFirstPageError()
        } else {
            throw IllegalStateException("Unknown view state " + viewState)
        }
    }

    private fun renderShowData(state: HomeViewState) {
        TransitionManager.beginDelayedTransition(view as ViewGroup)
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        swipeRefreshLayout.visibility = View.VISIBLE
        val changed = adapter.setLoadingNextPage(state.loadingNextPage)
        if (changed && state.loadingNextPage) {
            // scroll to the end of the list so that the user sees the load more progress bar
            recyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        adapter.setItems(state.data)

        val pullToRefreshFinished = swipeRefreshLayout.isRefreshing
                && !state.loadingPullToRefresh
                && state.pullToRefreshError == null
        if (pullToRefreshFinished) {
            // Swipe to refresh finished successfully so scroll to the top of the list (to see inserted items)
            recyclerView.smoothScrollToPosition(0)
        }

        swipeRefreshLayout.isRefreshing = state.loadingPullToRefresh

        if (state.nextPageError != null) {
            Snackbar.make(view!!, R.string.error_unknown, Snackbar.LENGTH_LONG)
                    .show() // TODO callback
        }

        if (state.pullToRefreshError != null) {
            Snackbar.make(view!!, R.string.error_unknown, Snackbar.LENGTH_LONG)
                    .show() // TODO callback
        }
    }

    private fun renderFirstPageLoading() {
        TransitionManager.beginDelayedTransition(view as ViewGroup)
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
    }

    private fun renderFirstPageError() {
        TransitionManager.beginDelayedTransition(view as ViewGroup)
        loadingView.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }

}