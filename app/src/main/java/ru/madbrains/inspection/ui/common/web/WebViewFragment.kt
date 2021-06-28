package ru.madbrains.inspection.ui.common.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.data.network.ApiData
import ru.madbrains.data.network.OAuthData
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
        private const val INTERFACE = "HTML_OUT"
    }

    private var loadingStarted = false

    private val jsInjectCode = """
        function parseForm(event) {
            var form = this;
            if (this.tagName.toLowerCase() != 'form')
                form = this.form;
            let username = '';
            let password = '';
            if (!form.method) form.method = 'get';
            var inputs = document.forms[0].getElementsByTagName('input');
            for (var i = 0; i < inputs.length; i++) {
                var field = inputs[i];
                if (field.type != 'submit' && field.type != 'reset' && field.type != 'button'){
                    if(field.name == 'username'){
                        username = field.value;
                    }
                    if(field.name == 'password'){
                        password = field.value;
                    }
                
                }
            }
            ${INTERFACE}.processFormData(username, password);
        }
        
        for (var form_idx = 0; form_idx < document.forms.length; ++form_idx)
            document.forms[form_idx].addEventListener('submit', parseForm, false);
        var inputs = document.getElementsByTagName('input');
        for (var i = 0; i < inputs.length; i++) {
            if (inputs[i].getAttribute('type') == 'button')
                inputs[i].addEventListener('click', parseForm, false);
        }
    """

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
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun processFormData(username: String, password: String) {
                        webViewViewModel.setFormData(username, password)
                    }
                }, INTERFACE)
            }
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    val authCode = Uri.parse(url).getQueryParameter("code")
                    authCode?.let {
                        Timber.tag("WebViewLog").d("authCode: $authCode")
                        webViewViewModel.getToken(it)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressView?.changeVisibility(false)
                    view?.loadUrl("javascript:(function() { $jsInjectCode })()")
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    if(!loadingStarted){
                        loadingStarted = true
                        progressView?.changeVisibility(true)
                    }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                }
            }
            loadUrl(startUrl)

           Timber.d("auth server: ${OAuthData.oauthUrl}")
           Timber.d("portal server: ${ApiData.apiUrl}")
        }
    }

    private fun startMainActivity() {
        MainActivity.start(requireContext())
        requireActivity().finish()
    }
}
