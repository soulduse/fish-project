package com.dave.fish.view.activity

import android.os.Bundle
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
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.common.PickTideDialog
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.model.realm.SelectItemModel
import com.dave.fish.model.retrofit.SidePanelData
import com.dave.fish.util.DLog
import com.dave.fish.view.CustomAreasSpinner
import com.dave.fish.view.adapter.ViewPagerAdapter
import com.dave.fish.view.fragment.FragmentAlarm
import com.dave.fish.view.fragment.FragmentCalendar
import com.dave.fish.view.fragment.FragmentMap
import com.dave.fish.view.fragment.FragmentWeb
import com.dave.fish.view.menu.DrawerAdapter
import com.dave.fish.view.menu.MenuDrawer
import com.dave.fish.view.menu.SimpleItem
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_left_drawer.*

class MainActivity : BaseActivity(), DrawerAdapter.OnItemSelectedListener{

    private lateinit var slidingRootNav: SlidingRootNav

    private lateinit var toolbarParams :AppBarLayout.LayoutParams
    private lateinit var toolbarLayoutParams : AppBarLayout.LayoutParams

    // fragments
    private lateinit var fragmentCalendar : FragmentCalendar
    private lateinit var fragmentMap : FragmentMap
    private lateinit var fragmentKma : FragmentWeb
    private lateinit var fragmentMarinKma : FragmentWeb
    private lateinit var fragmentWindyty : FragmentWeb
    private lateinit var fragmentAlarm : FragmentAlarm

    // dialog
    private lateinit var pickTideDialog :PickTideDialog

    override fun getContentId(): Int = R.layout.activity_main

    override fun initViews() {
        initFragments()
        initToolbar()
        initSlidingMenu()
        initViewPager()
        initPickTide()
        initSpinner()
    }



    override fun initData() {

    }

    private fun initFragments() {
        fragmentCalendar = FragmentCalendar.newInstance()
        fragmentMap = FragmentMap.newInstance()
        fragmentKma = FragmentWeb.newInstance()
        fragmentKma.arguments = Bundle().apply { putString("url", Constants.KMA_M_URL) }
        fragmentMarinKma = FragmentWeb.newInstance()
        fragmentMarinKma.arguments = Bundle().apply { putString("url", Constants.MARIN_KMA_M_URL) }
        fragmentWindyty = FragmentWeb.newInstance()
        fragmentWindyty.arguments = Bundle().apply { putString("url", Constants.WINDYTY_M_URL) }
        fragmentAlarm = FragmentAlarm.newInstance()
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

        val menuList = arrayListOf(
                createItemFor(MenuDrawer.INFO).setChecked(true),
                createItemFor(MenuDrawer.MAP),
                createItemFor(MenuDrawer.KMA),
                createItemFor(MenuDrawer.MARIN_KMA),
                createItemFor(MenuDrawer.CATCH),
                createItemFor(MenuDrawer.CHAT),
                createItemFor(MenuDrawer.ALARM),
                createItemFor(MenuDrawer.MAIL),
                createItemFor(MenuDrawer.SHARE))

        val menuAdapter = DrawerAdapter(menuList)

        menuAdapter.setListener(this)
        val list = findViewById<RecyclerView>(R.id.menu_drawer_list)
        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = menuAdapter

        menuAdapter.setSelected(0)
    }

    private fun initViewPager() {
        main_viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when(position){
                    PAGE_CALENDAR -> {
                        visibleCollapsingToolbar()
                        setScrollAble(true)
                    }
                    PAGE_MAP_RECORD->{
                        visibleCollapsingToolbar()
                        setScrollAble(false)
                    }
                    else->{
                        goneCollapsingToolbar()
                        setScrollAble(false)
                    }
                }
            }
        })
        // 스크롤 되게 하기 위해 해당 값을 true 로 해줘야한다.
        nest_scrollview.isFillViewport = true
        main_viewpager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(fragmentCalendar)
            addFragment(fragmentMap)
            addFragment(fragmentKma)
            addFragment(fragmentMarinKma)
            addFragment(fragmentWindyty)
            addFragment(fragmentAlarm)
        }
    }

    private fun createItemFor(menuDrawer: MenuDrawer): SimpleItem {
        return SimpleItem(ContextCompat.getDrawable(this, menuDrawer.icon), menuDrawer.title)
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent))
    }

    private fun initPickTide(){
        val mListener : (values: SidePanelData)->Unit = {
            val tideList = arrayListOf(it.lvl1, it.lvl2, it.lvl3, it.lvl4)
            DLog.w(tideList.toString())
            tv_pick_tide_name.text = applicationContext.resources.getString(R.string.today_tide, it.title)
            tv_pick_tide_values.text = tideList.joinToString ("\n")
        }

        CustomAreasSpinner(this).apply {
            init(true)
            getPickedValueOfTide {
                it.run(mListener)
            }
        }

        pickTideDialog = PickTideDialog().apply {
            initDialog({
                it.run(mListener)
            })
        }
        tv_pick_tide_name.setOnClickListener(setOnClickPickTide)
        tv_pick_tide_values.setOnClickListener(setOnClickPickTide)
    }

    private fun initSpinner(){
        val customAreasSpinner = findViewById<CustomAreasSpinner>(R.id.main_spinners).apply {
            init(false)
        }

        customAreasSpinner.initListener {
//            if(!firstExecute){
            DLog.w("customAreasSpinner.initListener")
                main_viewpager.adapter.notifyDataSetChanged()
//            }
//            firstExecute = false
        }
    }

    private val setOnClickPickTide = View.OnClickListener {
        pickTideDialog.show(supportFragmentManager, "pickTide")
    }

    @ColorInt
    private fun color(@ColorRes res: Int): Int = ContextCompat.getColor(this, res)

    override fun onItemSelected(position: Int) {
        slidingRootNav.closeMenu()
        DLog.w("onItemSelected --> $position")
        when(position){
            in 0 .. 1 -> {
                visibleCollapsingToolbar()
                main_viewpager.currentItem = position
            }

            else -> {
                goneCollapsingToolbar()
                main_viewpager.currentItem = position
//                Toast.makeText(applicationContext, "개발 진행중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goneCollapsingToolbar(){
        if(toolbar_layout.visibility == View.VISIBLE){
            toolbar_layout.visibility = View.GONE
        }
    }

    private fun visibleCollapsingToolbar(){
        if(toolbar_layout.visibility == View.GONE){
            toolbar_layout.visibility = View.VISIBLE
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

    override fun onBackPressed() {
        if(slidingRootNav.isMenuClosed){
            slidingRootNav.openMenu()
        }else{
            super.onBackPressed()
        }
    }

    companion object {
        private var firstExecute = true
        private val PAGE_CALENDAR = 0
        private val PAGE_MAP_RECORD = 1
    }
}
