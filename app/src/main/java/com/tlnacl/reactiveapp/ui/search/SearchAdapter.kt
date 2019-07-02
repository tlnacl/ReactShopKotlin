package com.tlnacl.reactiveapp.ui.search

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder

/**
 * Created by tomt on 21/06/17.
 */
class SearchAdapter(private val context: Context,private val callback: ProductViewHolder.ProductClickedListener): androidx.recyclerview.widget.RecyclerView.Adapter<ProductViewHolder>(){
    private var products: List<Product> = emptyList()

    fun setProducts(products:List<Product>){
        this.products = products
        notifyDataSetChanged()
    }

    override fun getItemCount()= products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(context, parent, callback)

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) = holder.bind(products[position])

}

