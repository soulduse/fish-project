package com.dave.fish.ui.map.record

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerViewDecoration(private val divHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect?.bottom = divHeight
    }
}
