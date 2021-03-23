package ru.madbrains.inspection.ui.main.equipment

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_equipment.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.equipment.tabs.EquipmentTabCommonDataFragment
import ru.madbrains.inspection.ui.main.equipment.tabs.EquipmentTabDefectsFragment
import ru.madbrains.inspection.ui.main.equipment.tabs.EquipmentTabDocumentsFragment


class EquipmentFragment : BaseFragment(R.layout.fragment_equipment) {

    companion object {
        const val KEY_EQUIPMENT_DATA = "KEY_EQUIPMENT_DATA"
    }

    private val equipmentViewModel: EquipmentViewModel by sharedViewModel()
    private lateinit var tabAdapter: EquipmentTabAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireNotNull(arguments).run {
            val data = (getSerializable(KEY_EQUIPMENT_DATA) as? EquipmentModel)
            data?.let {
                equipmentViewModel.setEquipmentData(it)
                setupToolbar(it.name)
                setupViewPager()
                observeData()
            }
        }
    }

    private fun observeData() {
        equipmentViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        equipmentViewModel.commonError.observe(viewLifecycleOwner, EventObserver {
            showErrorToast()
        })
    }

    private fun showErrorToast() {
        Toast.makeText(
            activity, strings[R.string.error],
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setupToolbar(equipmentName: String?) {
        toolbarLayout.apply {
            tvTitle.text = equipmentName
            btnLeading.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupViewPager() {
        val fragments = listOf(
            EquipmentTabCommonDataFragment(),
            EquipmentTabDefectsFragment(),
            EquipmentTabDocumentsFragment()
        )

        routesViewPager.isUserInputEnabled = false

        tabAdapter = EquipmentTabAdapter(this).apply {
            setItems(fragments)
            routesViewPager.adapter = this
        }

        TabLayoutMediator(routesTabLayout, routesViewPager) { tab, position ->
            tab.text = when (fragments[position]) {
                is EquipmentTabCommonDataFragment -> strings[R.string.equipmentCommonData]
                is EquipmentTabDefectsFragment -> strings[R.string.equipmentDefects]
                is EquipmentTabDocumentsFragment -> strings[R.string.equipmentDocuments]
                else -> ""
            }
            routesViewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}