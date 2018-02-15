package com.dave.fish.ui.map.record

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dave.fish.R
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.LocationModel
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_record_detail.*

/**
 * Created by soul on 2018. 2. 8..
 */
class RecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)

        initToolbar()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        record_recycler_view.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@RecordActivity)
            addItemDecoration(RecyclerViewDecoration(25))
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onResume() {
        super.onResume()

        val savedLocation = RealmProvider.instance.findAllData(LocationModel::class.java) as RealmResults<LocationModel>

        if (savedLocation.isEmpty()) {
            showEmptyRecord()
        } else {
            hideEmptyRecord()
        }

        record_recycler_view.adapter = RecordAdapter(savedLocation.sort("id", Sort.DESCENDING))
    }

    private fun showEmptyRecord() {
        containerEmptyRecordMessage.visibility = View.VISIBLE
    }

    private fun hideEmptyRecord() {
        containerEmptyRecordMessage.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
