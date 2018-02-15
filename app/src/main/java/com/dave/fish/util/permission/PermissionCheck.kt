package com.dave.fish.util.permission

import android.content.Context
import android.content.Intent
import com.dave.fish.common.Constants

/**
 * Created by soul on 2018. 2. 14..
 *
 * < Usage >
 *      PermissionCheck
 *          .withContext(this)
 *          .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
 *                              Manifest.permission.CAMERA,
 *                              Manifest.permission.WRITE_CALENDAR,
 *                              Manifest.permission.READ_PHONE_STATE)
 *          .success { do something }
 *          .fail { do som }
 *          .check()
 */

class PermissionCheck {

    companion object {
        private var context: Context? = null
        private var permissions: Array<out String>? = null
        private var message: String ?= null
        var success: (() -> Unit) = {}
        var fail: (() -> Unit) = {}

        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10

        fun withContext(context: Context): PermissionCheck.Companion {
            this.context = context
            return this
        }

        fun withPermissions(vararg permissions: String): PermissionCheck.Companion {
            this.permissions = permissions
            return this
        }

        fun setAlertMessage(message: String): PermissionCheck.Companion{
            this.message = message
            return this
        }

        fun success(success: () -> Unit): PermissionCheck.Companion {
            this.success = success
            return this
        }

        fun fail(fail: () -> Unit): PermissionCheck.Companion {
            this.fail = fail
            return this
        }

        fun check() {
            val permissionIntent = Intent(context, PermissionResultActivity::class.java).apply {
                putExtra(Constants.EXTRA_PERMISSIONS, permissions)
                putExtra(Constants.EXTRA_PERMISSION_ALERT_MESSAGE, message)
            }
            context?.startActivity(permissionIntent)
        }
    }
}
