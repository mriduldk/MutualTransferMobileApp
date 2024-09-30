package com.codingstudio.mutualtransfer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserAuthenticationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, UserAuthenticationActivity::class.java)
        intent.putExtra(UserAuthenticationActivity.NAVIGATION_TYPE, UserAuthenticationActivity.NAVIGATION_LOGIN)
        startActivity(intent)


    }
}