package ru.madbrains.inspection.ui.main.checkpoints.list

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_checkpoint_list.*
import kotlinx.android.synthetic.main.toolbar_with_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.view.SearchToolbar

class CheckpointListFragment : BaseFragment(R.layout.fragment_checkpoint_list) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val checkpointListViewModel: CheckpointListViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        (toolbarLayout as SearchToolbar).apply {
            tvTitle.text = strings[R.string.fragment_group_list_title]
            btnLeading.setOnClickListener {
                mainViewModel.menuClick()
            }
            setupSearch(context.drawables[R.drawable.ic_back], {})
        }
    }
}