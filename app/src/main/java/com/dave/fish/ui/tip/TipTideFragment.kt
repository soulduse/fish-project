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
 * 물때표 보는법을 알려준다.
 */
class TipTideFragment : Fragment() {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tip_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context

        FirestoreProvider.instance.read("tip_tide", {
            it?.first()?.let {
                drawInfoView(it)
            }
        })
    }

    private fun drawInfoView(item: DocumentSnapshot){
        val title = item["title"] as String
        val tips = item["tips"] as ArrayList<String>

        addViewToRoot(ViewDrawExt.drawTitle(mContext, title))

        tips.forEachIndexed { index, s ->
            if(index % 2 == 0){
                addViewToRoot(ViewDrawExt.drawImage(mContext, s))
            } else { // 홀수 = 설명
                addViewToRoot(ViewDrawExt.drawText(mContext, s))
            }
        }
    }

    private fun addViewToRoot(view: View){
        tip_info_container.addView(view)
    }

    companion object {
        fun newInstance(): TipTideFragment {
            val fragment = TipTideFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}