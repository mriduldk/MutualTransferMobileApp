package com.codingstudio.mutualtransfer.ui.auth.compoment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailsChangeActivity : AppCompatActivity() {

    private val TAG = "UserDetailsChangeActivity"
    private var navigationFragment : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_authentication)

        navigationFragment = intent.extras?.getString(NAVIGATION_FRAGMENT) ?: FRAGMENT_ONE


        when (navigationFragment) {
            FRAGMENT_ONE -> {

                val fragment = UserSetProfileOneFragment.newInstance(fragmentType = Constants.GO_TO_BACK)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
                fragmentTransaction.commit()

            }
            FRAGMENT_TWO -> {

                val fragment = UserSetProfileTwoFragment.newInstance(fragmentType = Constants.GO_TO_BACK)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
                fragmentTransaction.commit()

            }
            FRAGMENT_THREE -> {

                val fragment = UserSetProfileThreeFragment.newInstance(fragmentType = Constants.GO_TO_BACK)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
                fragmentTransaction.commit()

            }
            FRAGMENT_PREFERENCE -> {

                val fragment = UserSetProfilePreferences.newInstance(fragmentType = Constants.GO_TO_BACK)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
                fragmentTransaction.commit()

            }
            else -> {
                val fragment = UserSetProfileOneFragment.newInstance(fragmentType = Constants.GO_TO_BACK)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragment, TAG)
                fragmentTransaction.commit()
            }
        }
    }

    companion object {

        const val NAVIGATION_TYPE = "NAVIGATION_TYPE"
        const val NAVIGATION_LOGIN = "NAVIGATION_LOGIN"
        const val NAVIGATION_SET_PROFILE = "NAVIGATION_SET_PROFILE"

        const val NAVIGATION_FRAGMENT = "NAVIGATION_FRAGMENT"

        const val FRAGMENT_ONE = "FRAGMENT_ONE"
        const val FRAGMENT_TWO = "FRAGMENT_TWO"
        const val FRAGMENT_THREE = "FRAGMENT_THREE"
        const val FRAGMENT_PREFERENCE = "FRAGMENT_PREFERENCE"


    }



}