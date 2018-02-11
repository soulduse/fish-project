package com.dave.fish.ui.map.record

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.db.model.LocationModel
import com.dave.fish.ui.map.GeoUtil
import com.dave.fish.ui.map.detail.DetailMapActivity
import com.dave.fish.util.DLog
import com.dave.fish.util.DateUtil
import kotlinx.android.synthetic.main.record_item.view.*

/**
 * Created by soul on 2018. 2. 9..
 */
class RecordAdapter : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    private var items: MutableList<LocationModel> = mutableListOf()

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.record_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.itemView?.run {

            with(items[position]) {
                if (locations != null && locations!!.isNotEmpty()) {
                    val location = locations!!.first()
                    val address = GeoUtil.getAddress(location.latitude, location.longtitude)

                    tv_title.text = address.split(" ").filterNot { it.isEmpty() }.first()
                    tv_address.text = address
                    tv_recorded_time.text = (
                            DateUtil.getDate(createdAt) + " ~ " + DateUtil.getDate(updatedAt) +
                                    "\n(${DateUtil.getSubtractMin(createdAt, updatedAt)}분 기록됨)"
                            )

                    goto_mapview.setOnClickListener {
                        DLog.w("clicked id value --> $id, address = $address")

                        val detailMapIntent = Intent(context, DetailMapActivity::class.java).apply {
                            putExtra(Constants.EXTRA_LOCATION_MODEL_IDX, id)
                            putExtra(Constants.EXTRA_LOCATION_LAT, location.latitude)
                            putExtra(Constants.EXTRA_LOCATION_LON, location.longtitude)
                        }
                        context.startActivity(detailMapIntent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(items: List<LocationModel>) {
        this.items = items.toMutableList()
    }

    fun clearItems() {
        this.items.clear()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
