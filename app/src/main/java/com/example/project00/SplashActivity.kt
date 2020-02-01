package com.example.project00

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SplashActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Thread.sleep(2000)
        }finally {

        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}