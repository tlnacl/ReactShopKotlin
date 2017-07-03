package com.tlnacl.reactiveapp

import org.junit.Test

/**
 * Created by tlnacl on 3/07/17.
 */
class KotlinPuzzler {
    fun main1() = println("Hello1")
    fun main2() = { println("Hello2") }

    @Test
    fun helloHelloTest() {
        main1()
        main2()
    }
}