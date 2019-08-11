package com.tlnacl.reactiveapp.ui.shop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tlnacl.reactiveapp.Constants
import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*

/**
 * Created by tomt on 23/06/17.
 */
class ProductViewHolder(override val containerView: View, private val callback: ProductClickedListener)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(product: Product){
        Glide.with(itemView.context)
                .load(Constants.BASE_IMAGE_URL + product.image)
                .apply(RequestOptions.centerCropTransform())
                .into(productImage)
        productName.text = product.name
        itemView.setOnClickListener { callback.onProductClicked(product) }
    }

    interface ProductClickedListener {
        fun onProductClicked(product: Product)
    }
}