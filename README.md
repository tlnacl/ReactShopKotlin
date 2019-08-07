# ReactShopKotlin
Using Kotlin to rewrite mosby sample mvi react app without mosby. More detail at http://hannesdorfmann.com/android/mosby3-mvi-1.

HomePresenter https://github.com/tlnacl/ReactShopKotlin/blob/master/app/src/main/java/com/tlnacl/reactiveapp/ui/home/HomePresenter.kt shows the complicated use case how to react with view state.

Working on using Android architecture component to handle orientation change

change Rxjava to Coroutines

In this branch, using kotlinx-coroutines-rx2 to partly convert rxjava to coroutines. It help with migrate rxjava2 to coroutines.

change mvp to use Android architecture component view model