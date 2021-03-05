package ru.madbrains.inspection.ui.common.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.main.MainActivity
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
class WebViewFragment : BaseFragment(R.layout.fragment_web_view) {

    companion object {
        const val KEY_TOOLBAR_TITLE = "toolbar_title"
        const val KEY_WEB_URL = "web_url"
    }

    private val webViewViewModel: WebViewViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireNotNull(arguments).run {
            setupToolbar(getString(KEY_TOOLBAR_TITLE))
            setupWebView(getString(KEY_WEB_URL))
        }

        webViewViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })
        webViewViewModel.navigateToMain.observe(viewLifecycleOwner, EventObserver {
            startMainActivity()
        })
    }

    private fun setupToolbar(title: String?) {
        title?.let { toolbarLayout.tvTitle.text = it }
        toolbarLayout.btnLeading.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupWebView(startUrl: String?) {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                allowContentAccess = true
            }
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressView?.changeVisibility(true)

                    val authCode = Uri.parse(url).getQueryParameter("code")
                    Timber.tag("WebViewLog").d("loading url: $url")
                    authCode?.let {
                        Timber.tag("WebViewLog").d("authCode: $authCode")
                        webViewViewModel.getToken(it)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressView?.changeVisibility(false)
                }
            }
            loadUrl(startUrl)
        }
    }

    private fun startMainActivity() {
        MainActivity.start(requireContext())
        requireActivity().finish()
    }
}