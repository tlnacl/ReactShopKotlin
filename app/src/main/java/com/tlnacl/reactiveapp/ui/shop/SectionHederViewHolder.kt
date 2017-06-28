package com.tlnacl.reactiveapp.ui.shop

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader

/**
 * @author Hannes Dorfmann
 */
class SectionHederViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.sectionName) lateinit var sectionName: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun onBind(item: SectionHeader) {
        sectionName.text = item.name
    }

    companion object {

        fun create(layoutInflater: LayoutInflater): SectionHederViewHolder {
            return SectionHederViewHolder(
                    layoutInflater.inflate(R.layout.item_section_header, null, false))
        }
    }
}
