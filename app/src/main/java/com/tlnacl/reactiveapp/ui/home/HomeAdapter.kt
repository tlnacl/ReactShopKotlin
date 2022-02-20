package com.tlnacl.reactiveapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import com.tlnacl.reactiveapp.databinding.ItemLoadingBinding
import com.tlnacl.reactiveapp.databinding.ItemMoreAvailableBinding
import com.tlnacl.reactiveapp.databinding.ItemProductBinding
import com.tlnacl.reactiveapp.databinding.ItemSectionHeaderBinding

/**
 * Created by tomt on 27/06/17.
 */
class HomeAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onMoreClick: (String) -> Unit
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(HomeDiffCallback()) {

    companion object {
        const val VIEW_TYPE_PRODUCT = 0
        const val VIEW_TYPE_LOADING_MORE_NEXT_PAGE = 1
        const val VIEW_TYPE_SECTION_HEADER = 2
        const val VIEW_TYPE_MORE_ITEMS_AVAILABLE = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_PRODUCT -> return ProductViewHolder(
                ItemProductBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                ), onProductClick
            )
            VIEW_TYPE_LOADING_MORE_NEXT_PAGE -> return LoadingViewHolder(
                ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_MORE_ITEMS_AVAILABLE -> return MoreItemsViewHolder(
                ItemMoreAvailableBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onMoreClick
            )
            VIEW_TYPE_SECTION_HEADER -> return SectionHeaderViewHolder(
                ItemSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        throw IllegalArgumentException("Couldn't create a ViewHolder for viewType  = $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingViewHolder) {
            return
        }

        val item = getItem(position)
        when (holder) {
            is ProductViewHolder -> holder.bind(item as Product)
            is SectionHeaderViewHolder -> holder.onBind(item as SectionHeader)
            is MoreItemsViewHolder -> holder.bind(item as AdditionalItemsLoadable)
            else -> throw IllegalArgumentException("couldn't accept  ViewHolder $holder")
        }
    }

    private var isLoadingNextPage: Boolean = false

    /**
     * @return true if value has changed since last invocation
     */
    fun setLoadingNextPage(loadingNextPage: Boolean): Boolean {
        val hasLoadingMoreChanged = loadingNextPage != isLoadingNextPage

        val notifyInserted = loadingNextPage && hasLoadingMoreChanged
        val notifyRemoved = !loadingNextPage && hasLoadingMoreChanged
        isLoadingNextPage = loadingNextPage

        if (notifyInserted) {
            notifyItemInserted(itemCount)
        } else if (notifyRemoved) {
            notifyItemRemoved(itemCount)
        }

        return hasLoadingMoreChanged
    }

    fun isLoadingNextPage(): Boolean {
        return isLoadingNextPage
    }

    override fun getItemViewType(position: Int): Int {

        if (isLoadingNextPage && position == itemCount) {
            return VIEW_TYPE_LOADING_MORE_NEXT_PAGE
        }

        when (val item = getItem(position)) {
            is Product -> {
                return VIEW_TYPE_PRODUCT
            }
            is SectionHeader -> {
                return VIEW_TYPE_SECTION_HEADER
            }
            is AdditionalItemsLoadable -> {
                return VIEW_TYPE_MORE_ITEMS_AVAILABLE
            }
            else -> throw IllegalArgumentException(
                "Not able to dertermine the view type for item at position "
                        + position
                        + ". Item is: "
                        + item
            )
        }

    }
}

class HomeDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem is Product && newItem is Product && oldItem.id == newItem.id) {
            return true
        }

        if (oldItem is SectionHeader && newItem is SectionHeader && oldItem.name == newItem.name) {
            return true
        }

        if (oldItem is AdditionalItemsLoadable && newItem is AdditionalItemsLoadable && oldItem.categoryName == newItem.categoryName) {
            return true
        }
        return false
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem) =
        areItemsTheSame(oldItem, newItem)

}
