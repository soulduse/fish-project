package com.dave.fish.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Spinner
import com.dave.fish.R

/**
 * Created by soul on 2017. 12. 17..
 */
class CustomTideSpinners : ConstraintLayout {

    var bg : ConstraintLayout ?= null
    var spinnerLoc : Spinner ?= null
    var spinnerMap : Spinner ?= null

    constructor(context : Context) : super(context){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, difStyle: Int) : super(context, attrs){
        initView()
        getAttrs(attrs, difStyle)
    }

    private fun initView(){
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val li = context.getSystemService(infService) as LayoutInflater
        val v = li.inflate(R.layout.areas_spinner, this, false)
        addView(v)

        bg = findViewById<ConstraintLayout>(R.id.bg) as ConstraintLayout
        spinnerLoc = findViewById<Spinner>(R.id.spinner_loc) as Spinner
        spinnerMap = findViewById<Spinner>(R.id.spinner_map) as Spinner
    }

    private fun getAttrs(attrs: AttributeSet){

    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int){

    }
}