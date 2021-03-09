package ru.madbrains.inspection.ui.main.defects.defectdetail.deviceSelectList

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_find_device.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.EquipmentsModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DeviceSelectAdapter
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailViewModel
import ru.madbrains.inspection.ui.view.SearchToolbar

class DeviceSelectListFragment : BaseFragment(R.layout.fragment_defect_find_device) {

    private val deviceSelectViewModel: DeviceSelectListViewModel by viewModel()
    private val defectDetailViewModel: DefectDetailViewModel by sharedViewModel()

    private val deviceSelectAdapter by lazy {
        DeviceSelectAdapter(
                onDeviceSelectClick = {
                    val deviceSelect = deviceSelectViewModel.deviceListModels.find { deviceSelect ->
                        deviceSelect.id == it.id
                    }
                    deviceSelectViewModel.deviceSelectClick(deviceSelect)
                })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolBar()

        rvDeviceSelectList.adapter = deviceSelectAdapter

        deviceSelectViewModel.getEquipments()

        deviceSelectViewModel.deviceList.observe(viewLifecycleOwner, Observer {
            deviceSelectAdapter.items = it
        })

        deviceSelectViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        deviceSelectViewModel.navigateToDefectDetail.observe(
                viewLifecycleOwner,
                EventObserver {
                    backToDefectDetailFragment(it)
                })
    }

    private fun setupToolBar() {
        (toolbarLayout as SearchToolbar).apply {
            tvTitle.text = strings[R.string.fragment_title_select_device]
            btnLeading.setOnClickListener {
                onNavigationBack(this@DeviceSelectListFragment)
            }
            setupSearch(context.drawables[R.drawable.ic_back],
                    { deviceSelectViewModel.searchEquipments(it) }
            )
        }
    }

    private fun backToDefectDetailFragment(equipment: EquipmentsModel) {
        defectDetailViewModel.changeCurrentDefectDevice(equipment)
        findNavController().popBackStack()
    }

}