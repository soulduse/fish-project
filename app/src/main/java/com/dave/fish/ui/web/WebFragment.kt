package com.dave.fish.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.dave.fish.R
import com.dave.fish.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_web.*

/**
 * Created by soul on 2017. 12. 3..
 */
class WebFragment : Fragment() {

    private val progressBar by lazy { webview_progressbar }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_web, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()
        refreshWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(webview_weather) {
            settings.javaScriptEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            webViewClient = CustomWebViewClient(progressBar)
            webChromeClient = object: WebChromeClient(){
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    webview_progressbar.progress = newProgress
                }
            }
            loadUrl(arguments?.getString("url"))

            setOnKeyListener(View.OnKeyListener { _, keyCode, keyEvent ->
                when {
                    keyEvent.action != KeyEvent.ACTION_DOWN -> return@OnKeyListener true

                    keyCode == KeyEvent.KEYCODE_BACK -> {
                        backPressed()
                        return@OnKeyListener true
                    }

                    else -> return@OnKeyListener false
                }
            })
        }
    }

    private fun refreshWebView() {
        fab_reload.setOnClickListener {
            webview_weather.reload()
        }
    }

    class CustomWebViewClient(private val progressBar: ProgressBar): WebViewClient(){

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun backPressed() {
        if (webview_weather.canGoBack()) {
            webview_weather.goBack()
        } else {
            (activity as MainActivity).onBackPressed()
        }
    }

    companion object {
        fun newInstance() : WebFragment {
            val fragmemt = WebFragment()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}