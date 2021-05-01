package ru.madbrains.inspection.ui.main.settings

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.toolbar_with_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
        setupNumberPicker()
        setupSaveButton()
    }

    private fun setupNumberPicker() {
        npSaveInfoDuration.minValue = 1
        npSaveInfoDuration.maxValue = 5
        npSaveInfoDuration.value = settingsViewModel.saveInfoDuration
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            settingsViewModel.saveInfoDuration = npSaveInfoDuration.value
        }
    }

    private fun setupToolbar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_settings_title]
            btnMenu.setOnClickListener {
                mainViewModel.menuClick()
            }
        }

    }
}