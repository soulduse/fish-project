package com.dave.fish.common.extensions

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import org.jetbrains.anko.custom.ankoView

/**
 * Created by soul on 2018. 2. 16..
 */
inline fun ViewManager.flexboxLayout(theme: Int = 0, init: FlexboxLayout.() -> Unit) = ankoView(::FlexboxLayout, theme, init)
inline fun ViewManager.swipeRefreshLayout(theme: Int = 0, init: SwipeRefreshLayout.() -> Unit) = ankoView(::SwipeRefreshLayout, theme, init)
inline fun ViewManager.textView(theme: Int = 0, init: TextView.() -> Unit) = ankoView(::TextView, theme, init)
inline fun ViewManager.imageView(theme: Int = 0, init: ImageView.() -> Unit) = ankoView(::ImageView, theme, init)
inline fun ViewManager.view(theme: Int = 0, init: View.() -> Unit) = ankoView(::View, theme, init)
inline fun ViewManager.button(theme: Int = 0, init: Button.() -> Unit) = ankoView(::Button, theme, init)