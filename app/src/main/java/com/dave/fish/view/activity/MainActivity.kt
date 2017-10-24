package com.dave.fish.view.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.model.realm.SelectItemModel
import com.dave.fish.network.RetrofitController
import com.dave.fish.view.adapter.ViewPagerAdapter
import com.dave.fish.view.fragment.FragmentCalendar
import com.dave.fish.view.fragment.FragmentMap
import com.dave.fish.view.menu.*
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener{
    private lateinit var realm: Realm
    private lateinit var mRealmController : RealmController
    private var firstSpinnerPosition = 0
    private var selectedSpinner : SelectItemModel?= null

    private var screenTitles: Array<String> = arrayOf()
    private var screenIcons: Array<Drawable?> = arrayOf()

    private lateinit var slidingRootNav: SlidingRootNav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        init()
    }

    private fun init(){
        initSlidingMenu()
        initRealm()
        initSpinner()
        initViewPager()
    }

    private fun initSlidingMenu(){
        slidingRootNav = SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menu_left_drawer)
                .withToolbarMenuToggle(toolbar)
                .withDragDistance(140) //Horizontal translation of a view. Default == 180dp
                .withRootViewScale(0.85f) //Content view's scale will be interpolated between 1f and 0.7f. Default == 0.65f;
                .withRootViewElevation(10) //Content view's elevation will be interpolated between 0 and 10dp. Default == 8.
                .withRootViewYTranslation(4) //Content view's translationY will be interpolated between 0 and 4. Default == 0
                .withMenuOpened(true)
                .inject()


        screenIcons = loadScreenIcons()
        screenTitles = loadScreenTitles()

        val menuAdapter = DrawerAdapter(arrayListOf(
                createItemFor(MenuDrawer.INFO.position).setChecked(true),
                createItemFor(MenuDrawer.MAP.position),
                SpaceItem(18),
                createItemFor(MenuDrawer.CATCH.position),
                createItemFor(MenuDrawer.CHAT.position),
                createItemFor(MenuDrawer.ALARM.position),
                SpaceItem(18),
                createItemFor(MenuDrawer.MAIL.position),
                createItemFor(MenuDrawer.SHARE.position)))

        menuAdapter.setListener(this)
        val list = findViewById<RecyclerView>(R.id.menu_drawer_list)
        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = menuAdapter

        menuAdapter.setSelected(MenuDrawer.INFO.position)
    }

    private fun createItemFor(position: Int): SimpleItem {
        return SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent))
    }

    private fun loadScreenTitles(): Array<String> {
        return resources.getStringArray(R.array.drawer_menu_titles)
    }

    private fun loadScreenIcons(): Array<Drawable?> {
        val ta = resources.obtainTypedArray(R.array.drawer_menu_images)
        val icons = arrayOfNulls<Drawable>(ta.length())
        for (i in 0 until ta.length()) {
            val id = ta.getResourceId(i, 0)
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id)
            }
        }
        ta.recycle()
        return icons
    }

    @ColorInt
    private fun color(@ColorRes res: Int): Int {
        return ContextCompat.getColor(this, res)
    }

    override fun onItemSelected(position: Int) {
        slidingRootNav.closeMenu()

        when(position){
            in 0..1 -> main_viewpager.currentItem = position
            else -> Toast.makeText(applicationContext, "개발 진행중인 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRealm() {
        realm = Realm.getDefaultInstance()
        mRealmController = RealmController.instance
        mRealmController.setListener(realmListener)
    }

    private fun initSpinner() {
        if (isEmptyRealmSpinner()) {
            initRealmForSpinner()
        } else {
            setSpinnerAdapter(spinner_loc, mRealmController.getSpinnerItems(realm))
        }

        spinner_loc.onItemSelectedListener = spinnerListener
        spinner_map.onItemSelectedListener = spinnerListener

        selectedSpinner = mRealmController.findSelectedSpinnerItem(realm)
        selectedSpinner?.let {
            spinner_loc.setSelection(selectedSpinner?.firstPosition!!)
        }
    }

    private fun setSpinnerAdapter(spinner : Spinner, items : List<String>){
        val spinnerArrayAdapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_none_arrow_item)
        spinner.adapter = spinnerArrayAdapter
    }

    private fun initViewPager() {
        // 스크롤 되게 하기 위해 해당 값을 true 로 해줘야한다.
        nest_scrollview.isFillViewport = true
        main_viewpager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(FragmentCalendar())
            addFragment(FragmentMap())
        }
    }

    var spinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val spinner = parent as Spinner

            when(spinner){
                spinner_loc ->{
                    setSpinnerAdapter(spinner_map, mRealmController.getSelectedSpinnerItem(realm, spinner_loc.selectedItem.toString())!!)
                    firstSpinnerPosition = pos
                    selectedSpinner?.let {
                        if(selectedSpinner?.doNm == spinner_loc.selectedItem.toString()){
                            spinner_map.setSelection(selectedSpinner?.secondPosition!!)
                        }else{
                            spinner_map.setSelection(0)
                        }
                    }
                }

                spinner_map ->{
                    mRealmController
                            .setSelectedSpinnerItem(
                                    realm,
                                    spinner_loc.selectedItem.toString(),
                                    spinner_map.selectedItem.toString(),
                                    firstSpinnerPosition,
                                    pos
                            )
                }
            }
        }
    }

    private fun isEmptyRealmSpinner(): Boolean {
        if (mRealmController.getSpinnerItems(realm).isEmpty()) {
            return true
        }

        return false
    }

    private fun initRealmForSpinner() {
        RetrofitController
                .instance
                .getGisData()
                .subscribe({ gisModel ->
                    var dataList = gisModel.data
                    var gisMap = dataList?.let {
                        dataList.groupBy {
                            it.doNm
                        }
                    }

                    Log.d(TAG, """
                            map data -->
                            size : ${gisMap?.size}"
                            keys : ${gisMap?.keys}"
                            values : ${gisMap?.values}"
                            """)
                    mRealmController.setSpinner(realm, gisMap!!)

                }, { e ->
                    Log.e(TAG, "result API response ===> error ${e.localizedMessage}")
                })
    }

    private val realmListener = object : RealmListener{
        override fun onSpinnerSuccess() {
            setSpinnerAdapter(spinner_loc, RealmController.instance.getSpinnerItems(realm))
        }

        override fun onTransactionSuccess() {
            if(!firstExecute){
                main_viewpager.adapter.notifyDataSetChanged()
            }
            firstExecute = false
        }
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
        private var firstExecute = true
    }
}
