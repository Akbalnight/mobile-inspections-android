package ru.madbrains.inspection.ui.main.equipment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class EquipmentTabAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private var data: List<Fragment> = listOf()

    override fun createFragment(position: Int): Fragment {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    fun setItems(items: List<Fragment>) {
        data = items
        notifyDataSetChanged()
    }
}