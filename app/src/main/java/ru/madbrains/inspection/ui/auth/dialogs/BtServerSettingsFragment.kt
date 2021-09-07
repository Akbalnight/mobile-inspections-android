package ru.madbrains.inspection.ui.auth.dialogs

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_bt_server_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseBottomSheetDialogFragment

class BtServerSettingsFragment :
    BaseBottomSheetDialogFragment(R.layout.fragment_bt_server_settings) {

    private val serverSettingsViewModel: BtServerSettingsViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        etServerPortal.append(serverSettingsViewModel.currentApiUrl)
        etServerAuth.append(serverSettingsViewModel.currentAuthUrl)

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        btnApply.setOnClickListener {
            serverSettingsViewModel.applyClick(
                etServerAuth.text.toString(),
                etServerPortal.text.toString()
            )
            this.dismiss()
        }
    }
}