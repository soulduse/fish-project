package com.dave.fish.common

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.dave.fish.R
import kotlinx.android.synthetic.main.dialog_pick_tide.*


/**
 * Created by soul on 2017. 12. 16..
 */
class PickTideDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater = activity.layoutInflater
        val rootView = inflater.inflate(R.layout.dialog_pick_tide, null)

        val spinnerLoc = rootView.findViewById<Spinner>(R.id.spinner_loc_pick) as Spinner
        val spinnerMap = rootView.findViewById<Spinner>(R.id.spinner_map_pick) as Spinner

        val arrayAdapter1 = ArrayAdapter(activity, R.layout.spinner_item, arrayOf("제주특별자치도","2","3"))
        val arrayAdapter2 = ArrayAdapter(context, R.layout.spinner_item, arrayOf("부산항신항","2","3"))
        spinnerLoc.adapter = arrayAdapter1
        spinnerMap.adapter = arrayAdapter2
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton("설정", DialogInterface.OnClickListener { _, _ ->
                    // sign in the user ...
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { _, _ -> this.dismiss() })
        return builder.create()
    }
}