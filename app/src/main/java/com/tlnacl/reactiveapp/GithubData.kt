package com.tlnacl.reactiveapp

/**
 * Created by tomt on 29/05/17.
 */
data class Repository(val id:Long,val name:String,val url:String,val stargazers_count:Int,val language:String,val forks_count:Int)

data class User(val id:Long,val login:String,val url:String,val name:String,val email:String)