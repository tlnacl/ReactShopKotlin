package com.tlnacl.reactiveapp.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.tlnacl.reactiveapp.R

/**
 * Created by tomt on 22/06/17.
 */
class ShoppingCartOverviewFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.layout_empty, container, false)
        ButterKnife.bind(this,view!!)
        return view
    }
}