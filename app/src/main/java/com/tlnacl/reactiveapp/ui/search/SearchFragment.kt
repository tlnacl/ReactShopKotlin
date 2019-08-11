package com.tlnacl.reactiveapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_errorview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by tomt on 21/06/17.
 */
class SearchFragment : androidx.fragment.app.Fragment(), SearchView, ProductViewHolder.ProductClickedListener {
    override fun onProductClicked(product: Product) {
        val i = Intent(activity, ProductDetailsActivity::class.java)
        i.putExtra("productId", product.id)
        activity!!.startActivity(i)
    }

    private val binderJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + binderJob)
    private var spanCount: Int = 2

    private lateinit var adapter: SearchAdapter

    @Inject lateinit var presenter: SearchPrensenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as AndroidApplication).appComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binderJob.cancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        //? how about init state at attachview ??
        presenter.attachView(this)
        presenter.initState()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spanCount = resources.getInteger(R.integer.grid_span_size)
        adapter = SearchAdapter(activity!!, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, spanCount)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount,
                resources.getDimensionPixelSize(R.dimen.grid_spacing), true))

        scope.launch {
            presenter.onUiEvent(RxSearchView.queryTextChanges(searchView)
                    .skip(2) // Because after screen orientation changes query Text will be resubmitted again
                    .filter { queryString -> queryString.length > 3 || queryString.isEmpty() }
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .map { it.toString() })
        }

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