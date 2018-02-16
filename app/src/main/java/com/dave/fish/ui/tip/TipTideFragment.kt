package com.dave.fish.ui.tip

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R


/**
 * Created by soul on 2018. 2. 16..
 * 물때표 보는법을 알려준다.
 */
class TipTideFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_tip_tide, container, false)

        return rootView
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