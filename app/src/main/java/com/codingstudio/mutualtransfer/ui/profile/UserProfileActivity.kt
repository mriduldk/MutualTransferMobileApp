package com.codingstudio.mutualtransfer.ui.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityProfileBinding
import com.codingstudio.mutualtransfer.databinding.ActivitySearchBinding
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserDetailsChangeActivity
import com.codingstudio.mutualtransfer.ui.recently_viewed.RecentlyViewedActivity
import com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed.RecentlyViewedViewModel
import com.codingstudio.mutualtransfer.ui.wallet.WalletActivity
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private val TAG = "UserProfileActivity"
    private var navigationType : String = ""

    private var _binding : ActivityProfileBinding ?= null
    private val binding get() = _binding!!

    private val localUserDetailsViewModel : LocalUserDetailsViewModel by viewModels()
    private val recentlyViewedViewModel : RecentlyViewedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setOnClickListeners()
        observeUserDetailsLocalData()

    }

    private fun setOnClickListeners(){

        binding.imageViewEditPersonalDetails.setOnClickListener {

            val intent = Intent(this, UserDetailsChangeActivity::class.java)
            intent.putExtra(UserDetailsChangeActivity.NAVIGATION_FRAGMENT, UserDetailsChangeActivity.FRAGMENT_ONE)
            startActivity(intent)
        }

        binding.imageViewEditEmployeeDetails.setOnClickListener {

            val intent = Intent(this, UserDetailsChangeActivity::class.java)
            intent.putExtra(UserDetailsChangeActivity.NAVIGATION_FRAGMENT, UserDetailsChangeActivity.FRAGMENT_TWO)
            startActivity(intent)
        }

        binding.imageViewEditSchoolDetails.setOnClickListener {

            val intent = Intent(this, UserDetailsChangeActivity::class.java)
            intent.putExtra(UserDetailsChangeActivity.NAVIGATION_FRAGMENT, UserDetailsChangeActivity.FRAGMENT_THREE)
            startActivity(intent)
        }

        binding.imageViewEditPreferences.setOnClickListener {

            val intent = Intent(this, UserDetailsChangeActivity::class.java)
            intent.putExtra(UserDetailsChangeActivity.NAVIGATION_FRAGMENT, UserDetailsChangeActivity.FRAGMENT_PREFERENCE)
            startActivity(intent)
        }


        binding.relativeLayoutReferAndEarn.setOnClickListener {

            val intent = Intent(this, ReferAndEarnActivity::class.java)
            intent.putExtra(UserDetailsChangeActivity.NAVIGATION_FRAGMENT, UserDetailsChangeActivity.FRAGMENT_PREFERENCE)
            startActivity(intent)

        }

        binding.relativeLayoutLogoutRecentlyViewed.setOnClickListener {

            val intent = Intent(this, RecentlyViewedActivity::class.java)
            startActivity(intent)

        }

        binding.relativeLayoutWalletProfile.setOnClickListener {

            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)

        }

        binding.imageViewBackButton.setOnClickListener {
            finish()
        }

        binding.relativeLayoutLogoutProfile.setOnClickListener {

            recentlyViewedViewModel.deleteAllFun()
            localUserDetailsViewModel.deleteAllFun()
        }

        binding.relativeLayoutNeedHelpProfile.setOnClickListener {

            sendWhatsappMessage("*Need Help*\n\nWrite your query: ")
        }

        binding.relativeLayoutPrivacyPolicyProfile.setOnClickListener {

            openChrome("https://themutualtransfer.in/PricavyPolicy.html")
        }

        binding.relativeLayoutTermsAndConditionProfile.setOnClickListener {

            openChrome("https://themutualtransfer.in/terms_and_condition.html")
        }

        binding.textViewJypkoWebsite.setOnClickListener {
            openChrome("https://jypko.com")

        }

    }

    private fun observeUserDetailsLocalData(){

        localUserDetailsViewModel.getUserDetailsByUserIdObserver.observe(this, Observer { userDetails ->

            userDetails?.let { _it ->

                Log.e(TAG, "observeUserDetailsLocalData: ${_it.email}" )

                binding.textViewUserName.text = userDetails.name
                binding.textViewUserPhone.text = "Phone: ${userDetails.phone}"
                binding.textViewUserEmail.text = "Email: ${userDetails.email ?: ""}"
                binding.textViewUserGender.text = "Gender: ${userDetails.gender ?: ""}"

                binding.textViewEmployeeCode.text = "Employee Code: ${userDetails.employee_code ?: ""}"
                binding.textViewSchoolType.text = "School Type: ${userDetails.school_type ?: ""}, ${userDetails.teacher_type ?: ""}, ${userDetails.subject_type ?: ""}"

                binding.textViewSchoolName.text = "School Name: ${userDetails.school_name ?: ""}"
                binding.textViewUdiceCode.text = "UDICE Code: ${userDetails.udice_code ?: ""}"
                binding.textViewSchooladdress.text = "School Address: ${userDetails.school_address_vill ?: ""}, ${userDetails.school_address_block ?: ""}, ${userDetails.school_address_district ?: ""}, ${userDetails.school_address_state ?: ""}, ${userDetails.school_address_pin ?: ""}"

                binding.textViewPreferredDistrict1.text = "1. ${userDetails.preferred_district_1 ?: ""}"
                binding.textViewPreferredDistrict2.text = "2. ${userDetails.preferred_district_2 ?: ""}"
                binding.textViewPreferredDistrict3.text = "3. ${userDetails.preferred_district_3 ?: ""}"

                binding.textViewSchoolAmalgamated.text = "School Amalgamated : ${if (userDetails.amalgamation == 1) { "Yes" } else { "No" } }"
            }

        })

        localUserDetailsViewModel.deleteAllObserver.observe(this, Observer {

            if (it == 1) {
                SharedPref().logoutUser(this)
                finish()
            }

        })

        binding.textViewVersionCode.text = "Version - ${BuildConfig.VERSION_NAME}"
    }

    companion object {
        const val NAVIGATION_SET_PROFILE = "NAVIGATION_SET_PROFILE"
    }

    override fun onResume() {
        super.onResume()

        val userId = SharedPref().getUserIDPref(this)
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    private fun sendWhatsappMessage(message: String) {

        val phoneNumber = Constants.FEEDBACK_WHATSAPP_NUMBER
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.setPackage("com.whatsapp") // Regular WhatsApp package name
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openChrome(url: String) {

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            setPackage("com.android.chrome")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            intent.setPackage(null)
            startActivity(intent)
        }

    }

}