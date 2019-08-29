package com.tlnacl.reactiveapp.ui.shop

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tlnacl.reactiveapp.Constants
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.Product

/**
 * Created by tomt on 23/06/17.
 */
class ProductViewHolder(context: Context, parent: ViewGroup, val callback: ProductClickedListener)
    :RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)) {

        fun bind(product: Product){
            // android extentions Does not work for search view maybe because of two hold in same screen
            val productImage = itemView.findViewById<ImageView>(R.id.productImage)
            val productName = itemView.findViewById<TextView>(R.id.productName)
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
