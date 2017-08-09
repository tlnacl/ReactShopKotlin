package com.tlnacl.reactiveapp.ui.detail

import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.model.ProductDetail
import com.tlnacl.reactiveapp.ui.BasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by tlnacl on 11/07/17.
 */
class ProductDetailsPresenter @Inject constructor(val api: ProductBackendApiDecorator) : BasePresenter<ProductDetailsView>() {
    private var startDisposables = CompositeDisposable()
    fun handleUiEvent(productId: Observable<Int>) {
        startDisposables.add(productId.flatMap {
            api.getProduct(it)
                    .map<ProductDetailsViewState> { ProductDetailsViewState.Data(ProductDetail(it, false)) }
                    .onErrorReturn { error -> ProductDetailsViewState.Error(error) }
                    .subscribeOn(Schedulers.io())
                    .startWith(ProductDetailsViewState.Loading)
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mvpView?.render(it) })
    }
}