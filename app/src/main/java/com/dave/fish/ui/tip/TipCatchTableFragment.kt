package com.dave.fish.ui.tip

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R


/**
 * Created by soul on 2018. 2. 16..
 * 월별 조과표를 보여준다.
 */
class TipCatchTableFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_tip_catch_table, container, false)

        return rootView
    }

    companion object {
        fun newInstance(): TipCatchTableFragment {
            val fragment = TipCatchTableFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}