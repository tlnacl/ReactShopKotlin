package com.tlnacl.reactiveapp.ui.search

import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by tomt on 21/06/17.
 */
class SearchFragment : Fragment(), SearchView, ProductViewHolder.ProductClickedListener {
    override fun onProductClicked(product: Product) {
//        ProductDetailsActivity.start(activity, product)
    }

    @BindView(R.id.searchView) lateinit var searchView: android.widget.SearchView
    @BindView(R.id.container) lateinit var container: ViewGroup
    @BindView(R.id.loadingView) lateinit var loadingView: View
    @BindView(R.id.errorView) lateinit var errorView: TextView
    @BindView(R.id.recyclerView) lateinit var recyclerView: RecyclerView
    @BindView(R.id.emptyView) lateinit var emptyView: View
    var spanCount: Int = 2

    private lateinit var adapter: SearchAdapter

    @Inject lateinit var presenter: SearchPrensenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as AndroidApplication).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_search, container, false)
        ButterKnife.bind(this, view!!)
        //? how about init state at attachview ??
        presenter.attachView(this)
        presenter.initState()
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spanCount = resources.getInteger(R.integer.grid_span_size)
        adapter = SearchAdapter(activity, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(activity, spanCount)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount,
                resources.getDimensionPixelSize(R.dimen.grid_spacing), true))

        presenter.handleUiEvent(RxSearchView.queryTextChanges(searchView)
                .skip(2) // Because after screen orientation changes query Text will be resubmitted again
                .filter { queryString -> queryString.length > 3 || queryString.isEmpty() }
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .map { it.toString() })

    }

    override fun render(searchViewState: SearchViewState) {
        Timber.d("render:" + searchViewState)
        when (searchViewState) {
            is SearchViewState.SearchNotStartedYet -> renderSearchNotStarted()
            is SearchViewState.EmptyResult -> renderEmptyResult()
            is SearchViewState.Loading -> renderLoading()
            is SearchViewState.SearchResult -> renderResult(searchViewState.result)
            is SearchViewState.Error -> renderError()
        }
    }

    private fun renderResult(result: List<Product>) {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        emptyView.visibility = View.GONE
        errorView.visibility = View.GONE
        adapter.setProducts(result)
        adapter.notifyDataSetChanged()
    }

    private fun renderSearchNotStarted() {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.visibility = View.GONE
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    private fun renderLoading() {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    private fun renderError() {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.visibility = View.GONE
        loadingView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    private fun renderEmptyResult() {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.visibility = View.GONE
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }

}