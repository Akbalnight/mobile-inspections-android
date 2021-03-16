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
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.inspection.R

import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.TechOperationAdapter
import ru.madbrains.inspection.ui.main.defects.defectlist.DefectListFragment
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel


class TechOperationsFragment : BaseFragment(R.layout.fragment_tech_operations) {

    companion object {
        const val KEY_TECH_MAP = "tech_map"
    }

    private val techOperationsViewModel: TechOperationsViewModel by sharedViewModel()
    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()

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
            val techMapModel = (getSerializable(KEY_TECH_MAP) as? TechMapModel)
            techMapModel?.let {
                techOperationsViewModel.setTechMapModel(it)
                setupToolbar(it.pointNumber)
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

        layoutBottomButtonAddDefect.setOnClickListener { clickAddDefect() }

        layoutBottomButtonDefect.setOnClickListener {
            techOperationsViewModel.techMap?.let { techMapModel ->
                if (!routePointsViewModel.routeDataModels.isNullOrEmpty()) {
                    val routePoints = routePointsViewModel.routeDataModels.filter {
                        it.techMapId == techMapModel.id
                    }
                    if (!routePoints.isNullOrEmpty()) {
                        val deviceList = arrayListOf<String>().apply {
                            routePoints[0].equipments?.map { equipmentModel ->
                                equipmentModel.name?.let { name ->
                                    add(name)
                                }
                            }
                        }
                        clickDefectListFragment(deviceList)
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        techOperationsViewModel.navigateToAddDefect.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_techOperationsFragment_to_addDefectFragment)
        })
    }

    private fun clickAddDefect() {
        techOperationsViewModel.addDefect()
    }

    private fun clickDefectListFragment(device: ArrayList<String>) {
        val args = bundleOf(
                DefectListFragment.KEY_DEVICE_ID_DEFECT_LIST to device
        )
        findNavController().navigate(R.id.graph_defects, args)
    }
}