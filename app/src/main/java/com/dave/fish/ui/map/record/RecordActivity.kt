package com.dave.fish.ui.map.record

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.dave.fish.R
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.LocationModel
import kotlinx.android.synthetic.main.activity_record_detail.*

/**
 * Created by soul on 2018. 2. 8..
 */
class RecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)

        val savedLocation = RealmProvider.instance.findData(LocationModel::class.java) as List<LocationModel>

        record_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@RecordActivity)
            addItemDecoration(RecyclerViewDecoration(25))
            adapter = RecordAdapter().apply { setItems(savedLocation) }
        }
    }
}
