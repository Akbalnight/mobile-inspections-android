package ru.madbrains.inspection.ui.main.sync

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_sync.*
import kotlinx.android.synthetic.main.toolbar_with_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel

class SyncFragment : BaseFragment(R.layout.fragment_sync) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val syncViewModel: SyncViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_sync_title]
            btnMenu.setOnClickListener {
                mainViewModel.menuClick()
            }
        }
    }
}