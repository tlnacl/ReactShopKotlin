package com.tlnacl.reactiveapp.ui.home

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tlnacl.reactiveapp.Constants
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.databinding.ItemProductBinding

/**
 * Created by tomt on 23/06/17.
 */
class ProductViewHolder(
    private val binding: ItemProductBinding,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {
        binding.productImage.load(Constants.BASE_IMAGE_URL + product.image)
        binding.productName.text = product.name
        itemView.setOnClickListener { onProductClick(product) }
    }
}
