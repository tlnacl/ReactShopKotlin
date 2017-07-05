package com.tlnacl.reactiveapp.ui.shop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.tlnacl.reactiveapp.AndroidApplication
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.ui.home.HomeFragment
import com.tlnacl.reactiveapp.ui.search.SearchFragment

class ShopActivity : AppCompatActivity() {
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.sliding_layout) lateinit var slidingUpPanel: SlidingUpPanelLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        ButterKnife.bind(this)
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
        if (slidingUpPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            return true
        }
        return false
    }
}
