package ru.madbrains.inspection.ui.main.equipmentList

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_equipment_list.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.EquipmentListAdapter
import ru.madbrains.inspection.ui.main.MainViewModel

class EquipmentListFragment : BaseFragment(R.layout.fragment_equipment_list) {
    companion object {
        const val KEY_EQUIPMENT_LIST_DATA = "KEY_EQUIPMENT_LIST_DATA"
    }

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val equipmentListViewModel: EquipmentListViewModel by viewModel()

    private val equipmentAdapter by lazy {
        EquipmentListAdapter(onEquipmentClick = {})
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()

        requireNotNull(arguments).run {
            val data = getSerializable(KEY_EQUIPMENT_LIST_DATA) as List<*>?
            data?.filterIsInstance<EquipmentModel>()?.takeIf { it.size == data.size }?.let {
                equipmentListViewModel.setEquipmentList(it)
            }
        }

        equipmentListViewModel.equipmentList.observe(viewLifecycleOwner, Observer {
            rvEquipmentList.adapter = equipmentAdapter.apply {
                items = it
            }
        })
    }

    private fun setupToolbar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_equipment_list_title]
            btnMenu.setOnClickListener {
                mainViewModel.menuClick()
            }
        }
    }
}