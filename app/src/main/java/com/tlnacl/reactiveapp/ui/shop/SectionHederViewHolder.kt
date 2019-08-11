package com.tlnacl.reactiveapp.ui.shop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_section_header.*

/**
 * @author Hannes Dorfmann
 */
class SectionHederViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun onBind(item: SectionHeader) {
        sectionName.text = item.name
    }
}
