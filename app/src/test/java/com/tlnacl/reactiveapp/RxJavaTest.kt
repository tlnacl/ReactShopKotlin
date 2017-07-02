package com.tlnacl.reactiveapp

import io.reactivex.Observable
import org.junit.Test
import java.util.*

/**
 * Created by tomt on 31/05/17.
 */
class RxJavaTest {
    @Test
    fun startWithTest() {
        Observable.just("1", "2", "3")
                .startWith("0")
//                .startWith { "0" } // bad kotlin
                .subscribe { println("test" + it) }
    }

    fun Date.isTuesday(): Boolean {
        return day == 2
    }

    @Test
    fun isTuesdayTest() {
        val epoch = Date(1970, 0, 0)
        if (epoch.isTuesday()) println("is Tuesday")
        else println("not Tuesday")
    }


}