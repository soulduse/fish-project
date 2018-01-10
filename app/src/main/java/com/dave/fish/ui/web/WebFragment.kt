package com.dave.fish.ui.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.dave.fish.R
import com.dave.fish.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_web.*

/**
 * Created by soul on 2017. 12. 3..
 */
class WebFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_web, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(webview_weather){
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            isFocusable = true
            isFocusableInTouchMode = true
            loadUrl(arguments?.getString("url"))

            setOnKeyListener(View.OnKeyListener { _, keyCode, keyEvent ->
                when{
                    keyEvent.action != KeyEvent.ACTION_DOWN -> return@OnKeyListener true

                    keyCode == KeyEvent.KEYCODE_BACK ->{
                        backPressed()
                        return@OnKeyListener true
                    }

                    else -> return@OnKeyListener false
                }
            })
        }

        fab_reload.setOnClickListener {
            webview_weather.reload()
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