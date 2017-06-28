/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tlnacl.reactiveapp.businesslogic.http;

import com.tlnacl.reactiveapp.businesslogic.model.Product;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * The Retrofit interface to retrieve data from the backend over http
 *https://raw.githubusercontent.com/tlnacl/reactiveApp/shop/app/server/api/products3.json
 * @author Hannes Dorfmann
 */
public interface ProductBackendApi {
  @GET("/tlnacl/ReactShopKotlin/master"
      + "/app/server/api/products{pagination}.json")
  Observable<List<Product>> getProducts(@Path("pagination") int pagination);
}
