package com.dave.fish.ui.map.record

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.db.model.LocationModel
import kotlinx.android.synthetic.main.record_item.view.*

/**
 * Created by soul on 2018. 2. 9..
 */
class RecordAdapter : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    private var items: MutableList<LocationModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.itemView?.run {
            tv_title.text = "타이틀"
            tv_address.text = "주소주소"
            tv_recorded_time.text = "2시간 50분"
            goto_mapview.text = "지도보기"
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(items: List<LocationModel>) {
        this.items = items.toMutableList()
    }

    fun clearItems(){
        this.items.clear()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
