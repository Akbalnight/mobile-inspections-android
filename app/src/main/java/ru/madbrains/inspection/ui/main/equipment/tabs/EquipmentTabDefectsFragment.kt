package ru.madbrains.inspection.ui.main.equipment.tabs

import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.ui.main.equipment.EquipmentViewModel

class EquipmentTabDefectsFragment : BaseFragment(R.layout.fragment_equipment_tab_defects) {

    private val equipmentViewModel: EquipmentViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}