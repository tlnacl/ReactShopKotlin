package com.tlnacl.reactiveapp.ui.shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.ui.home.HomeFragment
import com.tlnacl.reactiveapp.ui.search.SearchFragment
import kotlinx.android.synthetic.main.activity_shop.*

class ShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        (application as AndroidApplication).appComponent.inject(this)

        toolbar.title = "Reactive App"
        toolbar.inflateMenu(R.menu.activity_main_toolbar)

        toolbar.setOnMenuItemClickListener {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.shop_layout, SearchFragment())
                    .addToBackStack("Search")
                    .commit()
            true
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, HomeFragment()).commit()
    }

    private fun closeSlidingUpPanelIfOpen(): Boolean {
        if (sliding_layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            return true
        }
        return false
    }
}
