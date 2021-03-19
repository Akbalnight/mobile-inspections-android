package ru.madbrains.inspection.ui.main.equipment.tabs

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_equipment_tab_common_data.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.EquipmentImageAdapter
import ru.madbrains.inspection.ui.adapters.LinearHorizontalSpacingDecoration
import ru.madbrains.inspection.ui.main.equipment.EquipmentViewModel
import ru.madbrains.inspection.ui.view.EquipmentSpecsTableRow

class EquipmentTabCommonDataFragment : BaseFragment(R.layout.fragment_equipment_tab_common_data) {

    private val equipmentViewModel: EquipmentViewModel by sharedViewModel()

    private val carouselImageAdapter by lazy {
        EquipmentImageAdapter(onImageClick = {})
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        equipmentViewModel.prepareCommonData()
        observeData()
    }

    private fun observeData() {
        equipmentViewModel.equipmentImageList.observe(viewLifecycleOwner, Observer { images ->
            rvEquipmentImages.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val spacing = resources.getDimensionPixelSize(R.dimen.carousel_spacing)
                addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
                adapter = carouselImageAdapter.apply {
                    items = images
                }
            }
        })

        equipmentViewModel.commonSpecsList.observe(viewLifecycleOwner, Observer { specs ->
            tvSpecs.isVisible = true
            tlSpecs.run {
                isVisible = true
                removeAllViews()
            }
            specs.forEach { (labelKey, value) ->
                if (value != null && value is String) tlSpecs.addView(
                    EquipmentSpecsTableRow(context, strings[labelKey], value)
                )
                if (value != null && value is Int) tlSpecs.addView(
                    EquipmentSpecsTableRow(context, strings[labelKey], strings[value])
                )
            }
        })

        equipmentViewModel.measuringPointsList.observe(viewLifecycleOwner, Observer { points ->
            tvMeasurementPoints.isVisible = true
            tlMeasurementPoints.run {
                isVisible = true
                removeAllViews()
                points.forEach {
                    addView(EquipmentSpecsTableRow(context, it, null))
                }
            }
        })

        equipmentViewModel.warrantyData.observe(viewLifecycleOwner, Observer { warranty ->
            tvWarrantySpecs.isVisible = true
            tlWarrantySpecs.run {
                isVisible = true
                removeAllViews()
            }
            warranty.forEach { (labelKey, value) ->
                if (value != null) tlWarrantySpecs.addView(
                    EquipmentSpecsTableRow(context, strings[labelKey], value, true)
                )
            }
        })
    }
}