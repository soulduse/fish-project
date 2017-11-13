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

    protected val realm : Realm = Realm.getDefaultInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContentId(), container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onLoadStart(savedInstanceState)
        onLoadContent()
    }

    override fun onStop() {
        realm.close()
        super.onStop()
    }

    abstract fun getContentId(): Int

    abstract fun onLoadStart(savedInstanceState: Bundle?)

    abstract fun onLoadContent()
}