package com.tlnacl.reactiveapp.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.tlnacl.reactiveapp.businesslogic.model.SectionHeader
import com.tlnacl.reactiveapp.databinding.ItemSectionHeaderBinding

class SectionHeaderViewHolder(private val binding: ItemSectionHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(item: SectionHeader) {
        binding.sectionName.text = item.name
    }
}
