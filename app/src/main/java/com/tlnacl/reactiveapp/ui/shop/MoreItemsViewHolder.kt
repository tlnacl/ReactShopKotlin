package com.tlnacl.reactiveapp.ui.shop

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable

class MoreItemsViewHolder constructor(itemView: View, listener: LoadItemsClickListener) : RecyclerView.ViewHolder(itemView) {

    interface LoadItemsClickListener {
        fun loadItemsForCategory(category: String)
    }

    @BindView(R.id.moreItemsCount) lateinit var moreItemsCount: TextView
    @BindView(R.id.loadingView) lateinit var loadingView: View
    @BindView(R.id.loadMoreButtton) lateinit var loadMoreButton: View
    @BindView(R.id.errorRetryButton) lateinit var errorRetry: Button

    private var currentItem: AdditionalItemsLoadable? = null

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {listener.loadItemsForCategory(currentItem!!.categoryName) }
        errorRetry.setOnClickListener {listener.loadItemsForCategory(currentItem!!.categoryName) }
        loadMoreButton.setOnClickListener {listener.loadItemsForCategory(currentItem!!.categoryName) }
    }

    fun bind(item: AdditionalItemsLoadable) {
        this.currentItem = item

        if (item.isLoading) {
            // TransitionManager.beginDelayedTransition((ViewGroup) itemView);
            moreItemsCount.visibility = View.GONE
            loadMoreButton.visibility = View.GONE
            loadingView.visibility = View.VISIBLE
            errorRetry.visibility = View.GONE
            itemView.isClickable = false
        } else if (item.loadingError != null) {
            //TransitionManager.beginDelayedTransition((ViewGroup) itemView);
            moreItemsCount.visibility = View.GONE
            loadMoreButton.visibility = View.GONE
            loadingView.visibility = View.GONE
            errorRetry.visibility = View.VISIBLE
            itemView.isClickable = true
        } else {
            moreItemsCount.text = "+" + item.moreItemsCount
            moreItemsCount.visibility = View.VISIBLE
            loadMoreButton.visibility = View.VISIBLE
            loadingView.visibility = View.GONE
            errorRetry.visibility = View.GONE
            itemView.isClickable = true
        }
    }

    companion object {

        fun create(layoutInflater: LayoutInflater,
                   clickListener: LoadItemsClickListener): MoreItemsViewHolder {
            return MoreItemsViewHolder(
                    layoutInflater.inflate(R.layout.item_more_available, null, false), clickListener)
        }
    }
}
