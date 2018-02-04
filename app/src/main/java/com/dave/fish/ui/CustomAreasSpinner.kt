package com.dave.fish.ui

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
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.api.model.SidePanelData
import com.dave.fish.api.model.SidePanelModel
import com.dave.fish.common.Constants
import com.dave.fish.db.RealmListener
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.SelectItemModel
import com.dave.fish.db.model.SpinnerSecondModel
import com.dave.fish.util.DLog
import kotlinx.android.synthetic.main.areas_spinner.view.*

/**
 * Created by soul on 2017. 12. 17..
 */
class CustomAreasSpinner : ConstraintLayout {

    private var keyTide: Int = Constants.KEY_TIDE_MAIN_SPINNER
    private var listenerChangedSpinner : ()-> Unit = {}

    constructor(context : Context) : super(context){
        initRealm()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initRealm()
    }


    fun init(keyTide: Int) : CustomAreasSpinner {
        this.keyTide = keyTide
        initView()
        return this
    }

    private fun initView(){
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val li = context.getSystemService(infService) as LayoutInflater
        val v = li.inflate(R.layout.areas_spinner, this, false)
        addView(v)
        initSpinner()
    }


    private fun initRealm() {
        RealmProvider.instance.setListener(realmListener)
    }

    private fun initSpinner() {
        spinner_loc.onItemSelectedListener = spinnerListener
        spinner_map.onItemSelectedListener = spinnerListener

        val firstNames = RealmProvider.instance.getSpinner().map { it.areaName }

        setSpinnerAdapter(spinner_loc, firstNames)

        RealmProvider.instance.getSelectedItem(keyTide).let {
            val firstPosition = getFirstSpinnerPosition(it)
            spinner_loc.setSelection(firstPosition)
        }
    }



    private fun getFirstSpinnerPosition(selectedSpinner: SelectItemModel) =
            RealmProvider.instance.getSpinner().indexOfFirst { it.areaName == selectedSpinner.doNm }

    private fun getSecondSpinnerPosition(selectedSpinner: SelectItemModel) =
            getSecondSpinner(selectedSpinner.doNm)
                    .indexOfFirst { it.obsPostName == selectedSpinner.postName }

    private fun setSpinnerAdapter(spinner : Spinner, items : List<String>){
        val spinnerArrayAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_none_arrow_item)
        spinner.adapter = spinnerArrayAdapter
    }

    private val realmListener = object : RealmListener {
        override fun onSpinnerSuccess() {
            DLog.w("success spinner!")
        }

        override fun onTransactionSuccess(listener: ()->Unit) {
            DLog.w("onTransactionSuccess")
            listener()
        }
    }

    private fun getSecondSpinner(areaName: String): List<SpinnerSecondModel>{
        return RealmProvider.instance.getSpinner()
                .filter { it.areaName == areaName }
                .map { it.secondSpinnerItems }
                .first()
                .toList()
    }

    fun initListener(changed : ()-> Unit){
        listenerChangedSpinner = changed
    }

    private var spinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val spinner = parent as Spinner

            when(spinner){
                spinner_loc ->{
                    val selectedDoNm = spinner_loc.selectedItem.toString()
                    setSpinnerAdapter(spinner_map, getSecondSpinner(selectedDoNm).map { it.obsPostName })

                    RealmProvider.instance.getSelectedItem(keyTide).let{
                        if(it.doNm == selectedDoNm){
                            val secondPosition = getSecondSpinnerPosition(it)
                            spinner_map.setSelection(secondPosition)
                            return
                        }

                        spinner_map.setSelection(0)
                    }
                }

                spinner_map->{
                    RealmProvider.instance
                            .setSelectSpinner(
                                    spinner_loc.selectedItem.toString(),
                                    spinner_map.selectedItem.toString(),
                                    keyTide,
                                    listenerChangedSpinner
                            )
                }
            }
        }
    }

    fun getPickedValueOfTide(event : (values: SidePanelData)->Unit){

            val postId: String ?= RealmProvider.instance.getSecondSpinnerItem(keyTide)?.obsPostId

            postId?.let {
                Network.request(ApiProvider.provideTideApi().getSidePanelData(postId),
                        NetworkCallback<SidePanelModel>().apply {
                            success = { sidePanelModel->
                                val tideList = sidePanelModel.data
                                if(tideList.isNotEmpty()){
                                    event(tideList.first())
                                }
                            }

                            error = {
                                Toast.makeText(context, "데이터를 읽어오는데 실패하였습니다.\n다시 시도해주세요.", Toast.LENGTH_LONG).show()
                            }
                        })
            }
    }
}
