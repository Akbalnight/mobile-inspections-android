package ru.madbrains.inspection.ui.main.equipment.tabs

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_equipment_tab_defects.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.domain.model.DefectStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DefectListAdapter
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailFragment
import ru.madbrains.inspection.ui.main.defects.defectlist.DefectListViewModel
import ru.madbrains.inspection.ui.main.equipment.EquipmentViewModel

class EquipmentTabDefectsFragment : BaseFragment(R.layout.fragment_equipment_tab_defects) {

    private val equipmentViewModel: EquipmentViewModel by sharedViewModel()
    private val defectListViewModel: DefectListViewModel by viewModel()


    private val defectsAdapter by lazy {
        DefectListAdapter(
            onEditClick = {
            },
            onDeleteClick = {
            },
            onConfirmClick = {
                val defect = defectListViewModel.defectListModels.find { defectModel ->
                    defectModel.id == it.id
                }
                defectListViewModel.confirmDefect(defect)
            },
            onEliminatedClick = {
                val defect = defectListViewModel.defectListModels.find { defectModel ->
                    defectModel.id == it.id
                }
                showDialogEliminatedDefect(defect)
            }

        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        defectListViewModel.isConfirmMode = true

        setupRVList()

        equipmentViewModel.savedEquipmentData?.let {
            defectListViewModel.getDefectList(listOf(it.id))
        } ?: run {
            defectListViewModel.getDefectList(null)
        }

        defectListViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        defectListViewModel.navigateToConfirmDefect.observe(viewLifecycleOwner, EventObserver {
            val args = bundleOf(
                DefectDetailFragment.KEY_DETAIL_DEFECT to it,
                DefectDetailFragment.KEY_DEFECT_TARGET_STATUS to DefectStatus.CONFIRMED
            )
            findNavController().navigate(R.id.action_equipmentFragment_to_detailFragment, args)
        })
    }

    private fun setupRVList() {
        rvDefectList.adapter = defectsAdapter

        defectListViewModel.defectList.observe(viewLifecycleOwner, Observer {
            defectsAdapter.items = it
        })
    }

    private fun showDialogEliminatedDefect(item: DefectModel?) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(strings[R.string.fragment_defect_dialog_eliminated_defect_title])
                setMessage(strings[R.string.fragment_defect_dialog_eliminated_defect_subtitle])
                setPositiveButton(strings[R.string.fragment_dialog_btn_save],
                    DialogInterface.OnClickListener { _, _ ->
                        defectListViewModel.eliminateDefect(item)
                    })
                setNegativeButton(strings[R.string.fragment_dialog_btn_cancel],
                    DialogInterface.OnClickListener { _, _ ->
                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }

}