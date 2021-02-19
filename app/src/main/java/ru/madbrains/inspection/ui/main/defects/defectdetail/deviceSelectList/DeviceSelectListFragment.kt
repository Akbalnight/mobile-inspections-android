package ru.madbrains.inspection.ui.main.defects.defectdetail.deviceSelectList

import android.os.Bundle

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_find_device.*
import kotlinx.android.synthetic.main.fragment_defect_find_device.toolbarLayout
import kotlinx.android.synthetic.main.fragment_defect_find_device.progressView
import kotlinx.android.synthetic.main.fragment_defect_list.*
import kotlinx.android.synthetic.main.fragment_routes.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DeviceSelectAdapter

class DeviceSelectListFragment : BaseFragment(R.layout.fragment_defect_find_device) {

    private val deviceSelectViewModel: DeviceSelectListViewModel by viewModel()

    private val deviceSelectAdapter by lazy {
        DeviceSelectAdapter()
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
    }

    private fun setupToolBar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_title_select_device]
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}