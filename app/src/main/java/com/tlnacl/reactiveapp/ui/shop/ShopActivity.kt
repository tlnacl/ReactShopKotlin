package com.tlnacl.reactiveapp.ui.shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.databinding.ActivityShopBinding
import com.tlnacl.reactiveapp.ui.home.HomeFragment
import com.tlnacl.reactiveapp.ui.search.SearchFragment
import com.tlnacl.reactiveapp.viewBinding

class ShopActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityShopBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        (application as AndroidApplication).appComponent.inject(this)

        binding.toolbar.title = "Reactive App"
        binding.toolbar.inflateMenu(R.menu.activity_main_toolbar)

        binding.toolbar.setOnMenuItemClickListener {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.shop_layout, SearchFragment())
                    .addToBackStack("Search")
                    .commit()
            true
        }

        // This is the fix for not create view model again for orientation change
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, HomeFragment()).commit()
        }
    }
}
