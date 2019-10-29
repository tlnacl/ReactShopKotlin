package com.tlnacl.reactiveapp.ui.shoppingcart

import com.tlnacl.reactiveapp.businesslogic.model.Product
import kotlinx.coroutines.flow.Flow

interface ShoppingCartOverviewView {

    /**
     * Intent to load the items from the shopping cart
     */
    fun loadItemsIntent(): Flow<Boolean>

    /**
     * Intent to mark a given item as selected
     */
    fun selectItemsIntent(): Flow<List<Product>>

    /**
     * Intent to remove a given item from the shopping cart
     */
    fun removeItemIntent(): Flow<Product>

    /**
     * Renders the View with the given items that are in the shopping cart right now
     */
    fun render(itemsInShoppingCart: List<ShoppingCartOverviewItem>)
}