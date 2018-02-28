package com.dave.fish.ui.chat

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import kotlinx.android.synthetic.main.fragment_chatting.*

/**
 * Created by soul on 2018. 2. 28..
 */
class FragmentChat: Fragment() {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_chatting, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context
    }

    companion object {
        fun newInstance(): FragmentChat {
            val fragmemt = FragmentChat()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}