package com.dave.fish.common.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.dave.fish.R
import com.dave.fish.ui.GlideApp
import org.jetbrains.anko.*

/**
 * Created by soul on 2018. 2. 16..
 */

object ViewDrawExt {

    fun drawImage(context: Context, imageUrls: Array<String>): View {
        return context.UI {
            verticalLayout {
                imageUrls.forEach {
                    imageView().wideShow(context, it)
                }
            }
        }.view
    }

    fun drawImage(context: Context, imageUrl: String): View {
        return context.UI {
            verticalLayout {
                imageView().wideShow(context, imageUrl)
            }
        }.view
    }

    fun drawTitle(context: Context, contents: String): View {
        return context.UI {
            verticalLayout {
                textView {
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 26F
                    text = replacePrettyStr(contents)
                }.lparams {
                    margin = dip(15)
                }
            }
        }.view
    }

    fun drawText(context: Context, contents: String): View {
        return context.UI {
            verticalLayout {
                textView {
                    text = replacePrettyStr(contents)
                    singleLine = false
                }.lparams {
                    margin = dip(15)
                }
            }
        }.view
    }

    fun drawBasicView(context: Context, map: Map<String, Any>): View {
        return context.UI {
            verticalLayout {
                textView {
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 26F
                    text = replacePrettyStr(map["title"] as String)
                }.lparams {
                    margin = dip(15)
                }

                textView {
                    text = replacePrettyStr(map["content"] as String)
                    singleLine = false
                }.lparams {
                    margin = dip(15)
                }
            }
        }.view
    }

    fun drawCaughtHistoryView(context: Context, map: Map<String, Any>): View {
        return context.UI {
            verticalLayout {
                textView {
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 26F
                    text = "월별 조과현황"
                }.lparams {
                    margin = dip(15)
                }

                tableLayout {
                    tableRow {
                        backgroundColor = context.resources.getColor(R.color.chart_marker_background)
                        textView {
                            text = "월"
                            typeface = Typeface.DEFAULT_BOLD
                            textSize = 22F
                        }
                        textView {
                            text = "조과물"
                            typeface = Typeface.DEFAULT_BOLD
                            textSize = 22F
                        }
                    }

                    val caughtHistories = map["history"] as List<String>
                    caughtHistories.forEachIndexed { index, s ->
                        tableRow {
                            background = context.resources.getDrawable(R.drawable.spinner_divider)
                            textView {
                                text = "${index + 1}월"
                                textSize = 15F
                            }.lparams { marginEnd = dip(10) }
                            textView {
                                text = s
                                textSize = 15F
                            }
                        }
                    }
                }.lparams {
                    margin = dip(15)
                }

                textView {
                    text = replacePrettyStr(map["notice"] as String)
                    textSize = 15F
                }.lparams {
                    marginStart = dip(15)
                    marginEnd = dip(15)
                    topMargin = dip(5)
                    bottomMargin = dip(10)
                }
            }
        }.view
    }

    fun drawDetailView(context: Context, map: Map<String, Any>): View {
        return context.UI {
            verticalLayout {
                textView {
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 26F
                    text = replacePrettyStr(map["title"] as String)
                }.lparams {
                    margin = dip(15)
                }

                textView {
                    text = replacePrettyStr(map["content"] as String)
                    singleLine = false
                }.lparams {
                    margin = dip(15)
                }

                val images = map["image"] as String
                imageView().wideShow(context, images)
            }
        }.view
    }

    @SuppressLint("CheckResult")
    private fun ImageView.show(context: Context?, imageUrl: String = "") {
        if (imageUrl.isBlank()) return

        if (context == null) return

        if (context is Activity && ((context as Activity).isFinishing || (context as Activity).isDestroyed)) return

        this.maxWidth = 350
        this.maxHeight = 350
        this.minimumWidth = 350
        this.minimumHeight = 350

        GlideApp.with(context)
                .load(imageUrl)
                .apply { RequestOptions().centerCrop() }
                .into(this)
    }

    private fun ImageView.wideShow(context: Context?, imageUrl: String = "") {
        if (imageUrl.isBlank()) return

        if (context == null) return

        if (context is Activity && ((context as Activity).isFinishing || (context as Activity).isDestroyed)) return

        GlideApp.with(context)
                .load(imageUrl)
                .apply { RequestOptions().centerCrop() }
                .into(this)
    }

    private fun replacePrettyStr(str: String): String {
        return str.replace("\\n", "\n")
                .replace("\\t", "\t")
    }
}
