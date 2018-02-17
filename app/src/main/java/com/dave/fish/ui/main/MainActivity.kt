package com.dave.fish.ui.main

import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.api.model.SidePanelData
import com.dave.fish.api.model.SidePanelModel
import com.dave.fish.common.Constants
import com.dave.fish.common.PickTideDialog
import com.dave.fish.db.RealmProvider
import com.dave.fish.ui.CustomAreasSpinner
import com.dave.fish.ui.alarm.AlarmFragment
import com.dave.fish.ui.calendar.CalendarFragment
import com.dave.fish.ui.kweather.KweatherFragment
import com.dave.fish.ui.main.menu.DrawerAdapter
import com.dave.fish.ui.main.menu.MenuDrawer
import com.dave.fish.ui.main.menu.SimpleItem
import com.dave.fish.ui.map.MapFragment
import com.dave.fish.ui.tip.TipMainFragment
import com.dave.fish.ui.web.WebFragment
import com.dave.fish.util.DLog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_left_drawer.*


class MainActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener {

    private lateinit var slidingRootNav: SlidingRootNav

    private lateinit var toolbarParams: AppBarLayout.LayoutParams
    private lateinit var toolbarLayoutParams: AppBarLayout.LayoutParams
    private lateinit var menuAdapter: DrawerAdapter

    // fragments
    private lateinit var fragmentCalendar: CalendarFragment
    private lateinit var fragmentMap: MapFragment
    private lateinit var fragmentKma: WebFragment
    private lateinit var fragmentMarinKma: WebFragment
    private lateinit var fragmentWindyty: WebFragment
    private lateinit var fragmentAlarm: AlarmFragment

    // dialog
    private lateinit var pickTideDialog: PickTideDialog
    private val mainSpinner: CustomAreasSpinner by lazy { findViewById<CustomAreasSpinner>(R.id.main_spinners) }

    // admob
    private lateinit var mInterstitialAd: InterstitialAd

    private var isFirstOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAd()

        initSpinner()

        initFragments()

        initToolbar()

        initSlidingMenu()

        initViewPager()

        initPickTide()
    }

    private fun initAd() {
        MobileAds.initialize(this, getString(R.string.admob_app_id))

        val adRequest = AdRequest.Builder().build()

        initBannerAd(adRequest)

        initInterstitialAd(adRequest)
    }

    private fun initBannerAd(adRequest: AdRequest?) {
        adView.loadAd(adRequest)
    }

    private fun initInterstitialAd(adRequest: AdRequest) {
        mInterstitialAd = InterstitialAd(this).apply {
            adUnitId = getString(R.string.admob_interstitial_id)
            loadAd(adRequest)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    }
                }
            }
        }
    }

    private fun initFragments() {
        fragmentCalendar = instanceFragment(
                CalendarFragment.newInstance()
        )

        fragmentMap = instanceFragment(
                MapFragment.newInstance()
        )

        fragmentKma = instanceFragment(
                WebFragment.newInstance(),
                Constants.KMA_M_URL
        )

        fragmentMarinKma = instanceFragment(
                WebFragment.newInstance(),
                Constants.MARIN_KMA_M_URL
        )

        fragmentWindyty = instanceFragment(
                WebFragment.newInstance(),
                Constants.FLOW_M_URL
        )

        fragmentAlarm = instanceFragment(
                AlarmFragment.newInstance()
        )
    }

    private fun <T : Fragment> instanceFragment(mFragment: T, url: String? = ""): T {
        url?.let {
            mFragment.arguments = Bundle().apply {
                putString(Constants.BUNDLE_FRAGMENT_URL, url)
            }
        }

        return mFragment
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbarParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams = toolbar_layout.layoutParams as AppBarLayout.LayoutParams
    }

    private fun initSlidingMenu() {
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
                createItemFor(MenuDrawer.KWEATHER),
                createItemFor(MenuDrawer.FWEATHER),
                createItemFor(MenuDrawer.ALARM),
                createItemFor(MenuDrawer.TIP)
        )

        menuAdapter = DrawerAdapter(menuList).apply {
            setListener(this@MainActivity)
            setSelected(0)
        }

        val list = findViewById<RecyclerView>(R.id.menu_drawer_list)
        with(list) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = menuAdapter
        }
    }

    private fun initViewPager() {
        main_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                toolbar.title = main_viewpager.adapter?.getPageTitle(position)
                menuAdapter.setSelected(position)
                when (position) {
                    PAGE_CALENDAR -> {
                        visibleCollapsingToolbar()
                        setScrollAble(true)
                    }
                    PAGE_MAP_RECORD -> {
                        visibleCollapsingToolbar()
                        setScrollAble(false)
                    }
                    else -> {
                        goneCollapsingToolbar()
                        setScrollAble(false)
                    }
                }
            }
        })
        // 스크롤 되게 하기 위해 해당 값을 true 로 해줘야한다.
        nest_scrollview.isFillViewport = true

        main_viewpager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(fragmentCalendar, getString(R.string.menu_tide_calendar))
            addFragment(fragmentMap, getString(R.string.menu_map))
            addFragment(KweatherFragment.newInstance(), "한국기상")
            addFragment(fragmentWindyty, getString(R.string.menu_weather_flow))
            addFragment(fragmentAlarm, getString(R.string.menu_alarm))
            addFragment(TipMainFragment.newInstance(), getString(R.string.menu_tip))
        }

        val viewPagerCount = (main_viewpager.adapter as ViewPagerAdapter).count
        main_viewpager.offscreenPageLimit = viewPagerCount
    }

    override fun onItemSelected(position: Int) {
        slidingRootNav.closeMenu()
        toolbar.title = main_viewpager.adapter?.getPageTitle(position)
        when (position) {
            in 0..1 -> {
                visibleCollapsingToolbar()
                main_viewpager.currentItem = position
            }

            else -> {
                goneCollapsingToolbar()
                main_viewpager.currentItem = position
            }
        }
    }

    private fun createItemFor(menuDrawer: MenuDrawer): SimpleItem {
        return SimpleItem(ContextCompat.getDrawable(this, menuDrawer.icon), menuDrawer.title)
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent))
    }

    private fun initPickTide() {
        initSideTide()

        val mListener: (values: SidePanelData) -> Unit = {
            setSideTide(it)
        }

        pickTideDialog = PickTideDialog().apply {
            initDialog({
                it.run(mListener)
            })
        }

        side_tide_container.setOnClickListener(setOnClickPickTide)
    }

    private fun setSideTide(it: SidePanelData) {
        val tideList = arrayListOf(it.lvl1, it.lvl2, it.lvl3, it.lvl4)
        DLog.w(tideList.toString())
        tv_pick_tide_name.text = applicationContext.resources.getString(R.string.today_tide, it.title)
        tv_pick_tide_values.text = tideList.joinToString("\n")
    }

    private fun initSideTide() {
        val postId: String? = RealmProvider.instance.getSecondSpinnerItem(Constants.KEY_TIDE_SIDE_SPINNER)?.obsPostId

        postId?.let {
            Network.request(ApiProvider.provideTideApi().getSidePanelData(postId),
                    NetworkCallback<SidePanelModel>().apply {
                        success = { sidePanelModel ->
                            val tideList = sidePanelModel.data
                            if (tideList.isNotEmpty()) {
                                setSideTide(tideList.first())
                            }
                        }

                        error = {
                            Toast.makeText(this@MainActivity, "데이터를 읽어오는데 실패하였습니다.\n다시 시도해주세요.", Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    private fun initSpinner() {
        mainSpinner.apply {
            init(Constants.KEY_TIDE_MAIN_SPINNER)
            initListener {
                if (!isFirstOpen) {
                    main_viewpager.adapter?.notifyDataSetChanged()
                }

                isFirstOpen = false
            }
        }
    }

    private val setOnClickPickTide = View.OnClickListener {
        pickTideDialog.show(supportFragmentManager, "pickTide")
    }

    @ColorInt
    private fun color(@ColorRes res: Int): Int = ContextCompat.getColor(this, res)

    private fun goneCollapsingToolbar() {
        if (toolbar_layout.visibility == View.VISIBLE) {
            toolbar_layout.visibility = View.GONE
        }
    }

    private fun visibleCollapsingToolbar() {
        if (toolbar_layout.visibility == View.GONE) {
            toolbar_layout.visibility = View.VISIBLE
        }
    }

    private fun setScrollAble(isAble: Boolean) {
        if (isAble) {
            fragmentCalendar.setVisibleNavigationCalendar(View.VISIBLE)
            enableScroll()
        } else {
            fragmentCalendar.setVisibleNavigationCalendar(View.GONE)
            unableScroll()
        }

        toolbar.requestLayout()
        toolbar_layout.requestLayout()
    }

    private fun unableScroll() {
        toolbarParams.scrollFlags = 0
        toolbarLayoutParams.scrollFlags = 0
    }

    private fun enableScroll() {
        toolbarParams.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        toolbarLayoutParams.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
    }

    override fun onPause() {
        if(adView != null){
            adView.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView.resume()
        }
    }

    override fun onDestroy() {
        if (adView != null) {
            adView.destroy()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        when {
            slidingRootNav.isMenuClosed -> slidingRootNav.openMenu()
            else -> super.onBackPressed()
        }
    }

    companion object {
        private var firstExecute = true
        private const val PAGE_CALENDAR = 0
        private const val PAGE_MAP_RECORD = 1
    }
}
