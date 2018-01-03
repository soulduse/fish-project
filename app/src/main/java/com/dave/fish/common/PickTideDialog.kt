package com.dave.fish.common

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.dave.fish.R
import com.dave.fish.api.model.SidePanelData
import com.dave.fish.ui.CustomAreasSpinner


/**
 * Created by soul on 2017. 12. 16..
 */
class PickTideDialog : DialogFragment() {

    private var mListener : (values: SidePanelData)->Unit = {}

    fun initDialog(event : (values: SidePanelData)->Unit){
        mListener = event
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity?.layoutInflater
        val rootView = inflater?.inflate(R.layout.dialog_pick_tide, null)

        val customAreasSpinner = rootView?.findViewById<CustomAreasSpinner>(R.id.custom_areas_spinner).apply {
            this?.init(true)
        }

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton("설정", DialogInterface.OnClickListener { _, _ ->
                    customAreasSpinner?.getPickedValueOfTide(mListener)
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { _, _ ->
                    this.dismiss()
                })
        return builder.create()
    }
}