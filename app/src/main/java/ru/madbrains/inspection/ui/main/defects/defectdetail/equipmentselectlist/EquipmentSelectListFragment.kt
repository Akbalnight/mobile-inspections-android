package ru.madbrains.inspection.ui.main.defects.defectdetail.equipmentselectlist

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_find_equipment.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.EquipmentSelectAdapter
import ru.madbrains.inspection.ui.view.SearchToolbar

class EquipmentSelectListFragment : BaseFragment(R.layout.fragment_defect_find_equipment) {

    private val equipmentSelectViewModel: EquipmentSelectListViewModel by sharedViewModel()

    private val equipmentSelectAdapter by lazy {
        EquipmentSelectAdapter(
                onDeviceSelectClick = {
                    val deviceSelect = equipmentSelectViewModel.deviceListModels.find { deviceSelect ->
                        deviceSelect.id == it.id
                    }
                    equipmentSelectViewModel.deviceSelectClick(deviceSelect)
                })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvDeviceSelectList.adapter = equipmentSelectAdapter

        equipmentSelectViewModel.getEquipments()

        setupToolBar()

        equipmentSelectViewModel.deviceList.observe(viewLifecycleOwner, Observer {
            equipmentSelectAdapter.items = it
        })

        equipmentSelectViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        equipmentSelectViewModel.navigateToDefectDetail.observe(
                viewLifecycleOwner,
                EventObserver {
                    findNavController().popBackStack()
                })
    }

    private fun setupToolBar() {
        (toolbarLayout as SearchToolbar).apply {
            tvTitle.text = strings[R.string.fragment_title_select_device]
            btnLeading.setOnClickListener {
                onNavigationBack(this@EquipmentSelectListFragment)
            }
            setupSearch(context.drawables[R.drawable.ic_back],
                    { equipmentSelectViewModel.searchEquipments(it) }
            )
        }
    }

}