package com.tlnacl.reactiveapp.ui.home

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.tlnacl.reactiveapp.businesslogic.model.AdditionalItemsLoadable
import com.tlnacl.reactiveapp.businesslogic.model.FeedItem
import com.tlnacl.reactiveapp.businesslogic.model.Product
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import com.tlnacl.reactiveapp.ui.shop.LoadingViewHolder
import com.tlnacl.reactiveapp.ui.shop.MoreItemsViewHolder
import com.tlnacl.reactiveapp.ui.shop.ProductViewHolder
import com.tlnacl.reactiveapp.ui.shop.SectionHederViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by tomt on 27/06/17.
 */
class HomeAdapter(private val context: Context, private val callback: ProductViewHolder.ProductClickedListener) :
        androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(), MoreItemsViewHolder.LoadItemsClickListener {

    companion object {
        val VIEW_TYPE_PRODUCT = 0
        val VIEW_TYPE_LOADING_MORE_NEXT_PAGE = 1
        val VIEW_TYPE_SECTION_HEADER = 2
        val VIEW_TYPE_MORE_ITEMS_AVAILABLE = 3
    }

    private val loadMoreItemsOfCategoryObservable = PublishSubject.create<String>()

    private var items: List<FeedItem> = emptyList()

    fun getItems(): List<FeedItem> {
        return items
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_PRODUCT -> return ProductViewHolder(context, parent, callback)
            VIEW_TYPE_LOADING_MORE_NEXT_PAGE -> return LoadingViewHolder(context, parent)
            VIEW_TYPE_MORE_ITEMS_AVAILABLE -> return MoreItemsViewHolder(context, this)
            VIEW_TYPE_SECTION_HEADER -> return SectionHederViewHolder(context, parent)
        }

        throw IllegalArgumentException("Couldn't create a ViewHolder for viewType  = " + viewType)
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingViewHolder) {
            return
        }

        val item = items[position]
        when (holder) {
            is ProductViewHolder -> holder.bind(item as Product)
            is SectionHederViewHolder -> holder.onBind(item as SectionHeader)
            is MoreItemsViewHolder -> holder.bind(item as AdditionalItemsLoadable)
            else -> throw IllegalArgumentException("couldn't accept  ViewHolder " + holder)
        }
    }

    override fun loadItemsForCategory(category: String) {
        loadMoreItemsOfCategoryObservable.onNext(category)
    }

    fun loadMoreItemsOfCategoryObservable(): Observable<String> {
        return loadMoreItemsOfCategoryObservable
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
            notifyItemInserted(items.size)
        } else if (notifyRemoved) {
            notifyItemRemoved(items.size)
        }

        return hasLoadingMoreChanged
    }

    fun isLoadingNextPage(): Boolean {
        return isLoadingNextPage
    }

    override fun getItemViewType(position: Int): Int {

        if (isLoadingNextPage && position == items.size) {
            return VIEW_TYPE_LOADING_MORE_NEXT_PAGE
        }

        val item = items[position]

        if (item is Product) {
            return VIEW_TYPE_PRODUCT
        } else if (item is SectionHeader) {
            return VIEW_TYPE_SECTION_HEADER
        } else if (item is AdditionalItemsLoadable) {
            return VIEW_TYPE_MORE_ITEMS_AVAILABLE
        }

        throw IllegalArgumentException("Not able to dertermine the view type for item at position "
                + position
                + ". Item is: "
                + item)
    }

    fun setItems(newItems: List<FeedItem>) {
        val oldItems = this.items
        this.items = newItems

        if (oldItems.isEmpty()) {
            notifyDataSetChanged()
        } else {
            // Use Diff utils
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldItems.size
                }

                override fun getNewListSize(): Int {
                    return newItems.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItems[oldItemPosition]
                    val newItem = newItems[newItemPosition]

                    if (oldItem is Product
                            && newItem is Product
                            && oldItem.id == newItem.id) {
                        return true
                    }

                    if (oldItem is SectionHeader
                            && newItem is SectionHeader
                            && oldItem.name == newItem.name) {
                        return true
                    }

                    if (oldItem is AdditionalItemsLoadable
                            && newItem is AdditionalItemsLoadable
                            && oldItem.categoryName == newItem.categoryName) {
                        return true
                    }

                    return false
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItems[oldItemPosition]
                    val newItem = newItems[newItemPosition]

                    return oldItem == newItem
                }
            }, true).dispatchUpdatesTo(this)
        }
    }
}
