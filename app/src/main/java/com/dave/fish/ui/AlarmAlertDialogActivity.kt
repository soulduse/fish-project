package com.dave.fish.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.Window
import android.view.WindowManager
import com.dave.fish.R
import com.dave.fish.util.DLog
import kotlinx.android.synthetic.main.dialog_alarm_bottom_sheet.*

/**
 * Created by soul on 2018. 1. 5..
 */
class AlarmAlertDialogActivity : Activity() {

    val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    lateinit var bottomSheetDialog: BottomSheetDialog

    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DLog.w("AlarmAlertDialogActivity 0000")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        DLog.w("AlarmAlertDialogActivity 1111")

        createDialog()
        bottomSheetDialog.show()
        ringtone()
        DLog.w("AlarmAlertDialogActivity 2222")
    }

    private fun ringtone(){
        val rm = RingtoneManager(applicationContext)
        RingtoneManager.URI_COLUMN_INDEX
        mediaPlayer = MediaPlayer.create(applicationContext, alarmUri)
        mediaPlayer.start()

    }

    private fun createDialog(){
        val sheetView = layoutInflater.inflate(R.layout.dialog_alarm_bottom_sheet, null)
        bottomSheetDialog = BottomSheetDialog(this).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
            setContentView(sheetView)

            tv_off_alarm.setOnClickListener {
                mediaPlayer.stop()
                dismiss()
                finish()
            }
        }
    }
}