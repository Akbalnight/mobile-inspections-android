package ru.madbrains.inspection.ui.main.checkpoints.list

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_checkpoint_group_list.*
import kotlinx.android.synthetic.main.fragment_checkpoint_group_list.progressView
import kotlinx.android.synthetic.main.fragment_checkpoint_group_list.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.CheckpointAdapter
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.view.SearchToolbar

class CheckpointGroupListFragment : BaseFragment(R.layout.fragment_checkpoint_group_list) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val checkpointListViewModel: CheckpointListViewModel by sharedViewModel()
    private val checkpointAdapter by lazy {
        CheckpointAdapter(click = {
                checkpointListViewModel.checkpointSelectGroupClick(it)
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvList.adapter = checkpointAdapter

        checkpointListViewModel.checkPointGroupList.observe(viewLifecycleOwner, Observer {
            checkpointAdapter.items = it
        })
        checkpointListViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        checkpointListViewModel.navigateToNextRoute.observe(
            viewLifecycleOwner,
            EventObserver { data ->
                findNavController().navigate(
                    R.id.action_checkpointGroupListFragment_to_checkpointListFragment)
            })

        checkpointListViewModel.getCheckpoints()

        setupToolbar()
    }

    private fun setupToolbar() {
        (toolbarLayout as SearchToolbar).apply {
            tvTitle.text = strings[R.string.fragment_checkpoint_group_list_title]
            btnLeading.setOnClickListener {
                mainViewModel.menuClick()
            }
            setupSearch(context.drawables[R.drawable.ic_menu], {})
        }
    }
}