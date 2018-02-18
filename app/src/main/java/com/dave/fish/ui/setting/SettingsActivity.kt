package com.dave.fish.ui.setting

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dave.fish.R
import android.content.Intent
import android.net.Uri
import com.android.billingclient.api.BillingClient
import kotlinx.android.synthetic.main.activity_settings.*
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener


/**
 * Created by soul on 2018. 2. 18..
 */
class SettingsActivity: AppCompatActivity(), PurchasesUpdatedListener {

    private var mBillingClient: BillingClient ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initToolbar()
        tv_send_email.setOnClickListener { sendEmail() }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun sendPay(){
        mBillingClient = BillingClient.newBuilder(applicationContext).setListener(this).build()
        mBillingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingResponse.OK && purchases != null){

        } else if (responseCode == BillingResponse.USER_CANCELED){

        } else {

        }
    }

    private fun pay(){
        mBillingClient?.queryPurchases(BillingClient.SkuType.INAPP)
    }

    private fun sendEmail(){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.email_address)))
            putExtra(Intent.EXTRA_SUBJECT, "To Developer of 해루질러")
        }

        startActivity(Intent.createChooser(intent, resources.getString(R.string.title_send_mail_to_developer)))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
