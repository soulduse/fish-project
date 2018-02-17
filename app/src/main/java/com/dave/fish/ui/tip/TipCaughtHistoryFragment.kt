package com.dave.fish.ui.tip

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.common.extensions.TipDrawExt
import com.dave.fish.common.firebase.FirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.fragment_tip_catch_table.*


/**
 * Created by soul on 2018. 2. 16..
 * 월별 조과표를 보여준다.
 */
class TipCaughtHistoryFragment : Fragment() {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tip_catch_table, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context

        FirestoreProvider.instance.read("tip_catch", {
            it?.forEach {
                drawInfoView(it)
            }
        })
    }

    private fun drawInfoView(item: DocumentSnapshot){
        when(item.id){
            resources.getString(R.string.tip_catch_history_month) -> addViewToRoot(TipDrawExt.drawCaughtHistoryView(mContext, item.data))
        }
    }

    private fun addViewToRoot(view: View){
        tip_caught_history_container.addView(view)
    }

    companion object {
        fun newInstance(): TipCaughtHistoryFragment {
            val fragment = TipCaughtHistoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}