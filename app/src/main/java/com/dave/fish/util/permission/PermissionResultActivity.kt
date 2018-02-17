package com.dave.fish.util.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.dave.fish.R
import com.dave.fish.common.Constants
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton

/**
 * Created by soul on 2018. 2. 15..
 */
class PermissionResultActivity : Activity() {

    private var permissions: Array<String> ?= null
    private var alertMessage: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        permissions = intent.getStringArrayExtra(Constants.EXTRA_PERMISSIONS)
        alertMessage = intent.getStringExtra(Constants.EXTRA_PERMISSION_ALERT_MESSAGE)?: null
    }

    override fun onStart() {
        super.onStart()
        requestPermissionsCheck(permissions)
    }

    private fun requestPermissionsCheck(permissions: Array<String>?) {
        if (isTargetSdkUnderAndroidM()) return

        if (null == permissions) return

        val deniedPermissions = permissions
                .filter { isDenied(it) }
                .toTypedArray()

        if (deniedPermissions.isNotEmpty()) {
            requestPermissions(deniedPermissions)
        } else {
            PermissionCheck.success()
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionCheck.MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionCheck.success()
                finish()
            } else {
                PermissionCheck.fail()
                alert {
                    message = alertMessage ?: resources.getString(R.string.permission_default_message)
                    positiveButton("설정") { goToSetting() }
                    cancelButton {
                        finish()
                    }
                }.show()
            }

            return
        }
    }

    private fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, PermissionCheck.MY_PERMISSIONS_REQUEST_READ_CONTACTS)
    }

    private fun isGranted(permission: String): Boolean =
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun isDenied(permission: String): Boolean = !isGranted(permission)

    private fun goToSetting() {
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(myAppSettings)
    }

    private fun isTargetSdkUnderAndroidM(): Boolean {
        return try {
            val info = packageManager.getPackageInfo(packageName, 0)
            val targetSdkVersion = info.applicationInfo.targetSdkVersion
            targetSdkVersion < Build.VERSION_CODES.M
        } catch (ignore: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0);
    }
}
