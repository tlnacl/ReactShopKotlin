package com.tlnacl.reactiveapp.ui.shop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_more_available.*

class MoreItemsViewHolder(override val containerView: View, private val callback: LoadItemsClickListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    interface LoadItemsClickListener {
        fun loadItemsForCategory(category: String)
    }

    private var currentItem: AdditionalItemsLoadable? = null

    init {
        itemView.setOnClickListener { callback.loadItemsForCategory(currentItem!!.categoryName) }
        errorRetryButton.setOnClickListener { callback.loadItemsForCategory(currentItem!!.categoryName) }
        loadMoreButton.setOnClickListener { callback.loadItemsForCategory(currentItem!!.categoryName) }
    }

    fun bind(item: AdditionalItemsLoadable) {
        this.currentItem = item

        if (item.isLoading) {
            // TransitionManager.beginDelayedTransition((ViewGroup) itemView);
            moreItemsCount.visibility = View.GONE
            loadMoreButton.visibility = View.GONE
            loadingView.visibility = View.VISIBLE
            errorRetryButton.visibility = View.GONE
            itemView.isClickable = false
        } else if (item.loadingError != null) {
            //TransitionManager.beginDelayedTransition((ViewGroup) itemView);
            moreItemsCount.visibility = View.GONE
            loadMoreButton.visibility = View.GONE
            loadingView.visibility = View.GONE
            errorRetryButton.visibility = View.VISIBLE
            itemView.isClickable = true
        } else {
            moreItemsCount.text = "+" + item.moreItemsCount
            moreItemsCount.visibility = View.VISIBLE
            loadMoreButton.visibility = View.VISIBLE
            loadingView.visibility = View.GONE
            errorRetryButton.visibility = View.GONE
            itemView.isClickable = true
        }
    }
}
