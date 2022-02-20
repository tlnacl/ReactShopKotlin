package com.tlnacl.reactiveapp.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.databinding.ItemProductBinding
import com.tlnacl.reactiveapp.ui.home.ProductViewHolder

/**
 * Created by tomt on 21/06/17.
 */
class SearchAdapter(private val onProductClick: (Product) -> Unit) :
    ListAdapter<Product, ProductViewHolder>(SearchDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onProductClick
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SearchDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

}

