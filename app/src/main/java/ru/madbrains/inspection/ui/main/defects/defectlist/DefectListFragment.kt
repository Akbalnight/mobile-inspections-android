package ru.madbrains.inspection.ui.main.defects.defectlist

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_list.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel

class DefectListFragment : BaseFragment(R.layout.fragment_defect_list) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val defectListViewModel: DefectListViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
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
}