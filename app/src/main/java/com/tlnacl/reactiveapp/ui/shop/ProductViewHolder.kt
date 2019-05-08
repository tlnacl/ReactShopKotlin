package com.tlnacl.reactiveapp.ui.shop

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.tlnacl.reactiveapp.Constants
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product

/**
 * Created by tomt on 23/06/17.
 */
class ProductViewHolder(context: Context, parent: ViewGroup, val callback: ProductClickedListener)
    : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)) {

    @BindView(R.id.productImage) lateinit var image: ImageView
    @BindView(R.id.productName) lateinit var name: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun bind(product: Product){
        Glide.with(itemView.context)
                .load<Any>(Constants.BASE_IMAGE_URL + product.image)
                .centerCrop()
                .into(image)
        name.setText(product.name)
        itemView.setOnClickListener { callback.onProductClicked(product) }
    }

    interface ProductClickedListener {
        fun onProductClicked(product: Product)
    }
}