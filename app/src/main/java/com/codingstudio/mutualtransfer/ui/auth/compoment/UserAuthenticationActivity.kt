package com.codingstudio.mutualtransfer.ui.auth.compoment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingstudio.mutualtransfer.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAuthenticationActivity : AppCompatActivity() {

    private val TAG = "UserAuthenticationActivity"
    private var navigationType : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_authentication)

        navigationType = intent.extras?.getString(NAVIGATION_TYPE) ?: NAVIGATION_LOGIN


        if (navigationType == NAVIGATION_SET_PROFILE) {

            val fragment = UserSetProfileOneFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
            fragmentTransaction.commit()

        }
        else {
            val fragment = UserPhoneNumberFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
            fragmentTransaction.commit()

        }
    }

    companion object {

        const val NAVIGATION_TYPE = "NAVIGATION_TYPE"
        const val NAVIGATION_LOGIN = "NAVIGATION_LOGIN"
        const val NAVIGATION_SET_PROFILE = "NAVIGATION_SET_PROFILE"

    }



}