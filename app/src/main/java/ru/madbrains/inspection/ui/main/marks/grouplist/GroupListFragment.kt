package ru.madbrains.inspection.ui.main.marks.grouplist

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_group_list.*
import kotlinx.android.synthetic.main.toolbar_with_menu_and_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel

class GroupListFragment : BaseFragment(R.layout.fragment_group_list) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val groupListViewModel: GroupListViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_group_list_title]
            btnMenu.setOnClickListener {
                mainViewModel.menuClick()
            }
            btnSearch.setOnClickListener {
                // TODO add click action
            }
        }
    }
}