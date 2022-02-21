package com.tlnacl.reactiveapp.ui.home

import android.view.View
import androidx.core.view.isVisible
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
                moreItemsCount.isVisible = false
                loadMoreButton.isVisible = false
                loadingView.isVisible = true
                errorRetryButton.isVisible = false
                itemView.isClickable = false
            }
            item.loadingError != null -> {
                moreItemsCount.isVisible = false
                loadMoreButton.isVisible = false
                loadingView.isVisible = false
                errorRetryButton.isVisible = true
                itemView.isClickable = true
            }
            else -> {
                moreItemsCount.text = "+ ${item.moreItemsCount}"
                moreItemsCount.isVisible = true
                loadMoreButton.isVisible = true
                loadingView.isVisible = false
                errorRetryButton.isVisible = false
                itemView.isClickable = true
            }
        }
    }
}
