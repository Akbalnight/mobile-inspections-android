package ru.madbrains.inspection.ui.main

import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.fragment_lock_screen.*
import kotlinx.android.synthetic.main.toolbar_empty.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings

class LockScreenFragment : BaseFragment(R.layout.fragment_lock_screen) {

    private val lockScreenViewModel: LockScreenViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvTitle.text = strings[R.string.auth]
        btnLogin.setOnClickListener {
            lockScreenViewModel.login(tvUsername.text.toString(), tvPassword.text.toString())
        }
        btnLogout.setOnClickListener {
            lockScreenViewModel.logout()
        }
        tvUsername.addTextChangedListener {
            tvValidationError.isInvisible = true
        }
        tvPassword.addTextChangedListener {
            tvValidationError.isInvisible = true
        }
        lockScreenViewModel.showError.observe(viewLifecycleOwner, EventObserver {
            tvValidationError.text = strings[it]
            tvValidationError.isInvisible = false
        })
    }
}