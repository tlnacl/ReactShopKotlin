package com.tlnacl.reactiveapp

import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.home.HomeFragment
import com.tlnacl.reactiveapp.ui.search.SearchFragment
import com.tlnacl.reactiveapp.ui.shop.ShopActivity
import dagger.Component
import javax.inject.Singleton

@Component(
  modules = [AndroidModule::class]
)
@Singleton
interface AppComponent {
  fun inject(into: ShopActivity)
  fun inject(into: ProductDetailsActivity)
  fun inject(into: SearchFragment)
  fun inject(into: HomeFragment)
}