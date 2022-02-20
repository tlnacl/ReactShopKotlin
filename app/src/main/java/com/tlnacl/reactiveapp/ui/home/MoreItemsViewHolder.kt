package com.tlnacl.reactiveapp.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.databinding.ItemMoreAvailableBinding

class MoreItemsViewHolder(
    private val binding: ItemMoreAvailableBinding,
    private val onMoreClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AdditionalItemsLoadable) = binding.apply {
        itemView.setOnClickListener { onMoreClick(item.categoryName) }
        binding.errorRetryButton.setOnClickListener { onMoreClick(item.categoryName) }
        binding.loadMoreButton.setOnClickListener { onMoreClick(item.categoryName) }
        when {
            item.isLoading -> {
                moreItemsCount.visibility = View.GONE
                loadMoreButton.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
                errorRetryButton.visibility = View.GONE
                itemView.isClickable = false
            }
            item.loadingError != null -> {
                moreItemsCount.visibility = View.GONE
                loadMoreButton.visibility = View.GONE
                loadingView.visibility = View.GONE
                errorRetryButton.visibility = View.VISIBLE
                itemView.isClickable = true
            }
            else -> {
                moreItemsCount.text = "+ ${item.moreItemsCount}"
                moreItemsCount.visibility = View.VISIBLE
                loadMoreButton.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
                errorRetryButton.visibility = View.GONE
                itemView.isClickable = true
            }
        }
    }
}
