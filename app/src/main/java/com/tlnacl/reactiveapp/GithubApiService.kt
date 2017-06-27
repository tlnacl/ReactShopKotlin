package com.tlnacl.reactiveapp

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by tomt on 29/05/17.
 */
interface GithubApiService {

    @GET("/users/{username}")
    fun getUser(
            @Path("username") username: String
    ): Observable<User>

    @GET("/users/{username}/repos")
    fun getUsersRepositories(
            @Path("username") username: String
    ): Observable<List<Repository>>
}