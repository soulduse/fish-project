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
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.fragment_web.view.*

/**
 * Created by soul on 2017. 12. 3..
 */
class WebFragment : Fragment() {

    private lateinit var mFrameLayout: FrameLayout

    private lateinit var mWebView: TouchWebView

    private lateinit var mProgressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val rootView = inflater.inflate(R.layout.fragment_web, container, false)

        mFrameLayout = rootView.web_container

        mWebView = TouchWebView(rootView.context)

        mProgressBar = rootView.webview_progressbar

        rootView.web_container.addView(mWebView)

        return rootView
    }



    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()
        refreshWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(mWebView) {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.setAppCacheEnabled(true)
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

            isFocusable = true
            isFocusableInTouchMode = true

            webViewClient = CustomWebViewClient(mProgressBar)
            webChromeClient = object: WebChromeClient(){
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    mProgressBar.progress = newProgress
                }
            }
            loadUrl(arguments?.getString(Constants.BUNDLE_FRAGMENT_URL))

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
            mWebView.reload()
        }
    }

    class CustomWebViewClient(private val progressBar: ProgressBar): WebViewClient(){

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }

    private fun backPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack()
        } else {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun onDestroy() {
        mFrameLayout.removeAllViews()
        mWebView.destroy()
        super.onDestroy()
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
