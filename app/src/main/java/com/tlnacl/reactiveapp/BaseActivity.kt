package com.tlnacl.reactiveapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by tomt on 29/05/17.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityComponent()
    }

    protected abstract fun setupActivityComponent()
}