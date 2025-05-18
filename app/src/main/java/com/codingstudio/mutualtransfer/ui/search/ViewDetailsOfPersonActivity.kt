package com.codingstudio.mutualtransfer.ui.search

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
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPersonNew
import com.codingstudio.mutualtransfer.ui.message.MessageTransactionActivity
import com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed.RecentlyViewedViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed.RecentlyViewedViewModelFactory
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModelFactory
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
    private var receiver_id = ""
    private var receiver_name = ""

    private var _binding : ActivityViewDetailsOfPersonBinding ?= null
    private val binding get() = _binding!!

    private val searchViewModel : SearchViewModel by viewModels {
        SearchViewModelFactory(application, (application as MainApplication).searchRepository)
    }
    private val recentlyViewedViewModel : RecentlyViewedViewModel by viewModels()

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

        checkAdEnableStatus()
    }

    private fun onClickListeners() {

        binding.constraintLayoutSearchResultMessage.setOnClickListener {

            val intent = Intent(this, MessageTransactionActivity::class.java).apply {
                putExtra(MessageTransactionActivity.RECEIVER_ID, receiver_id)
                putExtra(MessageTransactionActivity.RECEIVER_NAME, receiver_name)
            }
            startActivity(intent)

        }

        binding.imageViewBackPersonInformation.setOnClickListener {
            finish()
        }

    }

    /**
     * Get Searched Person Details based on District Name and Block Name
     */
    private fun getSearchedPersonResult(){

        val user_id = SharedPref().getUserIDPref(this)
        val user_type_id = SharedPref().getStringPref(this, Constants.user_type_id)

        if (user_type_id == Constants.TETTeacherID) {
            searchViewModel.viewPersonDetailsFun(
                person_user_id = personId ?: "",
                user_id = user_id ?: "",
            )
        } else {
            searchViewModel.viewPersonDetails_v3Fun(
                person_user_id = personId ?: "",
                user_id = user_id ?: "",
            )
        }
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

        searchViewModel.viewPersonDetails_v3Observer.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                responseResult.personDetails?.let {
                                    setPersonalInformationNew(it)
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


    private fun setPersonalInformation(personDetails: ModelSearchResultOfPerson) {

        receiver_id = "${personDetails.fk_user_id}"
        receiver_name = "${personDetails.name}"

        binding.textViewViewDetailsUserName.text = personDetails.name

        binding.textViewViewDetailsPostName.text = "${personDetails.teacher_type} (${personDetails.school_type})"
        binding.textViewViewDetailsPostSubject.text = personDetails.subject_type

        binding.textViewViewDetailsSchoolName.text = personDetails.school_name
        binding.textViewViewDetailsSchoolUdiceCode.text = "UDICE Code: ${personDetails.udice_code}"
        binding.textViewViewDetailsSchoolAddress.text = "${personDetails.school_address_vill}, ${personDetails.school_address_block}, ${personDetails.school_address_district}, ${personDetails.school_address_state}, ${personDetails.school_address_pin}"

        binding.textViewViewDetailsSchoolAmalgamated.text = "School Amalgamated: ${if (personDetails.amalgamation == 1) { "Yes" } else { "No" }}"

        binding.constraintLayoutSearchResultPayCoins.visibility = View.GONE
        binding.constraintLayoutSearchResultSave.visibility = View.VISIBLE
        binding.constraintLayoutSearchResultMessage.visibility = View.VISIBLE

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

    }
    private fun setPersonalInformationNew(personDetails: ModelSearchResultOfPersonNew) {

        receiver_id = "${personDetails.fk_user_id}"
        receiver_name = "${personDetails.name}"

        binding.textViewViewDetailsUserName.text = personDetails.name
        binding.textViewViewDetailsUserType.text = personDetails.user_type
        binding.textViewViewDetailsUserType.visibility = View.VISIBLE

        binding.textViewViewDetailsPostName.text =  "${personDetails.current_role} (${personDetails.department})"
        binding.textViewViewDetailsPostSubject.text = personDetails.service_type

        binding.textViewViewDetailsSchoolDetails.text = "Service Details"
        binding.textViewViewDetailsSchoolName.text = personDetails.current_organisation_name
        binding.textViewViewDetailsSchoolUdiceCode.visibility = View.GONE
        personDetails.zone_division?.let {
            binding.textViewViewDetailsSchoolUdiceCode.text = "Zone / Division: ${personDetails.zone_division}"
            binding.textViewViewDetailsSchoolUdiceCode.visibility = View.VISIBLE
        }

        binding.textViewViewDetailsSchoolAddress.text = "${personDetails.job_address_village}, ${personDetails.job_address_district}, ${personDetails.job_address_state}, ${personDetails.job_address_pin}"

        binding.textViewViewDetailsSchoolAmalgamated.visibility = View.GONE

        binding.constraintLayoutSearchResultPayCoins.visibility = View.GONE
        binding.constraintLayoutSearchResultSave.visibility = View.VISIBLE
        binding.constraintLayoutSearchResultMessage.visibility = View.VISIBLE

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

    }
    private fun formatDetails(vararg parts: String?): String {
        return parts.filterNotNull().filter { it.isNotBlank() }
            .joinToString(" ") { part -> if (part.contains(" ")) "($part)" else part }
    }


    private fun saveInRecentlyViewed(personDetails: ModelSearchResultOfPerson){

        val recentlyViewed = personDetails.toRecentlyViewedModel()
        recentlyViewedViewModel.insertFun(recentlyViewed)
    }
    private fun saveInRecentlyViewed(personDetails: ModelSearchResultOfPersonNew){

        val recentlyViewed = personDetails.toRecentlyViewedModelNew()
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

        val banner_ad = SharedPref().getBooleanPref(this, Constants.banner_ad)
        if (banner_ad) {
            loadBannerAdView()
        }

        /*val db = FirebaseFirestore.getInstance()

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
            }*/
    }

}