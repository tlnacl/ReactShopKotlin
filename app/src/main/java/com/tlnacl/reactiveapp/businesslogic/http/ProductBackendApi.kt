package com.tlnacl.reactiveapp.businesslogic.http

import com.tlnacl.reactiveapp.businesslogic.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * The Retrofit interface to retrieve data from the backend over http
 * https://raw.githubusercontent.com/tlnacl/reactiveApp/shop/app/server/api/products3.json
 */
interface ProductBackendApi {
    @GET("/tlnacl/ReactShopKotlin/master" + "/app/server/api/products{pagination}.json")
    suspend fun getProducts(@Path("pagination") pagination: Int): List<Product>
}
