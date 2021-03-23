package ru.madbrains.inspection.ui.main.defects.defectlist

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_list.*
import kotlinx.android.synthetic.main.fragment_defect_list.toolbarLayout
import kotlinx.android.synthetic.main.fragment_defect_list.progressView
import kotlinx.android.synthetic.main.toolbar_with_menu.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DefectListAdapter
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailFragment
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment

class DefectListFragment : BaseFragment(R.layout.fragment_defect_list) {

    companion object {
        const val KEY_EQUIPMENTS_IDS_DEFECT_LIST = "device_id_defect_list_fragment"
        const val KEY_IS_FILTER_DEFECT_LIST = "is_filter_defect_list_fragment"
    }

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val defectListViewModel: DefectListViewModel by viewModel()

    private val defectsAdapter by lazy {
        DefectListAdapter(
                onLeftActionClick = {
                    val defect = defectListViewModel.defectListModels.find { detourModel ->
                        detourModel.id == it.id
                    }
                    defectListViewModel.defectClick(defect)
                },
                onRightActionClick = {

                }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val deviceIds = arguments?.getStringArrayList(DefectListFragment.KEY_EQUIPMENTS_IDS_DEFECT_LIST)
        val isFilterList = arguments?.getBoolean(DefectListFragment.KEY_IS_FILTER_DEFECT_LIST, false)
        isFilterList?.let {
            if (it) {
                setupViewingDefects()
            } else {
                setupRegisterDefects()
            }
        } ?: run {
            setupRegisterDefects()
        }

        setupRVList()

        defectListViewModel.getDefectList(deviceIds)

        defectListViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        defectListViewModel.navigateToDefect.observe(viewLifecycleOwner, EventObserver {
            val args = bundleOf(
                    DefectDetailFragment.KEY_DETAIL_DEFECT to it
            )
            findNavController().navigate(R.id.action_defectListFragment_to_detailFragment, args)
        })

        setupClickListener()
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

    private fun setupClickListener() {

    }
}