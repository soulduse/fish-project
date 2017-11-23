package com.dave.fish.view.activity

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.model.realm.SelectItemModel
import com.dave.fish.view.adapter.ViewPagerAdapter
import com.dave.fish.view.fragment.FragmentCalendar
import com.dave.fish.view.fragment.FragmentMap
import com.dave.fish.view.menu.DrawerAdapter
import com.dave.fish.view.menu.MenuDrawer
import com.dave.fish.view.menu.SimpleItem
import com.dave.fish.view.menu.SpaceItem
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), DrawerAdapter.OnItemSelectedListener{

    private lateinit var mRealmController : RealmController
    private var firstSpinnerPosition = 0
    private var selectedSpinner : SelectItemModel = SelectItemModel()

    private var screenTitles: Array<String> = arrayOf()
    private var screenIcons: Array<Drawable?> = arrayOf()

    private lateinit var slidingRootNav: SlidingRootNav

    private lateinit var toolbarParams :AppBarLayout.LayoutParams
    private lateinit var toolbarLayoutParams : AppBarLayout.LayoutParams

    private lateinit var fragmentCalendar : FragmentCalendar
    private lateinit var fragmentMap : FragmentMap

    override fun getContentId(): Int = R.layout.activity_main

    override fun initViews() {
        initRealm()
        initFragments()
        initToolbar()
        initSlidingMenu()
        initSpinner()
        initViewPager()
    }

    override fun initData() {

    }

    private fun initRealm() {
        mRealmController = RealmController.instance
        mRealmController.setListener(realmListener)
    }

    private fun initFragments() {
        fragmentCalendar = FragmentCalendar()
        fragmentMap = FragmentMap()
    }

    private fun initToolbar(){
        setSupportActionBar(toolbar)
        toolbarParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams = toolbar_layout.layoutParams as AppBarLayout.LayoutParams
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

    private fun initSpinner() {
        spinner_loc.onItemSelectedListener = spinnerListener
        spinner_map.onItemSelectedListener = spinnerListener

        setSpinnerAdapter(spinner_loc, mRealmController.getSpinnerItems(realm))
        selectedSpinner = mRealmController.findSelectedSpinnerItem(realm)
        spinner_loc.setSelection(selectedSpinner.firstPosition, false)
    }

    private fun initViewPager() {
        main_viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when(position){
                    PAGE_CALENDAR -> setScrollAble(true)
                    PAGE_MAP_RECORD -> setScrollAble(false)
                }
            }
        })
        // 스크롤 되게 하기 위해 해당 값을 true 로 해줘야한다.
        nest_scrollview.isFillViewport = true
        main_viewpager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(fragmentCalendar)
            addFragment(fragmentMap)
        }
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
            in PAGE_CALENDAR .. PAGE_MAP_RECORD -> {
                main_viewpager.currentItem = position
            }
            else -> Toast.makeText(applicationContext, "개발 진행중인 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setScrollAble(isAble : Boolean){
        if(isAble){
            fragmentCalendar.setVisibleNavigationCalendar(View.VISIBLE)
            enableScroll()
        }else{
            fragmentCalendar.setVisibleNavigationCalendar(View.GONE)
            unableScroll()
        }

        toolbar.requestLayout()
        toolbar_layout.requestLayout()
    }

    private fun unableScroll(){
        toolbarParams.scrollFlags = 0
        toolbarLayoutParams.scrollFlags = 0
    }

    private fun enableScroll(){
        toolbarParams.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        toolbarLayoutParams.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
    }

    private fun setSpinnerAdapter(spinner : Spinner, items : List<String>){
        val spinnerArrayAdapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_none_arrow_item)
        spinner.adapter = spinnerArrayAdapter
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
                    selectedSpinner.let {
                        if(selectedSpinner.doNm == spinner_loc.selectedItem.toString()){
                            spinner_map.setSelection(selectedSpinner.secondPosition)
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

    override fun onBackPressed() {
        if(slidingRootNav.isMenuClosed){
            slidingRootNav.openMenu()
        }else{
            super.onBackPressed()
        }
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
        private var firstExecute = true
        private val PAGE_CALENDAR = 0
        private val PAGE_MAP_RECORD = 1
    }
}
