package com.tlnacl.reactiveapp

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 *
 */
suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.map { it.await() }
}