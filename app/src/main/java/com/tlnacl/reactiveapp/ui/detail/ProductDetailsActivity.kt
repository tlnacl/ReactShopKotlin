package com.tlnacl.reactiveapp.ui.detail

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.Constants
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by tlnacl on 11/07/17.
 */
class ProductDetailsActivity : AppCompatActivity() ,ProductDetailsView{
    val KEY_PRODUCT_ID = "productId"
    private var product: Product? = null
    private var isProductInshoppingCart = false

    @Inject lateinit var presenter: ProductDetailsPresenter
    @BindView(R.id.errorView) lateinit var errorView: View
    @BindView(R.id.loadingView) lateinit var loadingView: View
    @BindView(R.id.detailsView) lateinit var detailsView: View
    @BindView(R.id.price) lateinit var price: TextView
    @BindView(R.id.description) lateinit var description: TextView
    @BindView(R.id.fab) lateinit var fab: FloatingActionButton
    @BindView(R.id.backdrop) lateinit var backdrop: ImageView
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.root) lateinit var rootView: ViewGroup
    @BindView(R.id.collapsingToolbar) lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        ButterKnife.bind(this)
        (application as AndroidApplication).appComponent.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        presenter.attachView(this)
        Timber.d("onCreate")
        presenter.initState()

        presenter.handleUiEvent(intent.getIntExtra(KEY_PRODUCT_ID,0))
//        fabClickObservable = RxView.clicks(fab).share().map { ignored -> true }
    }

    override fun render(productDetailsViewState: ProductDetailsViewState) {
        Timber.d("render " + productDetailsViewState)

        when(productDetailsViewState){
            is ProductDetailsViewState.Loading -> renderLoading()
            is ProductDetailsViewState.Data -> renderData(productDetailsViewState)
            is ProductDetailsViewState.Error -> renderError()
        }
    }

    private fun renderError() {
        TransitionManager.beginDelayedTransition(rootView)
        errorView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        detailsView.visibility = View.GONE
    }

    private fun renderData(state: ProductDetailsViewState.Data) {
        TransitionManager.beginDelayedTransition(rootView)
        errorView.visibility = View.GONE
        loadingView.visibility = View.GONE
        detailsView.visibility = View.VISIBLE

        isProductInshoppingCart = state.data.isInShoppingCart
        product = state.data.product
        price.text = "Price: $" + String.format(Locale.US, "%.2f", product?.price)
        description.text = product?.getDescription()
        toolbar.title = product?.getName()
        collapsingToolbarLayout.title = product?.name

        if (isProductInshoppingCart) {
            fab.setImageResource(R.drawable.ic_in_shopping_cart)
        } else {
            fab.setImageResource(R.drawable.ic_add_shopping_cart)
        }

        Glide.with(this)
                .load<Any>(Constants.BASE_IMAGE_URL + product?.image)
                .centerCrop()
                .into(backdrop)
    }

    private fun renderLoading() {
        TransitionManager.beginDelayedTransition(rootView)
        errorView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        detailsView.visibility = View.GONE
    }

}