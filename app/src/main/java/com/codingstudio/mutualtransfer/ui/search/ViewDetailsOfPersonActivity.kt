package com.codingstudio.mutualtransfer.ui.search

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivityViewDetailsOfPersonBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPerson
import com.codingstudio.mutualtransfer.ui.payment.AlertConfirmationDialog
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModel
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModelFactory
import com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed.RecentlyViewedViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed.RecentlyViewedViewModelFactory
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModelFactory
import com.codingstudio.mutualtransfer.ui.wallet.BuyCoinActivity
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewDetailsOfPersonActivity : AppCompatActivity() {

    private val TAG = "ViewDetailsOfPersonActivity"
    private var personId : String ?= null
    private var searchedPerson : ModelSearchResultOfPerson ?= null

    private var _binding : ActivityViewDetailsOfPersonBinding ?= null
    private val binding get() = _binding!!

    private val searchViewModel : SearchViewModel by viewModels {
        SearchViewModelFactory(application, (application as MainApplication).searchRepository)
    }
    private val recentlyViewedViewModel : RecentlyViewedViewModel by viewModels {
        RecentlyViewedViewModelFactory((application as MainApplication).recentlyViewedRepository)
    }
    private val paymentViewModel : PaymentViewModel by viewModels {
        PaymentViewModelFactory(application, (application as MainApplication).paymentRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityViewDetailsOfPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        personId = intent.extras?.getString(SEARCH_PERSON_ID) ?: ""

        personId?.let {
            getSearchedPersonResult()
        }

        onClickListeners()
        observeSearchedPersonResult()
        observePaymentRequestForThePerson()

        checkAdEnableStatus()
    }

    private fun onClickListeners() {

        binding.constraintLayoutSearchResultPayCoins.setOnClickListener {

            AlertConfirmationDialog.Builder(this)
                .setTitle("Confirmation of Paying ${searchedPerson?.pay_to_view_amount} Coins")
                .setMessage("You are paying ${searchedPerson?.pay_to_view_amount} coins to view ${searchedPerson?.name}'s details.")
                .setPositiveButtonText("Pay")
                .setNegativeButtonText("Cancel")
                .onConfirm {
                    paymentRequestForThePerson()
                }
                .onCancel {
                }
                .build()
                .show()

        }

        binding.imageViewBackPersonInformation.setOnClickListener {
            finish()
        }

        binding.imageViewPhoneNoCopy.setOnClickListener {

            searchedPerson?.let {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Phone No", it.phone)
                clipboard.setPrimaryClip(clip)
            }
        }

    }

    /**
     * Get Searched Person Details based on District Name and Block Name
     */
    private fun getSearchedPersonResult(){

        val user_id = SharedPref().getUserIDPref(this)

        searchViewModel.viewPersonDetailsFun(
            person_user_id = personId ?: "",
            user_id = user_id ?: "",
        )
    }
    private fun observeSearchedPersonResult() {

        searchViewModel.viewPersonDetailsObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                responseResult.personDetails?.let {
                                    setPersonalInformation(it)
                                    saveInRecentlyViewed(it)
                                }
                            }
                            else{
                                showSnackBarMessage(responseResult.message)
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


    /**
     * Payment Request For Viewing The Person's Details
     */
    private fun paymentRequestForThePerson(){

        val user_id = SharedPref().getUserIDPref(this)

        paymentViewModel.saveUserPayForAnotherUserFun(
            payment_done_by = user_id ?: "",
            payment_done_for = searchedPerson?.fk_user_id ?: "",
            amount = searchedPerson?.pay_to_view_amount ?: ""
        )
    }
    private fun observePaymentRequestForThePerson() {

        paymentViewModel.saveUserPayForAnotherUserObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){
                                getSearchedPersonResult()
                            }
                            else if (responseResult.status == 409){

                                AlertConfirmationDialog.Builder(this)
                                    .setTitle("Error !!!")
                                    .setMessage(responseResult.message)
                                    .setPositiveButtonText("Buy Coins")
                                    .setHasNegativeButton(true)
                                    .onConfirm {
                                        startActivity(Intent(this, BuyCoinActivity::class.java))
                                    }
                                    .onCancel {
                                    }
                                    .build()
                                    .show()
                            }
                            else{
                                AlertConfirmationDialog.Builder(this)
                                    .setTitle("Error !!!")
                                    .setMessage(responseResult.message)
                                    .setPositiveButtonText("Ok")
                                    .setHasNegativeButton(false)
                                    .onConfirm {
                                    }
                                    .onCancel {
                                    }
                                    .build()
                                    .show()
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



    private fun setPersonalInformation(personDetails: ModelSearchResultOfPerson) {

        binding.textViewViewDetailsUserName.text = personDetails.name
        binding.textViewViewDetailsUserPhoneNo.text = personDetails.phone
        binding.textViewViewDetailsUserEmail.text = personDetails.email

        binding.textViewViewDetailsPostName.text = "${personDetails.teacher_type} (${personDetails.school_type})"
        binding.textViewViewDetailsPostSubject.text = personDetails.subject_type

        binding.textViewViewDetailsSchoolName.text = personDetails.school_name
        binding.textViewViewDetailsSchoolUdiceCode.text = "UDICE Code: ${personDetails.udice_code}"
        binding.textViewViewDetailsSchoolAddress.text = "${personDetails.school_address_vill}, ${personDetails.school_address_block}, ${personDetails.school_address_district}, ${personDetails.school_address_state}, ${personDetails.school_address_pin}"

        binding.textViewViewDetailsSchoolAmalgamated.text = "School Amalgamated: ${if (personDetails.amalgamation == 1) { "Yes" } else { "No" }}"

        if(personDetails.is_paid == 1) {
            binding.constraintLayoutSearchResultPayCoins.visibility = View.GONE
            binding.constraintLayoutSearchResultSave.visibility = View.VISIBLE
        }
        else {
            binding.constraintLayoutSearchResultPayCoins.visibility = View.VISIBLE
            binding.constraintLayoutSearchResultSave.visibility = View.VISIBLE
        }

        binding.textViewViewDetailsPayCoins.text = "Pay ${personDetails.pay_to_view_amount} Coins"

        var hasPreferredDistricts = false

        personDetails.preferred_district_1?.let {
            binding.chipDistrictPreference1.text = it
            binding.chipDistrictPreference1.visibility = View.VISIBLE
            hasPreferredDistricts = true
        } ?: run {
            binding.chipDistrictPreference1.visibility = View.GONE
        }

        personDetails.preferred_district_2?.let {
            binding.chipDistrictPreference2.text = it
            binding.chipDistrictPreference2.visibility = View.VISIBLE
            hasPreferredDistricts = true
        } ?: run {
            binding.chipDistrictPreference2.visibility = View.GONE
        }

        personDetails.preferred_district_3?.let {
            binding.chipDistrictPreference3.text = it
            binding.chipDistrictPreference3.visibility = View.VISIBLE
            hasPreferredDistricts = true
        } ?: run {
            binding.chipDistrictPreference3.visibility = View.GONE
        }

        if (hasPreferredDistricts) {
            binding.textViewViewDetailsPreferredDistrictsText.text = "Preferred Districts"
        }
        else {
            binding.textViewViewDetailsPreferredDistrictsText.text = "No Preferred District Available"
        }


        searchedPerson = personDetails

    }

    private fun saveInRecentlyViewed(personDetails: ModelSearchResultOfPerson){

        val recentlyViewed = personDetails.toRecentlyViewedModel()
        recentlyViewedViewModel.insertFun(recentlyViewed)
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
        const val SEARCH_PERSON_ID = "SEARCH_PERSON_ID"

    }

    private fun loadBannerAdView() {

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewViewPersonDetails.loadAd(adRequest)

        binding.adViewViewPersonDetails.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to execute when an ad finishes loading
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                binding.adViewViewPersonDetails.visibility = View.GONE
            }

            override fun onAdOpened() {
                // Code to execute when an ad opens an overlay that covers the screen
            }

            override fun onAdClicked() {
                // Code to execute when the user clicks on an ad
            }

            override fun onAdClosed() {
                // Code to execute when the user is about to return to the app after tapping on an ad
            }
        }
    }

    private fun checkAdEnableStatus() {

        val db = FirebaseFirestore.getInstance()

        db.collection("ad_config").document("banner_ad")
            .get()
            .addOnSuccessListener { document ->

                if (document != null) {

                    val enable = document.getBoolean("enable")
                    val banner_ad_enable = enable ?: false
                    if (banner_ad_enable) {
                        loadBannerAdView()
                    }

                }
            }
    }

}