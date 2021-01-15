package ru.madbrains.inspection.ui.auth

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_authorization.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.common.WebViewFragment

class AuthorizationFragment : BaseFragment(R.layout.fragment_authorization) {

    private val authorizationViewModel: AuthorizationViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()

        btnAuth.setOnClickListener {
            authorizationViewModel.authClick()
        }
        btnServerSettings.setOnClickListener {
            authorizationViewModel.serverSettingsClick()
        }

        authorizationViewModel.navigateToAuth.observe(viewLifecycleOwner, EventObserver { url ->
            openAuthWebPage(url)
        })
        authorizationViewModel.navigateToServerSettings.observe(viewLifecycleOwner, EventObserver {
            openServerSettings()
        })
    }

    private fun setupToolbar() {
        toolbarLayout.tvTitle.text = strings[R.string.fragment_auth_title]
        toolbarLayout.btnClose.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun openAuthWebPage(url: String) {
        val args = bundleOf(
            WebViewFragment.KEY_TOOLBAR_TITLE to strings[R.string.fragment_web_view_title_auth],
            WebViewFragment.KEY_WEB_URL to url
        )
        findNavController().navigate(R.id.action_authorizationFragment_to_webViewFragment, args)
    }

    private fun openServerSettings() {
        // TODO add navigation
    }
}