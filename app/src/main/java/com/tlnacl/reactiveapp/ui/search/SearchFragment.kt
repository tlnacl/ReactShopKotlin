package com.tlnacl.reactiveapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_errorview.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 21/06/17.
 */
class SearchFragment : Fragment(), SearchView, ProductViewHolder.ProductClickedListener {
    override fun onProductClicked(product: Product) {
        val i = Intent(activity, ProductDetailsActivity::class.java)
        i.putExtra("productId", product.id)
        activity!!.startActivity(i)
    }

    private var spanCount: Int = 2

    private lateinit var adapter: SearchAdapter

    @Inject lateinit var presenter: SearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as AndroidApplication).appComponent.inject(this)
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
        recyclerView.layoutManager = GridLayoutManager(activity, spanCount)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount,
                resources.getDimensionPixelSize(R.dimen.grid_spacing), true))

        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(queryString: String?): Boolean {
                presenter.onUiEvent(queryString)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })

        presenter.onUiEvent("")
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