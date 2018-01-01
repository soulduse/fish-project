package com.dave.fish.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dave.fish.R
import kotlinx.android.synthetic.main.fragment_web.*

/**
 * Created by soul on 2017. 12. 3..
 */
class FragmentWeb : BaseFragment() {

    override fun getContentId(): Int = R.layout.fragment_web

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                1->{
                    webViewGoback()
                }
            }
        }
    }

    private fun webViewGoback() {
        webview_weather.goBack()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews(savedInstanceState: Bundle?) {
        webview_weather.webViewClient = CustomWebViewClient()
        webview_weather.settings.javaScriptEnabled = true
        webview_weather.isFocusable = true
        webview_weather.isFocusableInTouchMode = true
        webview_weather.loadUrl(arguments?.getString("url"))
        webview_weather.setOnKeyListener(View.OnKeyListener { _, keyCode, keyEvent ->
            if(keyCode == KeyEvent.KEYCODE_BACK &&
                    keyEvent?.action == MotionEvent.ACTION_UP &&
                    webview_weather.canGoBack()){
                handler.sendEmptyMessage(1)
                return@OnKeyListener true
            }
            false
        })

        fab_reload.setOnClickListener {
            webview_weather.reload()
        }
    }


    override fun initData() {

    }

    inner class CustomWebViewClient : WebViewClient(){
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }
    }

    companion object {
        fun newInstance() : FragmentWeb {
            val fragmemt = FragmentWeb()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}