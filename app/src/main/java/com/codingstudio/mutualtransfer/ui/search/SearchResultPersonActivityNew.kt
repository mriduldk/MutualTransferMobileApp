package com.codingstudio.mutualtransfer.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivitySearchResultOfPersonBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ModelSearch
import com.codingstudio.mutualtransfer.ui.message.MessageTransactionActivity
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModel
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModelFactory
import com.codingstudio.mutualtransfer.ui.search.adpter.AdapterForSearchResultOfPersonNew
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModelFactory
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore


class SearchResultPersonActivityNew : AppCompatActivity() {

    private val TAG = "SearchResultPersonActivityNew"
    private var modelSearch : ModelSearch ?= null
    private var _binding : ActivitySearchResultOfPersonBinding ?= null
    private val binding get() = _binding!!

    private var maxClickCountForAd = 5

    private lateinit var adapterForSearchResultOfPerson: AdapterForSearchResultOfPersonNew

    private val paymentViewModel : PaymentViewModel by viewModels {
        PaymentViewModelFactory(application, (application as MainApplication).paymentRepository)
    }
    private val searchViewModel : SearchViewModel by viewModels {
        SearchViewModelFactory(application, (application as MainApplication).searchRepository)
    }

    private var interstitial_ad_enable = false
    private var interstitial_ad_click_count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchResultOfPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelSearch = intent.extras?.getParcelable(SEARCH_MODEL)

        modelSearch?.let {
            getSearchedPersonResult()

            var textOfSearch = ""
            if (!it.searchStateText.isNullOrEmpty()){
                textOfSearch = "State : ${it.searchStateText}"
            }
            if (!it.searchDistrictText.isNullOrEmpty()){
                textOfSearch = "District : ${it.searchDistrictText}"
            }
            if (!it.searchBlockText.isNullOrEmpty()){
                textOfSearch = "$textOfSearch, Block : ${it.searchBlockText}"
            }
            if (!it.searchSchoolText.isNullOrEmpty()){
                textOfSearch = "$textOfSearch, School Name : ${it.searchSchoolText}"
            }
            binding.textViewSearchResultTextOfSearch.text = textOfSearch
        }


        setOnClickListeners()
        observeSearchedPersonResult()
        setRecyclerViewOfSearchHistory()

        //checkAdLoadCount()
        checkAdEnableStatus()
    }

    private fun setOnClickListeners() {

        binding.textViewSearchResultUserName.setOnClickListener {
            finish()
        }

        binding.swipeRefreshLayoutSearchResult.setOnRefreshListener {
            getSearchedPersonResult()
            binding.swipeRefreshLayoutSearchResult.isRefreshing = false
        }

    }

    private fun setRecyclerViewOfSearchHistory() {

        adapterForSearchResultOfPerson = AdapterForSearchResultOfPersonNew()
        binding.recyclerViewSearchResultOfPerson.apply {
            adapter = adapterForSearchResultOfPerson
            layoutManager = LinearLayoutManager(this@SearchResultPersonActivityNew)
        }

        adapterForSearchResultOfPerson.setOnPersonViewDetailsClickListener { personDetails ->

            val intent = Intent(this, ViewDetailsOfPersonActivity::class.java).apply {
                putExtra(ViewDetailsOfPersonActivity.SEARCH_PERSON_ID, personDetails.user_details_new_id)
            }
            startActivity(intent)

        }

        adapterForSearchResultOfPerson.setOnMessagePersonClickedListener { personDetails ->

            val intent = Intent(this, MessageTransactionActivity::class.java).apply {
                putExtra(MessageTransactionActivity.RECEIVER_ID, personDetails.fk_user_id)
                putExtra(MessageTransactionActivity.RECEIVER_NAME, personDetails.name)
            }
            startActivity(intent)
        }

    }

    /**
     * Get Searched Person Details based on District Name and Block Name
     */
    private fun getSearchedPersonResult(){

        val user_id = SharedPref().getUserIDPref(this)

        searchViewModel.searchPerson_v3Fun(
            job_address_state = modelSearch?.searchStateText ?: "",
            job_address_district = modelSearch?.searchDistrictText ?: "",
            job_address_block = modelSearch?.searchBlockText ?: "",
            department = modelSearch?.searchDepartmentText ?: "",
            current_role = modelSearch?.searchCurrentRoleText ?: "",
            user_id = user_id ?: ""
        )
    }
    private fun observeSearchedPersonResult() {

        searchViewModel.searchPerson_v3Observer.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){
                                adapterForSearchResultOfPerson.differ.submitList(responseResult.searchResult)

                                val size = responseResult.searchResult?.size ?: 0
                                if (size <= 0) {
                                    binding.linearLayoutNoResultFound.visibility = View.VISIBLE
                                    binding.recyclerViewSearchResultOfPerson.visibility = View.GONE
                                }
                                else if (size <= 1) {
                                    binding.linearLayoutNoResultFound.visibility = View.GONE
                                    binding.recyclerViewSearchResultOfPerson.visibility = View.VISIBLE
                                    binding.textViewSearchResultOfPerson.text = "$size person found"
                                }
                                else {
                                    binding.linearLayoutNoResultFound.visibility = View.GONE
                                    binding.recyclerViewSearchResultOfPerson.visibility = View.VISIBLE
                                    binding.textViewSearchResultOfPerson.text = "$size persons found"
                                }
                            }
                            else{
                                binding.linearLayoutNoResultFound.visibility = View.VISIBLE
                                binding.recyclerViewSearchResultOfPerson.visibility = View.GONE
                                showSnackBarMessage(responseResult.message)
                                binding.textViewSearchResultOfPerson.text = "0 person found"
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
                                    binding.linearLayoutNoResultFound.visibility = View.VISIBLE
                                    binding.recyclerViewSearchResultOfPerson.visibility = View.GONE
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
        const val SEARCH_MODEL = "SEARCH_MODEL"

    }

    private var interstitialAd: InterstitialAd? = null

    private fun checkAdLoadCount() {

        var clickCount = SharedPref().getIntPref(this, Constants.AD_CLICK_COUNT)

        if (clickCount >= maxClickCountForAd) {
            SharedPref().setInt(this, Constants.AD_CLICK_COUNT, 0)
            loadInterstitialAd()
        }
        else {
            clickCount += 1
            SharedPref().setInt(this, Constants.AD_CLICK_COUNT, clickCount)
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID, adRequest, object :
            InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                showInterstitialAd()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }
        })
    }

    private fun showInterstitialAd() {

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                //loadInterstitialAd() // Optionally load another ad after dismissal
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }

        interstitialAd?.show(this)
    }

    private fun checkAdEnableStatus() {

        val interstitial_ad = SharedPref().getBooleanPref(this, Constants.interstitial_ad)
        val interstitial_ad_maxClickCount = SharedPref().getIntPref(this, Constants.interstitial_ad_maxClickCount)
        if (interstitial_ad) {
            if (interstitial_ad_maxClickCount != 0) {
                maxClickCountForAd = interstitial_ad_maxClickCount
            }
            checkAdLoadCount()
        }
        val banner_ad = SharedPref().getBooleanPref(this, Constants.banner_ad)
        if (banner_ad) {
            loadBannerAdView()
        }

        /*val db = FirebaseFirestore.getInstance()

        db.collection("ad_config").document("interstitial_ad")
            .get()
            .addOnSuccessListener { document ->

                if (document != null) {

                    val enable = document.getBoolean("enable")
                    val no_of_clicks_to_open_ad = document.getLong("no_of_clicks_to_open_ad")?.toInt()
                    interstitial_ad_enable = enable ?: false
                    maxClickCountForAd = no_of_clicks_to_open_ad ?: 5

                    if (interstitial_ad_enable) {
                        checkAdLoadCount()
                    }

                }
            }*/


    }

    private fun loadBannerAdView() {

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewHome.loadAd(adRequest)

        binding.adViewHome.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to execute when an ad finishes loading
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                binding.adViewHome.visibility = View.GONE
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

}