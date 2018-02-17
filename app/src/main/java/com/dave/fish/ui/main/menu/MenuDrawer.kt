package com.dave.fish.ui.main.menu

import com.dave.fish.R

/**
 * Created by soul on 2017. 10. 22..
 */
enum class MenuDrawer(val title : String, val icon : Int) {
    INFO("물때&날씨", R.drawable.ic_toys_white_24dp),
    MAP("지도&기록", R.drawable.ic_toys_white_24dp),
    KWEATHER("국내예보", R.drawable.ic_toys_white_24dp),
    FWEATHER("해외예보", R.drawable.ic_toys_white_24dp),
    ALARM("알림설정", R.drawable.ic_toys_white_24dp),
    TIP("해루질팁", R.drawable.ic_toys_white_24dp),
    MAIL("메일문의", R.drawable.ic_toys_white_24dp),
    SHARE("공유하기", R.drawable.ic_toys_white_24dp)
}