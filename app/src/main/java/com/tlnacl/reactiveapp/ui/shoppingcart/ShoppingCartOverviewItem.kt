package com.tlnacl.reactiveapp.ui.shoppingcart

import com.tlnacl.reactiveapp.businesslogic.model.Product

/**
 * Is part of the view state for [ShoppingCartOverviewView]
 */
class ShoppingCartOverviewItem(val product: Product, val isSelected: Boolean) {

    override fun toString(): String {
        return "ShoppingCartItem{" +
                "product=" + product +
                ", isSelected=" + isSelected +
                '}'.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val item = o as ShoppingCartOverviewItem?

        return if (isSelected != item!!.isSelected) false else product.equals(item.product)
    }

    override fun hashCode(): Int {
        var result = product.hashCode()
        result = 31 * result + if (isSelected) 1 else 0
        return result
    }
}
