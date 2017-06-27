package com.tlnacl.reactiveapp

import io.reactivex.Observable
import org.junit.Test

/**
 * Created by tomt on 31/05/17.
 */
class RxJavaTest{
    @Test
    fun startWithTest(){
        Observable.just("1","2","3")
                .startWith("0")
//                .startWith { "0" } // bad kotlin
                .subscribe { println("test" + it) }
    }
}