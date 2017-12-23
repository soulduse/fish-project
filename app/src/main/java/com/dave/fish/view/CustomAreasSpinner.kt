package com.dave.fish.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.model.realm.SelectItemModel
import com.dave.fish.model.retrofit.SidePanelData
import com.dave.fish.network.RetrofitController
import io.realm.Realm

/**
 * Created by soul on 2017. 12. 17..
 */
class CustomAreasSpinner : ConstraintLayout {

    private lateinit var bg : ConstraintLayout
    private lateinit var spinnerLoc : Spinner
    private lateinit var spinnerMap : Spinner

    private var selectedSpinner : SelectItemModel = SelectItemModel()
    private var firstSpinnerPosition = 0
    private lateinit var mRealmController : RealmController
    private lateinit var realm : Realm
    private var isTodayTide: Boolean = false

    constructor(context : Context) : super(context){
        initRealm()
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initRealm()
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, difStyle: Int) : super(context, attrs){
        initRealm()
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

        initSpinner()
    }

    private fun getAttrs(attrs: AttributeSet){

    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int){

    }

    private fun initRealm() {
        realm = Realm.getDefaultInstance()
        mRealmController = RealmController.instance
        mRealmController.setListener(realmListener)
    }

    private fun initSpinner() {
        spinnerLoc.onItemSelectedListener = spinnerListener
        spinnerMap.onItemSelectedListener = spinnerListener

        setSpinnerAdapter(spinnerLoc, mRealmController.getSpinnerItems(realm))
        selectedSpinner = mRealmController.findSelectedSpinnerItem(realm, isTodayTide)
        spinnerLoc.setSelection(selectedSpinner.firstPosition, false)
    }

    private fun setSpinnerAdapter(spinner : Spinner, items : List<String>){
        val spinnerArrayAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_none_arrow_item)
        spinner.adapter = spinnerArrayAdapter
    }

    private val realmListener = object : RealmListener {
        override fun onSpinnerSuccess() {
            setSpinnerAdapter(spinnerLoc, RealmController.instance.getSpinnerItems(realm))
        }

        override fun onTransactionSuccess() {
//            if(!MainActivity.firstExecute){
//                main_viewpager.adapter.notifyDataSetChanged()
//            }
//            MainActivity.firstExecute = false
        }
    }

    fun setIsTodayTide(isTodayTide: Boolean){
        this.isTodayTide = isTodayTide
    }

    private var spinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val spinner = parent as Spinner

            when(spinner){
                spinnerLoc ->{
                    setSpinnerAdapter(spinnerMap, mRealmController.getSelectedSpinnerItem(realm, spinnerLoc.selectedItem.toString())!!)
                    firstSpinnerPosition = pos
                    selectedSpinner.let {
                        if(selectedSpinner.doNm == spinnerLoc.selectedItem.toString()){
                            spinnerMap.setSelection(selectedSpinner.secondPosition)
                        }else{
                            spinnerMap.setSelection(0)
                        }
                    }
                }

                spinnerMap->{
                    mRealmController
                            .setSelectedSpinnerItem(
                                    realm,
                                    spinnerLoc.selectedItem.toString(),
                                    spinnerMap.selectedItem.toString(),
                                    firstSpinnerPosition,
                                    pos,
                                    isTodayTide
                            )
                }
            }
        }
    }

    fun getPickedValueOfTide(event : (values: SidePanelData)->Unit){
        val secondSpinnerItem = mRealmController.findSelectedSecondModel(realm, isTodayTide)
        val postId = secondSpinnerItem.obsPostId

        postId.let {
            val panelData = RetrofitController.instance.getSidePanelData(postId)
            panelData.subscribe ({ response ->
                val tideList = response.data
                if(tideList.isNotEmpty()){
                    event(tideList.first())
                }
            },{
                Toast.makeText(context, "데이터를 읽어오는데 실패하였습니다.\n다시 시도해주세요.", Toast.LENGTH_LONG).show()
            })
        }
    }

    fun closeRealm(){
        if(!realm.isClosed){
            realm.close()
        }
    }
}