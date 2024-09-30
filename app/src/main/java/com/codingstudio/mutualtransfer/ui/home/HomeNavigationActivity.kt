package com.codingstudio.mutualtransfer.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityHomeNavigationBinding
import com.codingstudio.mutualtransfer.databinding.ActivityProfileBinding
import com.codingstudio.mutualtransfer.databinding.ActivitySearchBinding
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserDetailsChangeActivity
import com.codingstudio.mutualtransfer.ui.recently_viewed.RecentlyViewedActivity
import com.codingstudio.mutualtransfer.ui.wallet.WalletActivity
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeNavigationActivity : AppCompatActivity() {

    private val TAG = "HomeNavigationActivity"
    private var navigationType : String = ""

    private var _binding : ActivityHomeNavigationBinding ?= null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayoutHome, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayoutHome.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()

            menuItem.isChecked = true
            binding.drawerLayoutHome.close()
            true
        }

    }




}