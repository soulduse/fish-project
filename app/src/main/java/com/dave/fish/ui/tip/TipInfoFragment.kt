package com.dave.fish.ui.tip

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dave.fish.R
import com.dave.fish.common.firebase.FirestoreProvider
import com.dave.fish.ui.GlideApp
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.fragment_tip_info.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.support.v4.UI


/**
 * Created by soul on 2018. 2. 16..
 * 해루질 기본 정보를 제공
 * 유튜브, 해루질 장비, 방법
 */
class TipInfoFragment : Fragment() {

    private lateinit var mContext: Context

    private val glideApp by lazy { GlideApp.with(this@TipInfoFragment) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tip_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context

        FirestoreProvider.instance.read("tip_info", {
            it?.forEach {
//                val id = it.id
//                val data = it.data

                drawInfoView(it)
            }
        })
    }

    private fun drawInfoView(item: DocumentSnapshot){
        when(item.id){
            resources.getString(R.string.tip_info_info) -> { addViewToRoot(drawBasicView(item.data)) }
            resources.getString(R.string.tip_info_detail) -> { addViewToRoot(drawDetailView(item.data))}
            resources.getString(R.string.tip_info_warm) -> { addViewToRoot(drawBasicView(item.data)) }
            resources.getString(R.string.tip_info_method) -> { addViewToRoot(drawBasicView(item.data)) }
        }
    }

    private fun addViewToRoot(view: View){
        tip_info_container.addView(view)
    }

    private fun drawBasicView(map: Map<String, Any>): View{
        return UI {
            verticalLayout {
                textView{
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 26F
                    text = replacePrettyStr(map["title"] as String)
                }

                textView{
                    text = replacePrettyStr(map["content"] as String)
                    singleLine = false
                }
            }
        }.view
    }

    private fun drawDetailView(map: Map<String, Any>): View{
        return UI {
            verticalLayout {
                textView{
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 26F
                    text = replacePrettyStr(map["title"] as String)
                }

                textView{
                    text = replacePrettyStr(map["content"] as String)
                    singleLine = false
                }

                flexboxLayout{
                    flexWrap = FlexWrap.WRAP
                    alignItems = AlignItems.CENTER
                    val images = map["images"] as List<String>

                    images.forEach {
                        imageView().show(it)
                    }
                }
            }
        }.view
    }

    private fun ImageView.show(imageUrl: String = "") {
        if (imageUrl.isBlank()) return

        if (context == null) return

        if (context is Activity && ((context as Activity).isFinishing || (context as Activity).isDestroyed)) return

        glideApp.load(imageUrl)
                .apply { RequestOptions().override(10, 10) }
                .into(this)
    }


    private fun ViewManager.flexboxLayout(init: (@AnkoViewDslMarker FlexboxLayout).() -> Unit): FlexboxLayout =
            ankoView(::FlexboxLayout, theme = 0) { init() }


    private fun replacePrettyStr(str: String): String{
        return str.replace("\\n", "\n")
                .replace("\\t", "\t")
    }

    companion object {
        fun newInstance(): TipInfoFragment {
            val fragment = TipInfoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}