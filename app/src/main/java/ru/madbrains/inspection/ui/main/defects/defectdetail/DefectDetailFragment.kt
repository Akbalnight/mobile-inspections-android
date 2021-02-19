package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import kotlinx.android.synthetic.main.fragment_defect_detail.*
import kotlinx.android.synthetic.main.fragment_defect_list.*
import kotlinx.android.synthetic.main.fragment_defect_list.toolbarLayout
import kotlinx.android.synthetic.main.fragment_route_list.*
import kotlinx.android.synthetic.main.toolbar_with_back.*
import kotlinx.android.synthetic.main.toolbar_with_back.btnBack
import kotlinx.android.synthetic.main.toolbar_with_back.tvTitle
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.RouteAdapter
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsFragment

class DefectDetailFragment : BaseFragment(R.layout.fragment_defect_detail) {

    private val defectTypicalAdapter by lazy {
        DefectTypicalListAdapter(
            context = context,
            layoutResource = R.layout.item_defect_typical,
            textViewResourceId = R.id.tvName
        )
    }

    private val defectDetailViewModel: DefectDetailViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            setupEditDefect()
        } ?: run {
            setupNewDefect()
        }

        setupDefectTypical()

        layoutDropDownDevice.setEndIconOnClickListener {
            openDeviceSelect()
        }
        dropDownDevice.setOnClickListener {
            openDeviceSelect()
        }

    }

    private fun setupNewDefect() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_add_title]
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupEditDefect() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_edit_title]
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupDefectTypical() {
        defectDetailViewModel.getDefectTypicalList()

        dropDownTypeDefect.setAdapter(defectTypicalAdapter)
        defectDetailViewModel.defectTypicalList.observe(viewLifecycleOwner, Observer {
            defectTypicalAdapter.addItems(it)
        })

        dropDownTypeDefect.setOnItemClickListener { _, _, position, _ ->
            val item = defectTypicalAdapter.getItem(position)
            defectDetailViewModel.changeCurrentDefectTypical(item)
            dropDownTypeDefect.setText(item.name, false)
        }
    }

    private fun openDeviceSelect(){
        findNavController().navigate(R.id.action_defectDetailFragment_to_deviceSelectListFragment)
    }
}