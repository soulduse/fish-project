package com.dave.fish.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by soul on 2018. 2. 14..
 */

class PermissionCheck{
    fun isGranted(context: Context, permission: String): Boolean =
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun isDenied(context: Context, permission: String): Boolean = !isGranted(context, permission)

    fun requestPermissions(activity: Activity?, vararg permissions: String){
        activity?.let {
            ActivityCompat.requestPermissions(activity, permissions, MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10
    }
}
