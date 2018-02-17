package com.dave.fish.common

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Created by soul on 2018. 2. 17..
 */
object FragmentProvider {

    fun <T : Fragment> instanceFragment(mFragment: T, url: String? = ""): T {
        url?.let {
            mFragment.arguments = Bundle().apply {
                putString(Constants.BUNDLE_FRAGMENT_URL, url)
            }
        }

        return mFragment
    }

    fun <T : Fragment> instanceFragment(mFragment: T, urls: Array<String>?): T {
        urls?.let {
            mFragment.arguments = Bundle().apply {
                putStringArray(Constants.BUNDLE_FRAGMENT_URLS, urls)
            }
        }

        return mFragment
    }
}
