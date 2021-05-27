package ru.madbrains.inspection.ui.main.defects.defectlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_list.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.domain.model.DefectStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DefectListAdapter
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailFragment

class DefectListFragment : BaseFragment(R.layout.fragment_defect_list) {

    companion object {
        const val KEY_EQUIPMENTS_IDS_DEFECT_LIST = "KEY_EQUIPMENTS_IDS_DEFECT_LIST"
        const val KEY_IS_EDIT_CONFIRM_MODE = "KEY_IS_EDIT_CONFIRM_MODE"
        const val KEY_IS_EDITABLE = "KEY_IS_EDITABLE"
    }

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val defectListViewModel: DefectListViewModel by viewModel()

    private val defectsAdapter by lazy {
        DefectListAdapter(
            onEditClick = {
                val defect = defectListViewModel.defectListModels.find { defectModel ->
                    defectModel.id == it.id
                }
                defectListViewModel.editDefect(defect)
            },
            onDeleteClick = {
                val defect = defectListViewModel.defectListModels.find { defectModel ->
                    defectModel.id == it.id
                }
                showDialogDeleteDefect(defect)
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

        val deviceIds = arguments?.getStringArrayList(KEY_EQUIPMENTS_IDS_DEFECT_LIST)
        defectListViewModel.isEditConfirmMode = arguments?.getBoolean(KEY_IS_EDIT_CONFIRM_MODE, false)?:false
        defectListViewModel.isEditable = arguments?.getBoolean(KEY_IS_EDITABLE, false)?:false
        if(defectListViewModel.isEditConfirmMode){
            setupViewingDefects()
        } else{
            setupRegisterDefects()
        }

        setupRVList()

        defectListViewModel.getDefectList(deviceIds)

        defectListViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        defectListViewModel.navigateToEditDefect.observe(viewLifecycleOwner, EventObserver {
            val args = bundleOf(
                DefectDetailFragment.KEY_DETAIL_DEFECT to it
            )
            findNavController().navigate(R.id.action_defectListFragment_to_detailFragment, args)
        })

        defectListViewModel.navigateToConfirmDefect.observe(viewLifecycleOwner, EventObserver {
            val args = bundleOf(
                DefectDetailFragment.KEY_DETAIL_DEFECT to it,
                DefectDetailFragment.KEY_DEFECT_TARGET_STATUS to DefectStatus.CONFIRMED
            )
            findNavController().navigate(R.id.action_defectListFragment_to_detailFragment, args)
        })

    }

    private fun setupRegisterDefects() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_list_title]
            btnMenu.setOnClickListener {
                mainViewModel.menuClick()
            }
        }

        fabAddDefect.setOnClickListener {
            findNavController().navigate(R.id.action_defectListFragment_to_detailFragment)
        }

    }

    private fun setupViewingDefects() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_edit_title]
            btnMenu.setImageResource(R.drawable.ic_back)
            btnMenu.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        fabAddDefect.visibility = View.GONE
    }

    private fun setupRVList() {
        rvDefectList.adapter = defectsAdapter

        defectListViewModel.defectList.observe(viewLifecycleOwner, Observer {
            defectsAdapter.items = it
        })
    }

    private fun showDialogDeleteDefect(item: DefectModel?) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(strings[R.string.fragment_defect_dialog_delete_defect_subtitle])
                setPositiveButton(strings[R.string.fragment_add_dialog_btn_delete],
                    DialogInterface.OnClickListener { _, _ ->
                        defectListViewModel.deleteDefect(item)
                    })
                setNegativeButton(strings[R.string.fragment_dialog_btn_cancel],
                    DialogInterface.OnClickListener { _, _ ->
                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }


    private fun showDialogEliminatedDefect(item: DefectModel?) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(strings[R.string.fragment_defect_dialog_eliminated_defect_title])
                setMessage(strings[R.string.fragment_defect_dialog_eliminated_defect_subtitle])
                setPositiveButton(strings[R.string.save],
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