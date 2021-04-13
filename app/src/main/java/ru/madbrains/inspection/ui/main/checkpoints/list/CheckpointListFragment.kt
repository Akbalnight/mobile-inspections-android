package ru.madbrains.inspection.ui.main.checkpoints.list

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_checkpoint_list.rvList
import kotlinx.android.synthetic.main.fragment_checkpoint_list.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.CheckpointGroupModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.ui.adapters.CheckpointAdapter
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.checkpoints.detail.CheckpointDetailFragment.Companion.KEY_CHECKPOINT_DETAIL_DATA
import ru.madbrains.inspection.ui.view.SearchToolbar

class CheckpointListFragment : BaseFragment(R.layout.fragment_checkpoint_list) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val checkpointListViewModel: CheckpointListViewModel by viewModel()
    private val checkpointAdapter by lazy {
        CheckpointAdapter(click = {
            checkpointListViewModel.checkpointSelectClick(it)
        })
    }

    companion object{
        const val KEY_CHECKPOINT_DATA = "KEY_CHECKPOINT_DATA"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvList.adapter = checkpointAdapter

        checkpointListViewModel.checkPointList.observe(viewLifecycleOwner, Observer {
            checkpointAdapter.items = it
        })

        checkpointListViewModel.navigateToNextRoute.observe(
            viewLifecycleOwner,
            EventObserver { data ->
                val args = bundleOf(
                    KEY_CHECKPOINT_DETAIL_DATA to data
                )
                findNavController().navigate(
                    R.id.action_checkpointListFragment_to_checkpointDetailFragment,
                    args
                )
            })

        requireNotNull(arguments).run {
            val routeDataModel = (getSerializable(KEY_CHECKPOINT_DATA) as? CheckpointGroupModel)
            routeDataModel?.let {
                checkpointListViewModel.setRawData(it)
                setupToolbar(it.parentName)
            }
        }
    }

    private fun setupToolbar(parentName: String?) {
        (toolbarLayout as SearchToolbar).apply {
            tvTitle.text = parentName
            btnLeading.setOnClickListener {
                findNavController().popBackStack()
            }
            setupSearch(context.drawables[R.drawable.ic_back], {})
        }
    }
}