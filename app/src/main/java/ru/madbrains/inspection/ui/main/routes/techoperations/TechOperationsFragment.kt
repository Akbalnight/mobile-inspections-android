package ru.madbrains.inspection.ui.main.routes.techoperations

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_tech_operations.*
import kotlinx.android.synthetic.main.fragment_tech_operations.progressView
import kotlinx.android.synthetic.main.fragment_tech_operations.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.R

import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.TechOperationAdapter
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailFragment
import ru.madbrains.inspection.ui.main.defects.defectlist.DefectListFragment
import ru.madbrains.inspection.ui.main.equipment.EquipmentFragment
import ru.madbrains.inspection.ui.main.equipmentList.EquipmentListFragment
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel


class TechOperationsFragment : BaseFragment(R.layout.fragment_tech_operations) {

    companion object {
        const val KEY_ROUTE_DATA = "KEY_ROUTE_DATA"
    }

    private val techOperationsViewModel: TechOperationsViewModel by sharedViewModel()
    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private val techOperationsAdapter by lazy {
        TechOperationAdapter(
            onDataInput = {
                techOperationsViewModel.onTechDataInput(it.id, it.inputData)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        techOperationsViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        requireNotNull(arguments).run {
            val routeDataModel = (getSerializable(KEY_ROUTE_DATA) as? RouteDataModel)
            routeDataModel?.let {
                techOperationsViewModel.setRouteData(it)
                setupToolbar(it.techMap?.pointNumber)
            }
        }

        fabTechOperationsSave.setOnClickListener {
            techOperationsViewModel.finishTechMap()
            findNavController().popBackStack()
        }

        rvTechOperations.adapter = techOperationsAdapter

        techOperationsViewModel.titleTechOperations.observe(viewLifecycleOwner, Observer {
            tvTitleTechOperations.text = it
        })

        techOperationsViewModel.techOperations.observe(viewLifecycleOwner, Observer {
            techOperationsAdapter.items = it
        })

        setupOnClickListener()

        setupNavigation()

    }

    private fun setupToolbar(positionPoint: Int?) {

        toolbarLayout.apply {
            var toolBarTitle = strings[R.string.fragment_tech_operations_app_bar]
            positionPoint?.let {
                toolBarTitle += it.toString()
            }
            tvTitle.text = toolBarTitle
            toolbarLayout.btnLeading.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupOnClickListener() {

        layoutBottomButtonAddDefect.setOnClickListener { toDefectDetailFragment() }

        layoutBottomButtonDefect.setOnClickListener {
            toDefectListFragment()
        }
        layoutBottomButtonDevice.setOnClickListener {
            techOperationsViewModel.toEquipmentFragment()
        }
    }

    private fun setupNavigation() {

        techOperationsViewModel.navigateToEquipment.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_techOperationsFragment_to_equipmentFragment, bundleOf(
                EquipmentFragment.KEY_EQUIPMENT_DATA to it
            ))
        })

        techOperationsViewModel.navigateToEquipmentList.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_techOperationsFragment_to_equipmentListFragment, bundleOf(
                EquipmentListFragment.KEY_EQUIPMENT_LIST_DATA to it
            ))
        })
    }


    private fun getEquipmentNames(): ArrayList<String>? {
        techOperationsViewModel.savedRouteData?.equipments?.let {
            if (!it.isNullOrEmpty()) {
                return arrayListOf<String>().apply {
                    it.map { equipmentModel ->
                        equipmentModel.name?.let { name ->
                            add(name)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun getEquipments(): List<EquipmentModel>? {
        techOperationsViewModel.savedRouteData?.equipments?.let {
            return it
        }
        return null
    }

    private fun getDetourId(): String? {
        techOperationsViewModel.savedRouteData?.routeId?.let { routeId ->
            val detourId = detoursViewModel.detourModels.find { it.id == routeId }
            detourId?.let {
                return it.id
            }
        }
        return null
    }

    private fun toDefectDetailFragment() {
        findNavController().navigate(R.id.action_techOperationsFragment_to_addDefectFragment, bundleOf(
                DefectDetailFragment.KEY_EQUIPMENT_LIST to getEquipments(),
                DefectDetailFragment.KEY_DETOUR_ID to getDetourId()
        ))
    }

    private fun toDefectListFragment() {
        findNavController().navigate(R.id.graph_defects, bundleOf(
                DefectListFragment.KEY_EQUIPMENTS_IDS_DEFECT_LIST to getEquipmentNames(),
                DefectListFragment.KEY_IS_CONFIRM_DEFECT_LIST to true
        ))
    }
}