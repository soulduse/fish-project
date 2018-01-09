package com.dave.fish.ui.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dave.fish.R
import com.dave.fish.ui.main.MainActivity
import com.dave.fish.util.DLog
import kotlinx.android.synthetic.main.fragment_web.*

/**
 * Created by soul on 2017. 12. 3..
 */
class WebFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_web, container, false)

    private val goBackHandler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                1->{
                    webViewGoback()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webview_weather.apply {
            webViewClient = CustomWebViewClient()
            settings.javaScriptEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            loadUrl(arguments?.getString("url"))
        }

        webview_weather.setOnKeyListener(View.OnKeyListener { _, keyCode, keyEvent ->
            if(keyEvent.action != KeyEvent.ACTION_DOWN)
                return@OnKeyListener true

            if(keyCode == KeyEvent.KEYCODE_BACK){
                if(webview_weather.canGoBack()){
                    DLog.w("canGoBack")
                    webview_weather.goBack()
                }else{
                    DLog.w("canNotGoBack")
                    (activity as MainActivity).onBackPressed()
                }
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        fab_reload.setOnClickListener {
            webview_weather.reload()
        }
    }

    private fun webViewGoback() {
        DLog.w("webViewGoback")
        webview_weather.goBack()
    }

    inner class CustomWebViewClient : WebViewClient(){
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
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