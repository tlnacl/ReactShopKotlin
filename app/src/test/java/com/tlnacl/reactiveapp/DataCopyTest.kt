package com.tlnacl.reactiveapp

import com.tlnacl.reactiveapp.ui.main.MainViewState
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by tomt on 14/06/17.
 */
class DataCopyTest{
    @Test
    fun testDataCopy(){
        var viewState = MainViewState(userName = "Tom")
        viewState = viewState.copy(userName = "Liang",loading = true)

        assertEquals("Liang",viewState.userName)
        assertEquals(true, viewState.loading)
    }
}