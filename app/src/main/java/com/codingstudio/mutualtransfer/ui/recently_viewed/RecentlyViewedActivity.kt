package com.codingstudio.mutualtransfer.ui.recently_viewed

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
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivityRecentlyViewedBinding
import com.codingstudio.mutualtransfer.databinding.ActivitySearchResultOfPersonBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelSearch
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPerson
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.ui.payment.AlertConfirmationDialog
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModel
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModelFactory
import com.codingstudio.mutualtransfer.ui.recently_viewed.adapter.AdapterForRecentlyViewedPerson
import com.codingstudio.mutualtransfer.ui.search.ViewDetailsOfPersonActivity
import com.codingstudio.mutualtransfer.ui.search.adpter.AdapterForSearchResultOfPerson
import com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed.RecentlyViewedViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.search.SearchViewModelFactory
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentlyViewedActivity : AppCompatActivity() {

    private val TAG = "RecentlyViewedActivity"
    private var _binding : ActivityRecentlyViewedBinding ?= null
    private val binding get() = _binding!!

    private var paidUserDetailsId = ""


    private lateinit var adapterForRecentlyViewedPerson: AdapterForRecentlyViewedPerson

    private val paymentViewModel : PaymentViewModel by viewModels {
        PaymentViewModelFactory(application, (application as MainApplication).paymentRepository)
    }
    private val recentlyViewedViewMode : RecentlyViewedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRecentlyViewedBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setOnClickListener()
        observeRecentlyViewedPersonResult()
        observePaymentRequestForThePerson()
        setRecyclerViewOfSearchHistory()

    }

    private fun setOnClickListener() {

        binding.textViewSearchResultUserName.setOnClickListener {
            finish()
        }

    }

    private fun setRecyclerViewOfSearchHistory() {

        adapterForRecentlyViewedPerson = AdapterForRecentlyViewedPerson()
        binding.recyclerViewRecentlyViewedPerson.apply {
            adapter = adapterForRecentlyViewedPerson
            layoutManager = LinearLayoutManager(this@RecentlyViewedActivity)
        }

        adapterForRecentlyViewedPerson.setOnPersonViewDetailsClickListener { personDetails ->

            val intent = Intent(this, ViewDetailsOfPersonActivity::class.java).apply {
                putExtra(ViewDetailsOfPersonActivity.SEARCH_PERSON_ID, personDetails.user_details_id)
            }
            startActivity(intent)

        }

        adapterForRecentlyViewedPerson.setOnPhoneNoCopyClickListener{ personDetails ->

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Phone No", personDetails.phone)
            clipboard.setPrimaryClip(clip)

        }

        adapterForRecentlyViewedPerson.setOnPayPersonClickedListener { personDetails ->

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
     * Get Recently Viewed Person from Local Database
     */
    private fun getRecentlyViewedPerson(){

        recentlyViewedViewMode.getAllRecentlyViewedPersonFun()
    }
    private fun observeRecentlyViewedPersonResult() {

        recentlyViewedViewMode.getAllRecentlyViewedPersonObserver.observe(this, Observer { response ->

            adapterForRecentlyViewedPerson.differ.submitList(response)

            val size = response.size
            if (size <= 1) {
                binding.textViewSearchResultOfPerson.text = "$size result found"
            }
            else {
                binding.textViewSearchResultOfPerson.text = "$size results found"
            }

        })

    }


    /**
     * Payment Request For Viewing The Person's Details
     */
    private fun paymentRequestForThePerson(personDetails: ModelRecentlyViewed){

        val user_id = SharedPref().getUserIDPref(this)

        paidUserDetailsId = personDetails.user_details_id

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

                                val intent = Intent(this, ViewDetailsOfPersonActivity::class.java).apply {
                                    putExtra(ViewDetailsOfPersonActivity.SEARCH_PERSON_ID, paidUserDetailsId)
                                }
                                startActivity(intent)

                            }
                            else if (responseResult.status == 409){

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

    override fun onResume() {
        super.onResume()
        getRecentlyViewedPerson()
    }



}