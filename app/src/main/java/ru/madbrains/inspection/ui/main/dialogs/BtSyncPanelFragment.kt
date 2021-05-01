package ru.madbrains.inspection.ui.main.dialogs

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_bt_sync_panel.*
import kotlinx.android.synthetic.main.toolbar_sync_panel.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseBottomSheetDialogFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.base.ProgressState
import ru.madbrains.inspection.ui.main.SyncViewModel

class BtSyncPanelFragment : BaseBottomSheetDialogFragment(R.layout.fragment_bt_sync_panel) {
    private val syncViewModel: SyncViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnCancel.setOnClickListener {
            syncViewModel.cancelSync()
            this.dismiss()
        }

        btnApply.setOnClickListener {
            syncViewModel.applySyncToDb()
            this.dismiss()
        }

        syncViewModel.defectsSyncStatus.observe(this, EventObserver {
            defectsStatusView.setState(it)
        })
        syncViewModel.detourSyncStatus.observe(this, EventObserver {
            detourStatusView.setState(it)
        })
        syncViewModel.mediaSyncStatus.observe(this, EventObserver {
            mediaStatusView.setState(it)
        })
        syncViewModel.docSyncStatus.observe(this, EventObserver {
            docStatusView.setState(it)
        })
        syncViewModel.etcSyncStatus.observe(this, EventObserver {
            etcStatusView.setState(it)
        })
        syncViewModel.allSyncProgress.observe(this, EventObserver {
            syncProgressBar.isIndeterminate = it == ProgressState.PROGRESS
            syncProgressBar.isEnabled = it == ProgressState.PROGRESS
            btnApply.isEnabled = it == ProgressState.DONE
        })
    }
}