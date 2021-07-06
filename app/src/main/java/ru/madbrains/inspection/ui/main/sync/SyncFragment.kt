package ru.madbrains.inspection.ui.main.sync

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_sync.*
import kotlinx.android.synthetic.main.toolbar_with_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.data.extensions.toyyyyMMddHHmm
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.clickWithDebounce
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DetourAdapter
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.SyncViewModel

class SyncFragment : BaseFragment(R.layout.fragment_sync) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val syncViewModel: SyncViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        DetourAdapter(
            onDetourClick = {}
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()

        rvRoutes.adapter = routesAdapter

        syncViewModel.changedItems.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it
            val sendDataAvailable = it.isNotEmpty()
            llBottomSendData.isClickable = sendDataAvailable
            llBottomSendData.isFocusable = sendDataAvailable
            llBottomSendData.alpha = if (sendDataAvailable) 1.0f else 0.5f
        })

        llBottomGetData.clickWithDebounce {
            syncViewModel.startSync()
        }
        llBottomSendData.clickWithDebounce {
            syncViewModel.startSendingData()
        }

        syncViewModel.syncInfo.observe(viewLifecycleOwner, Observer {
            tvGetDate.text = it.getDate?.toyyyyMMddHHmm() ?: "-"
            tvSendDate.text = it.sendDate?.toyyyyMMddHHmm() ?: "-"
        })
    }

    override fun onResume() {
        super.onResume()
        syncViewModel.getChangedDetours()
    }

    private fun setupToolbar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.sync]
            btnMenu.setOnClickListener {
                mainViewModel.menuClick()
            }
        }
    }
}