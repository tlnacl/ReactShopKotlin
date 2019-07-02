package com.tlnacl.reactiveapp.ui.shop

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.tlnacl.reactiveapp.R
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader

/**
 * @author Hannes Dorfmann
 */
class SectionHederViewHolder(context: Context, parent: ViewGroup)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_section_header, parent, false)) {

    @BindView(R.id.sectionName) lateinit var sectionName: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun onBind(item: SectionHeader) {
        sectionName.text = item.name
    }
}
