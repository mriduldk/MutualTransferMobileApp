package com.codingstudio.mutualtransfer.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityReferBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import taimoor.sultani.sweetalert2.Sweetalert


@AndroidEntryPoint
class ReferAndEarnActivity : AppCompatActivity() {

    private val TAG = "ReferAndEarnActivity"
    private var navigationType : String = ""

    private var _binding : ActivityReferBinding ?= null
    private val binding get() = _binding!!

    private val localUserDetailsViewModel : LocalUserDetailsViewModel by viewModels()
    private val userDetailsViewModel : UserDetailsViewModel by viewModels()

    private var referralCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReferBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setOnClickListeners()
        observeUserDetailsLocalData()
        observerReferralInsert()

    }

    private fun setOnClickListeners(){

        binding.btnEnter.setOnClickListener {

            if (binding.editTextReferralCode.text.toString().isNullOrEmpty()){
                showSnackBarMessage("Referral code is required.")
                binding.editTextReferralCode.requestFocus()
            }
            else {

                val userId = SharedPref().getUserIDPref(this)

                userDetailsViewModel.useReferralCodeFun(
                    referral_code = binding.editTextReferralCode.text.toString(),
                    user_id = userId ?: ""
                )
            }

        }

        binding.imageViewBackButton.setOnClickListener {
            finish()
        }

        binding.btnCopy.setOnClickListener {

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral Code", referralCode)
            clipboard.setPrimaryClip(clip)

            showSnackBarMessage("Referral code copied.")
        }

        binding.ivShareWhatsApp.setOnClickListener {

            val url = "https://play.google.com/store/apps/details?id=com.codingstudio.mutualtransfer"
            val message = "Explore mutual transfers and find your match easily. \nUse my referral code _*$referralCode*_ to join. \nApp on PlayStore : $url\n\n Share your referral link to earn bonuses for each friend who signs up. Start referring and boosting your rewards today! \uD83D\uDE80"


            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, message)
            try {
                startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                // WhatsApp is not installed, handle this gracefully
            }

        }

        binding.ivShareMessenger.setOnClickListener {

            val url = "https://play.google.com/store/apps/details?id=com.codingstudio.mutualtransfer"
            val message = "Explore mutual transfers and find your match easily. \nUse my referral code _*$referralCode*_ to join. \nApp on PlayStore : $url\n\n Share your referral link to earn bonuses for each friend who signs up. Start referring and boosting your rewards today! \uD83D\uDE80"

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, "Share via"))

        }

        binding.ivShareMail.setOnClickListener {

            val url = "https://play.google.com/store/apps/details?id=com.codingstudio.mutualtransfer"
            val message = "Explore mutual transfers and find your match easily. \nUse my referral code _*$referralCode*_ to join. \nApp on PlayStore : $url\n\n Share your referral link to earn bonuses for each friend who signs up. Start referring and boosting your rewards today! \uD83D\uDE80"

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, "Share via"))

        }

        binding.ivShareSkype.setOnClickListener {

            val url = "https://play.google.com/store/apps/details?id=com.codingstudio.mutualtransfer"
            val message = "Explore mutual transfers and find your match easily. \nUse my referral code _*$referralCode*_ to join. \nApp on PlayStore : $url\n\n Share your referral link to earn bonuses for each friend who signs up. Start referring and boosting your rewards today! \uD83D\uDE80"

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, "Share via"))

        }


    }

    private fun observeUserDetailsLocalData(){

        localUserDetailsViewModel.getUserDetailsByUserIdObserver.observe(this, Observer { userDetails ->

            if (userDetails.is_referral_code_used == 1) {

                binding.tvMyReferralCode.text = userDetails.my_referral_code
                binding.textViewReferralUseHeader.text = "You have used the referral code."
                binding.textViewReferralUseSubHeader.text = ""
                binding.textViewReferralUseSubHeader.visibility = View.GONE

                binding.editTextReferralCode.setText(userDetails.used_referral_code)
                binding.editTextReferralCode.isEnabled = false
                binding.btnEnter.isEnabled = false

                binding.btnEnter.background = ContextCompat.getDrawable(this, R.drawable.rounded_button_disable)

                referralCode = userDetails.my_referral_code ?: ""
            }
            else {

                binding.tvMyReferralCode.text = userDetails.my_referral_code

                binding.textViewReferralUseHeader.text = "Do you have referral code ?"
                binding.textViewReferralUseSubHeader.text = "Paste the referral code from your \nfriend and earn coin."
                binding.textViewReferralUseSubHeader.visibility = View.VISIBLE

                binding.editTextReferralCode.setText("")
                binding.editTextReferralCode.isEnabled = true
                binding.btnEnter.isEnabled = true

                binding.btnEnter.background = ContextCompat.getDrawable(this, R.drawable.rounded_button)

                referralCode = userDetails.my_referral_code ?: ""
            }

        })

    }

    private fun observerReferralInsert(){

        userDetailsViewModel.useReferralCodeObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserDetails ->

                            if (responseUserDetails.status == 200){

                                Sweetalert(this, Sweetalert.SUCCESS_TYPE)
                                    .setTitleText("Success..!!!")
                                    .setContentText("Referral code used successfully")
                                    .show()

                                onResume()

                            }
                            else{
                                showSnackBarMessage(responseUserDetails.message)
                            }
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()

                        response.message?.let { errorMessage ->
                            when (errorMessage) {
                                Constants.NO_INTERNET -> {
                                    showSnackBarMessage("No internet connection")
                                }
                                else -> {
                                    showSnackBarMessage(errorMessage)
                                }
                            }
                        }

                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }

            }

        })

    }


    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.GONE
    }

    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.relativeLayoutParent, message, Snackbar.LENGTH_LONG).show()
    }


    companion object {
        const val NAVIGATION_SET_PROFILE = "NAVIGATION_SET_PROFILE"
    }

    override fun onResume() {
        super.onResume()

        val userId = SharedPref().getUserIDPref(this)
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }


}