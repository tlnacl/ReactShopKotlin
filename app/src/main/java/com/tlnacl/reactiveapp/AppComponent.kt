package com.tlnacl.reactiveapp

import com.tlnacl.reactiveapp.ui.shop.SearchFragment
import com.tlnacl.reactiveapp.ui.shop.ShopActivity
import dagger.Component
import javax.inject.Singleton

@Component(
  modules = arrayOf(
      AndroidModule::class
  )
)
@Singleton
interface AppComponent {
  fun inject(into: ShopActivity)
  fun inject(into: SearchFragment)
}