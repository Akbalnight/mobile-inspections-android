package ru.madbrains.inspection.ui.main.routes.techoperations

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_tech_operations.*
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
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailFragment
import ru.madbrains.inspection.ui.main.defects.defectlist.DefectListFragment
import ru.madbrains.inspection.ui.main.equipment.EquipmentFragment
import ru.madbrains.inspection.ui.main.equipmentList.EquipmentListFragment
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel

class TechOperationsFragment : BaseFragment(R.layout.fragment_tech_operations) {

    companion object {
        const val KEY_ROUTE_DATA = "KEY_ROUTE_DATA"
    }

    private val techOperationsViewModel: TechOperationsViewModel by sharedViewModel()
    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private val techOperationsAdapter by lazy {
        TechOperationAdapter(
            routePointsViewModel.isDetourEditable(),
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
                setupToolbar(it.position)
                setupUI(it)
            }
        }

        fabTechOperationsScanRFID.setOnClickListener {
            techOperationsViewModel.checkRfidAndFinish()
        }

        fabTechOperationsSave.setOnClickListener {
            techOperationsViewModel.checkAvailableFinishTechMap()
        }

        rvTechOperations.adapter = techOperationsAdapter

        techOperationsViewModel.titleTechOperations.observe(viewLifecycleOwner, Observer {
            tvTitleTechOperations.text = it
        })

        techOperationsViewModel.techOperations.observe(viewLifecycleOwner, Observer {
            techOperationsAdapter.items = it
        })

        techOperationsViewModel.navigatePop.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        techOperationsViewModel.showDialogBlankFields.observe(viewLifecycleOwner, EventObserver {
            showDialogBlankFields()
        })

        setupOnClickListener()

        setupNavigation()

        progressView.setTextButton(strings[R.string.stop]) {
            techOperationsViewModel.stopRfidScan()
        }

        techOperationsViewModel.rfidProgress.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
            progressView.changeTextVisibility(it)
        })

        techOperationsViewModel.showDialog.observe(viewLifecycleOwner, EventObserver {
            val alertDialog: AlertDialog? = activity?.let { activity ->
                val builder = AlertDialog.Builder(activity)
                builder.apply {
                    setTitle(strings[it])
                    setMessage("")
                    setPositiveButton(strings[R.string.ok]) { _, _ -> }
                }
                builder.create()
            }
            alertDialog?.show()
        })
    }

    private fun setupUI(routeDataModel: RouteDataModel) {
        val rfidVisible = routeDataModel.rfidCode != null
        val isEditable = routePointsViewModel.isDetourEditable()
        fabTechOperationsScanRFID.isVisible = rfidVisible && isEditable
        fabTechOperationsSave.isVisible = !rfidVisible && isEditable
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
            findNavController().navigate(
                R.id.action_techOperationsFragment_to_equipmentFragment, bundleOf(
                    EquipmentFragment.KEY_EQUIPMENT_DATA to it
                )
            )
        })

        techOperationsViewModel.navigateToEquipmentList.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                R.id.action_techOperationsFragment_to_equipmentListFragment, bundleOf(
                    EquipmentListFragment.KEY_EQUIPMENT_LIST_DATA to it
                )
            )
        })
    }


    private fun getEquipmentIds(): ArrayList<String>? {
        techOperationsViewModel.savedRouteData?.equipments?.let {
            if (!it.isNullOrEmpty()) {
                return arrayListOf<String>().apply {
                    it.map { equipmentModel ->
                        add(equipmentModel.id)
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

    private fun toDefectDetailFragment() {
        findNavController().navigate(
            R.id.action_techOperationsFragment_to_addDefectFragment, bundleOf(
                DefectDetailFragment.KEY_EQUIPMENT_LIST to getEquipments(),
                DefectDetailFragment.KEY_DETOUR_ID to routePointsViewModel.detourModel?.id
            )
        )
    }

    private fun toDefectListFragment() {
        findNavController().navigate(
            R.id.graph_defects, bundleOf(
                DefectListFragment.KEY_EQUIPMENTS_IDS_DEFECT_LIST to getEquipmentIds(),
                DefectListFragment.KEY_IS_CONFIRM_MODE to true
            )
        )
    }

    private fun showDialogBlankFields() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(strings[R.string.fragment_tech_operations_dialog_empty_fields_title])
                setMessage(strings[R.string.fragment_tech_operations_dialog_empty_fields_message])
                setPositiveButton(strings[R.string.fragment_tech_operations_dialog_empty_fields_button_complete],
                    DialogInterface.OnClickListener { _, _ ->
                        techOperationsViewModel.finishTechMap()
                    })
                setNegativeButton(strings[R.string.fragment_tech_operations_dialog_empty_fields_button_cancel],
                    DialogInterface.OnClickListener { _, _ ->
                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }
}