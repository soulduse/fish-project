package com.dave.fish.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.MyApplication
import io.realm.Realm

/**
 * Created by soul on 2017. 11. 13..
 */
abstract class BaseFragment : Fragment() {

    protected lateinit var realm : Realm
    protected var mContext : Context? = MyApplication.context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        realm = Realm.getDefaultInstance()
        return inflater.inflate(getContentId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(savedInstanceState)
        initData()
    }

    override fun onDestroyView() {
        realm.close()
        super.onDestroyView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    abstract fun getContentId(): Int

    abstract fun initViews(savedInstanceState: Bundle?)

    abstract fun initData()
}