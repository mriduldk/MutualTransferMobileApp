package com.codingstudio.mutualtransfer.ui.wallet

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivityOnlinePaymentBinding
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
import com.codingstudio.mutualtransfer.ui.wallet.adapter.AdapterForOnlinePayment
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnlinePaymentActivity : AppCompatActivity() {

    private val TAG = "OnlinePaymentActivity"
    private var _binding : ActivityOnlinePaymentBinding ?= null
    private val binding get() = _binding!!

    private lateinit var adapterForOnlinePayment: AdapterForOnlinePayment

    private val paymentViewModel : PaymentViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnlinePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListener()
        observeRecentlyViewedPersonResult()
        observeOnlinePayments()

        setRecyclerViewOfSearchHistory()

    }

    private fun setOnClickListener() {

        binding.textViewBack.setOnClickListener {
            finish()
        }

    }

    private fun setRecyclerViewOfSearchHistory() {

        adapterForOnlinePayment = AdapterForOnlinePayment()
        binding.recyclerViewOnlinePayment.apply {
            adapter = adapterForOnlinePayment
            layoutManager = LinearLayoutManager(this@OnlinePaymentActivity)
        }

        adapterForOnlinePayment.setRefreshPaymentClickedListener { personDetails ->

            refreshPaymentStatus(
                orderId = personDetails.order_id ?: "",
                onlinePaymentId = personDetails.online_payment_id ?: ""
            )

        }

        adapterForOnlinePayment.setContactSupportClickedListener{ personDetails ->

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Payment Support*\n\nUser Mobile No: $userPhone\nOrder ID: ${personDetails.order_id}\nAmount: ₹ ${personDetails.amount}\nCoins: ${personDetails.coins}\nPayment Id: ${personDetails.razorpay_payment_id}\n" +
                    "Online Payment Id: ${personDetails.online_payment_id}\nReceipt: ${personDetails.receipt}\n\nWrite your query:")

        }

    }

    /**
     * Refresh payment Status
     */
    private fun refreshPaymentStatus(orderId: String, onlinePaymentId: String){

        val userId = SharedPref().getUserIDPref(this)

        paymentViewModel.refreshPaymentStatusWithOrderIdFun(
            user_id = userId ?: "",
            online_payment_id = onlinePaymentId,
            razorpay_order_id = orderId,
        )
    }
    private fun observeRecentlyViewedPersonResult() {

        paymentViewModel.refreshPaymentStatusWithOrderIdObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { walletResponse ->

                            showSnackBarMessage(walletResponse.message)
                            getOnlinePayments()

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


    private fun getOnlinePayments() {

        val userId = SharedPref().getUserIDPref(this)

        paymentViewModel.getUsersPaymentDetailsFun(
            user_id = userId ?: ""
        )

    }

    private fun observeOnlinePayments() {

        paymentViewModel.getUsersPaymentDetailsObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { onlinePaymentResponse ->

                            if (onlinePaymentResponse.status == 200){

                                adapterForOnlinePayment.differ.submitList(onlinePaymentResponse.onlinePayments)
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

    override fun onResume() {
        super.onResume()
        getOnlinePayments()
    }



}