package com.tlnacl.reactiveapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 27/06/17.
 */
class HomeFragment : Fragment(), HomeView, ProductViewHolder.ProductClickedListener {
    // TODO do we need it in ui
    private val binderJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + binderJob)

    @Inject lateinit var presenter: HomePresenter
    @BindView(R.id.swipeRefreshLayout) lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    @BindView(R.id.recyclerView) lateinit var recyclerView: RecyclerView
    @BindView(R.id.loadingView) lateinit var loadingView: View
    @BindView(R.id.errorView) lateinit var errorView: TextView
    var spanCount: Int = 2

    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("HomeFragment OnCreate")
        (activity!!.application as AndroidApplication).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        Timber.d("HomeFragment onCreateView")
        ButterKnife.bind(this, view!!)
        presenter.attachView(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spanCount = resources.getInteger(R.integer.grid_span_size)
        var layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, spanCount)
        layoutManager.spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                if (viewType == HomeAdapter.VIEW_TYPE_LOADING_MORE_NEXT_PAGE || viewType == HomeAdapter.VIEW_TYPE_SECTION_HEADER) {
                    return spanCount
                }
                return 1
            }
        }
        adapter = HomeAdapter(activity!!, this)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount,
                resources.getDimensionPixelSize(R.dimen.grid_spacing), true))

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        scope.launch {
            presenter.handleUiEvent(Observable.just(HomeUiEvent.LoadFirstPage))
            presenter.handleUiEvent(adapter.loadMoreItemsOfCategoryObservable().map { HomeUiEvent.LoadAllProductsFromCategory(it) })
            presenter.handleUiEvent(RxRecyclerView.scrollStateChanges(recyclerView)
                    .filter { !adapter.isLoadingNextPage() }
                    .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                    .filter { layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItems().size - 1 }
                    .map { HomeUiEvent.LoadNextPage })
            presenter.handleUiEvent(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).map { HomeUiEvent.PullToRefresh })
        }
    }

    override fun onProductClicked(product: Product) {
        val i = Intent(activity, ProductDetailsActivity::class.java)
        i.putExtra("productId", product.id)
        activity!!.startActivity(i)
    }

    override fun render(homeViewState: HomeViewState) {
        Timber.i("render %s", homeViewState)
        if (!homeViewState.loadingFirstPage && homeViewState.firstPageError == null) {
            renderShowData(homeViewState)
        } else if (homeViewState.loadingFirstPage) {
            renderFirstPageLoading()
        } else if (homeViewState.firstPageError != null) {
            renderFirstPageError()
        } else {
            throw IllegalStateException("Unknown view state " + homeViewState)
        }
    }

    private fun renderShowData(state: HomeViewState) {
//        TransitionManager.beginDelayedTransition(view as ViewGroup)
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
//        TransitionManager.beginDelayedTransition(view as ViewGroup)
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
    }

    private fun renderFirstPageError() {
//        TransitionManager.beginDelayedTransition(view as ViewGroup)
        loadingView.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binderJob.cancel()

//        if (!isChangingConfigurations) {
//            presentation.stop()
//        }
    }

}