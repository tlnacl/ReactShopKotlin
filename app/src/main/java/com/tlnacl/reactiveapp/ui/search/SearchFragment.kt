package com.tlnacl.reactiveapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.databinding.FragmentSearchBinding
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import com.tlnacl.reactiveapp.viewBinding
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 21/06/17.
 */
class SearchFragment : Fragment(), SearchView {
    private var spanCount: Int = 2

    private lateinit var adapter: SearchAdapter

    @Inject
    lateinit var presenter: SearchPresenter

    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as AndroidApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        //? how about init state at attachview ??
        presenter.attachView(this)
        presenter.initState()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spanCount = resources.getInteger(R.integer.grid_span_size)
        adapter = SearchAdapter { onProductClicked(it) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(activity, spanCount)
        binding.recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                resources.getDimensionPixelSize(R.dimen.grid_spacing), true
            )
        )

        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
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

    private fun onProductClicked(product: Product) {
        val i = Intent(activity, ProductDetailsActivity::class.java)
        i.putExtra("productId", product.id)
        requireActivity().startActivity(i)
    }

    override fun render(searchViewState: SearchViewState) {
        Timber.d("render:$searchViewState")
        when (searchViewState) {
            is SearchViewState.SearchNotStartedYet -> renderSearchNotStarted()
            is SearchViewState.EmptyResult -> renderEmptyResult()
            is SearchViewState.Loading -> renderLoading()
            is SearchViewState.SearchResult -> renderResult(searchViewState.result)
            is SearchViewState.Error -> renderError()
        }
    }

    private fun renderResult(result: List<Product>) = binding.apply {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.isVisible = true
        loadingView.isVisible = false
        emptyView.isVisible = false
        errorView.root.isVisible = false
        adapter.submitList(result)
    }

    private fun renderSearchNotStarted() = binding.apply {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.isVisible = false
        loadingView.isVisible = false
        errorView.root.isVisible = false
        emptyView.isVisible = false
    }

    private fun renderLoading() = binding.apply {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.isVisible = false
        loadingView.isVisible = true
        errorView.root.isVisible = false
        emptyView.isVisible = false
    }

    private fun renderError() = binding.apply {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.isVisible = false
        loadingView.isVisible = false
        errorView.root.isVisible = true
        emptyView.isVisible = false
    }

    private fun renderEmptyResult() = binding.apply {
        TransitionManager.beginDelayedTransition(container)
        recyclerView.isVisible = false
        loadingView.isVisible = false
        errorView.root.isVisible = false
        emptyView.isVisible = true
    }
}