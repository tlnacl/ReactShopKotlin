package com.tlnacl.reactiveapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import coil.load
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.Constants
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.databinding.ActivityProductDetailBinding
import com.tlnacl.reactiveapp.dataflow.data.ViewState
import com.tlnacl.reactiveapp.dataflow.onStates
import com.tlnacl.reactiveapp.viewBinding
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by tlnacl on 11/07/17.
 */
class ProductDetailsActivity : AppCompatActivity() {
    val KEY_PRODUCT_ID = "productId"
    private val binding by viewBinding(ActivityProductDetailBinding::inflate)
    private var product: Product? = null
    private var isProductInshoppingCart = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        (application as AndroidApplication).appComponent.inject(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Timber.d("onCreate")
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductDetailsViewModel::class.java)

        onStates(viewModel) { state ->

            when (state) {
                is ViewState.Loading -> renderLoading()
                is ViewState.Failed -> renderError()
                is ProductDetailsViewState -> renderData(state)
            }
        }

        viewModel.getDetail(intent.getIntExtra(KEY_PRODUCT_ID, 0))
    }

    private fun renderError() = binding.apply {
        TransitionManager.beginDelayedTransition(rootView)
        errorView.root.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        detailsView.visibility = View.GONE
    }

    private fun renderData(state: ProductDetailsViewState) = binding.apply {
        TransitionManager.beginDelayedTransition(rootView)
        errorView.root.visibility = View.GONE
        loadingView.visibility = View.GONE
        detailsView.visibility = View.VISIBLE

        isProductInshoppingCart = state.data.isInShoppingCart
        product = state.data.product
        price.text = "Price: $" + String.format(Locale.US, "%.2f", product?.price)
        description.text = product?.description
        toolbar.title = product?.name
        collapsingToolbar.title = product?.name

        if (isProductInshoppingCart) {
            fab.setImageResource(R.drawable.ic_in_shopping_cart)
        } else {
            fab.setImageResource(R.drawable.ic_add_shopping_cart)
        }

        backdrop.load(Constants.BASE_IMAGE_URL + product?.image)

    }

    private fun renderLoading() = binding.apply {
        TransitionManager.beginDelayedTransition(rootView)
        errorView.root.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        detailsView.visibility = View.GONE
    }

}