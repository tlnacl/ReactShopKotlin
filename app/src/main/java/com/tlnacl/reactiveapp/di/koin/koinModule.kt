package com.tlnacl.reactiveapp.di.koin

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.tlnacl.reactiveapp.BuildConfig
import com.tlnacl.reactiveapp.businesslogic.feed.GroupedPagedFeedLoader
import com.tlnacl.reactiveapp.businesslogic.feed.HomeFeedLoader
import com.tlnacl.reactiveapp.businesslogic.feed.PagingFeedLoader
import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApi
import com.tlnacl.reactiveapp.businesslogic.http.ProductBackendApiDecorator
import com.tlnacl.reactiveapp.businesslogic.searchengine.SearchEngine
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsActivity
import com.tlnacl.reactiveapp.ui.detail.ProductDetailsViewModel
import com.tlnacl.reactiveapp.ui.home.HomeFragment
import com.tlnacl.reactiveapp.ui.home.HomeViewModel
import com.tlnacl.reactiveapp.ui.search.SearchFragment
import com.tlnacl.reactiveapp.ui.search.SearchPresenter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val koinModule = module {

    single { provideProductBackendService() }
    single { ProductBackendApiDecorator(get()) }
    single { PagingFeedLoader(get()) }
    single { GroupedPagedFeedLoader(get()) }
    single { HomeFeedLoader(get(),get()) }
    single { SearchEngine(get()) }

    scope(named<ProductDetailsActivity>()) {
        viewModel { ProductDetailsViewModel(get()) }
    }

    scope(named<ProductDetailsActivity>()) {
        viewModel { ProductDetailsViewModel(get()) }
    }

    scope(named<HomeFragment>()) {
        viewModel { HomeViewModel(get()) }
    }

    scope(named<SearchFragment>()) {
        scoped { SearchPresenter(get()) }
    }
}



fun provideProductBackendService(): ProductBackendApi {
    val httpClientBuilder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("OkHttp").d(message)
            }
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        httpClientBuilder.addInterceptor(loggingInterceptor)
        httpClientBuilder.addNetworkInterceptor(StethoInterceptor())
    }

    val restAdapter = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
    return restAdapter.create(ProductBackendApi::class.java)
}