package com.dave.fish_project

import android.arch.lifecycle.LifecycleActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : LifecycleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
