package com.tlnacl.reactiveapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tlnacl.reactiveapp.*
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.databinding.FragmentHomeBinding
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.widgets.GridSpacingItemDecoration
import com.tlnacl.reactiveapp.dataflow.data.ViewState
import com.tlnacl.reactiveapp.dataflow.onEvents
import com.tlnacl.reactiveapp.dataflow.onStates
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tomt on 27/06/17.
 */
class HomeFragment : Fragment() {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: HomeViewModel

    var spanCount: Int = 2
    private lateinit var adapter: HomeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("HomeFragment OnCreate")
        (requireActivity().application as AndroidApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        viewModel = provideViewModel(viewModelFactory)
        spanCount = resources.getInteger(R.integer.grid_span_size)
        var layoutManager = GridLayoutManager(activity, spanCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                if (viewType == HomeAdapter.VIEW_TYPE_LOADING_MORE_NEXT_PAGE || viewType == HomeAdapter.VIEW_TYPE_SECTION_HEADER) {
                    return spanCount
                }
                return 1
            }
        }
        adapter = HomeAdapter({ onProductClicked(it) }, { loadItemsForCategory(it) })
        binding.apply {
            recyclerView.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount,
                    resources.getDimensionPixelSize(R.dimen.grid_spacing), true
                )
            )

            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!adapter.isLoadingNextPage() && newState == RecyclerView.SCROLL_STATE_IDLE
                        && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        viewModel.loadNextPage()
                    }
                }
            })
            swipeRefreshLayout.setOnRefreshListener { viewModel.pullToRefresh() }
        }
        onStates(viewModel) { viewState ->
            when (viewState) {
                is ViewState.Loading -> renderFirstPageLoading()
                is ViewState.Failed -> renderFirstPageError()
                is HomeViewState -> renderShowData(viewState)
            }
        }
        onEvents(viewModel) { event ->
            when (event) {
                is HomeViewEvent.PullToRefreshSuccess -> binding.recyclerView.smoothScrollToPosition(
                    0
                )
                is HomeViewEvent.Error -> Snackbar.make(
                    view,
                    R.string.error_unknown,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        viewModel.loadFirstPage()
    }

    private fun onProductClicked(product: Product) {
        val i = Intent(activity, ProductDetailsActivity::class.java)
        i.putExtra("productId", product.id)
        requireActivity().startActivity(i)
    }

    private fun loadItemsForCategory(category: String) {
        viewModel.loadAllProductsFromCategory(category)
    }

    private fun renderShowData(state: HomeViewState) {
        binding.apply {
            loadingView.isVisible = false
            errorView.root.isVisible = false
            swipeRefreshLayout.isVisible = true
            val changed = adapter.setLoadingNextPage(state.loadingNextPage)
            if (changed && state.loadingNextPage) {
                // scroll to the end of the list so that the user sees the load more progress bar
                recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
            adapter.submitList(state.data)

            swipeRefreshLayout.isRefreshing = state.loadingPullToRefresh
        }
    }

    private fun renderFirstPageLoading() = binding.apply {
        loadingView.isVisible = true
        errorView.root.isVisible = false
        swipeRefreshLayout.isVisible = false
    }

    private fun renderFirstPageError() = binding.apply {
        loadingView.isVisible = false
        swipeRefreshLayout.isVisible = false
        errorView.root.isVisible = true
    }
}