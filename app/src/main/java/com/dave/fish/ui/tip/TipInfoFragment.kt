package com.dave.fish.ui.tip

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.common.extensions.ViewDrawExt
import com.dave.fish.common.firebase.FirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.fragment_tip_info.*


/**
 * Created by soul on 2018. 2. 16..
 * 해루질 기본 정보를 제공
 * TODO 유튜브, 해루질 장비, 방법
 */
class TipInfoFragment : Fragment() {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tip_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context

        FirestoreProvider.instance.read("tip_info", {
            it?.forEach {
                drawInfoView(it)
            }
        })
    }

    private fun drawInfoView(item: DocumentSnapshot){
        when(item.id){
            mContext.resources.getString(R.string.tip_info_info),
            mContext.resources.getString(R.string.tip_info_warm),
            mContext.resources.getString(R.string.tip_info_method) -> addViewToRoot(ViewDrawExt.drawBasicView(mContext, item.data))
            mContext.resources.getString(R.string.tip_info_detail) -> addViewToRoot(ViewDrawExt.drawDetailView(this, item.data))
        }
    }

    private fun addViewToRoot(view: View){
        tip_info_container.addView(view)
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