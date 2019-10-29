package com.tlnacl.reactiveapp.ui.shoppingcart

import com.tlnacl.reactiveapp.businesslogic.ShoppingCart
import com.tlnacl.reactiveapp.ui.BasePresenter
import timber.log.Timber
import java.util.*

/**
 *
 */
//class ShoppingCartOverviewPresenter(private val shoppingCart: ShoppingCart,
//                                    private val deleteSelectedItemsIntent: Observable<Boolean>, private val clearSelectionIntent: Observable<Boolean>) : BasePresenter<ShoppingCartOverviewView, List<ShoppingCartOverviewItem>>() {
//    private var deleteDisposable: Disposable? = null
//    private var deleteSelectedDisposable: Disposable? = null
//
//
//    val viewStateObservable: Observable<List<ShoppingCartOverviewItem>>
//        get() = super.getViewStateObservable()
//
//    protected fun bindIntents() {
//
//        //
//        // Observable that emits a list of selected products over time (or empty list if the selection has been cleared)
//        //
//        val selectedItemsIntent = intent(???({ ShoppingCartOverviewView.selectItemsIntent() }))
//        .mergeWith(clearSelectionIntent.map<R> { ignore -> emptyList<T>() })
//                .doOnNext({ items -> Timber.d("intent: selected items %d", items.size()) })
//                .startWith(ArrayList<Product>(0))
//                .publish()
//                .refCount()
//
//        //
//        // Delete multiple selected Items
//        //
//
//        deleteSelectedDisposable = selectedItemsIntent
//                .switchMap({ selectedItems ->
//                    deleteSelectedItemsIntent.filter { ignored -> !selectedItems.isEmpty() }
//                            .doOnNext { ignored -> Timber.d("intent: remove %d selected items from shopping cart", selectedItems.size) }
//                            .flatMap<Any> { ignore -> shoppingCart.removeProducts(selectedItems).toObservable() }
//                })
//                .subscribe()
//
//        //
//        // Delete a single item
//        //
//        deleteDisposable = intent(???({ ShoppingCartOverviewView.removeItemIntent() }))
//        .doOnNext { item -> Timber.d("intent: remove item from shopping cart: %s", item) }
//                .flatMap({ productToDelete -> shoppingCart.removeProduct(productToDelete).toObservable() })
//                .subscribe()
//        //
//        // Display a list of items in the shopping cart
//        //
//        val shoppingCartContentObservable = intent(???({ ShoppingCartOverviewView.loadItemsIntent() }))
//        .doOnNext { ignored -> Timber.d("load ShoppingCart intent") }
//                .flatMap({ ignored -> shoppingCart.itemsInShoppingCart() })
//
//
//        //
//        // Display list of items / view state
//        //
//        val combiningObservables = Arrays.asList<Observable<*>>(shoppingCartContentObservable, selectedItemsIntent)
//
//        val shoppingCartContentWithSelectedItems = Observable.combineLatest<Any, List<ShoppingCartOverviewItem>>(combiningObservables) { results ->
//            val itemsInShoppingCart = results[0] as List<Product>
//            val selectedProducts = results[1] as List<Product>
//
//            val items = ArrayList<ShoppingCartOverviewItem>(itemsInShoppingCart.size)
//            for (i in itemsInShoppingCart.indices) {
//                val p = itemsInShoppingCart[i]
//                items.add(ShoppingCartOverviewItem(p, selectedProducts.contains(p)))
//            }
//            items
//        }
//                .observeOn(AndroidSchedulers.mainThread())
//
//        subscribeViewState(shoppingCartContentWithSelectedItems, ???({ ShoppingCartOverviewView.render() }))
//    }
//
//    protected fun unbindIntents() {
//        deleteDisposable!!.dispose()
//        deleteSelectedDisposable!!.dispose()
//    }
//}
