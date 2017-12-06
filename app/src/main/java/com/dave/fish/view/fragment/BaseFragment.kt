package com.dave.fish.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm

/**
 * Created by soul on 2017. 11. 13..
 */
abstract class BaseFragment : Fragment() {

    protected lateinit var realm : Realm

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

    abstract fun getContentId(): Int

    abstract fun initViews(savedInstanceState: Bundle?)

    abstract fun initData()
}