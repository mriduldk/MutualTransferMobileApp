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
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivitySearchResultOfPersonBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ModelSearch
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPerson
import com.codingstudio.mutualtransfer.ui.payment.AlertConfirmationDialog
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModel
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModelFactory
import com.codingstudio.mutualtransfer.ui.search.adpter.AdapterForSearchResultOfPerson
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModelFactory
import com.codingstudio.mutualtransfer.ui.wallet.BuyCoinActivity
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore


class SearchResultPersonActivity : AppCompatActivity() {

    private val TAG = "SearchResultPersonActivity"
    private var modelSearch : ModelSearch ?= null
    private var _binding : ActivitySearchResultOfPersonBinding ?= null
    private val binding get() = _binding!!

    private var maxClickCountForAd = 5

    private lateinit var adapterForSearchResultOfPerson: AdapterForSearchResultOfPerson

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
        observePaymentRequestForThePerson()
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

        adapterForSearchResultOfPerson = AdapterForSearchResultOfPerson()
        binding.recyclerViewSearchResultOfPerson.apply {
            adapter = adapterForSearchResultOfPerson
            layoutManager = LinearLayoutManager(this@SearchResultPersonActivity)
        }

        adapterForSearchResultOfPerson.setOnPersonViewDetailsClickListener { personDetails ->

            val intent = Intent(this, ViewDetailsOfPersonActivity::class.java).apply {
                putExtra(ViewDetailsOfPersonActivity.SEARCH_PERSON_ID, personDetails.user_details_id)
            }
            startActivity(intent)

        }

        adapterForSearchResultOfPerson.setOnPhoneNoCopyClickListener{ personDetails ->

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Phone No", personDetails.phone)
            clipboard.setPrimaryClip(clip)

        }

        adapterForSearchResultOfPerson.setOnPayPersonClickedListener { personDetails ->

            AlertConfirmationDialog.Builder(this)
                .setTitle("Confirmation of Paying ${personDetails.pay_to_view_amount} Coins")
                .setMessage("You are paying ${personDetails.pay_to_view_amount} coins to view ${personDetails.name}'s details.")
                .setPositiveButtonText("Pay")
                .setNegativeButtonText("Cancel")
                .onConfirm {
                    paymentRequestForThePerson(personDetails)
                }
                .onCancel {
                }
                .build()
                .show()

        }

    }

    /**
     * Get Searched Person Details based on District Name and Block Name
     */
    private fun getSearchedPersonResult(){

        val user_id = SharedPref().getUserIDPref(this)

        searchViewModel.searchPersonFun(
            school_address_district = modelSearch?.searchDistrictText ?: "",
            user_id = user_id ?: "",
            school_address_block = modelSearch?.searchBlockText ?: "",
            school_address_vill = "",
            school_name = modelSearch?.searchSchoolId ?: ""
        )
    }
    private fun observeSearchedPersonResult() {

        searchViewModel.searchPersonObserver.observe(this, Observer { res ->

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
                                    binding.textViewSearchResultOfPerson.text = "$size result found"
                                }
                                else {
                                    binding.linearLayoutNoResultFound.visibility = View.GONE
                                    binding.recyclerViewSearchResultOfPerson.visibility = View.VISIBLE
                                    binding.textViewSearchResultOfPerson.text = "$size results found"
                                }
                            }
                            else{
                                binding.linearLayoutNoResultFound.visibility = View.VISIBLE
                                binding.recyclerViewSearchResultOfPerson.visibility = View.GONE
                                showSnackBarMessage(responseResult.message)
                                binding.textViewSearchResultOfPerson.text = "0 result found"
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


    /**
     * Payment Request For Viewing The Person's Details
     */
    private fun paymentRequestForThePerson(personDetails: ModelSearchResultOfPerson){

        val user_id = SharedPref().getUserIDPref(this)

        paymentViewModel.saveUserPayForAnotherUserFun(
            payment_done_by = user_id ?: "",
            payment_done_for = personDetails.fk_user_id ?: "",
            amount = personDetails.pay_to_view_amount ?: ""
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
                            else {

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
                           /* else{
                                showSnackBarMessage(responseResult.message)
                            }*/
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()

                        response.message?.let { errorMessage ->
                            when (errorMessage) {
                                Constants.NO_INTERNET -> {
                                    showSnackBarMessage("No internet connection")
                                }
                                Constants.CONFLICT -> {
                                    AlertConfirmationDialog.Builder(this)
                                        .setTitle("Error !!!")
                                        .setMessage(errorMessage)
                                        .setPositiveButtonText("Ok")
                                        .setHasNegativeButton(false)
                                        .onConfirm {

                                        }
                                        .onCancel {
                                        }
                                        .build()
                                        .show()
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

        val db = FirebaseFirestore.getInstance()

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
            }


    }

}