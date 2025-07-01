package com.codingstudio.mutualtransfer.ui.message

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivityMessageBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.message.adapter.AdapterForMessageParent
import com.codingstudio.mutualtransfer.ui.message.viewmodel.MessageViewModel
import com.codingstudio.mutualtransfer.ui.message.viewmodel.MessageViewModelFactory
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageActivity : AppCompatActivity() {

    private val TAG = "MessageActivity"
    private var _binding : ActivityMessageBinding ?= null
    private val binding get() = _binding!!

    private lateinit var userId : String

    private lateinit var adapterForMessageParent: AdapterForMessageParent

    private val messageViewModel : MessageViewModel by viewModels {
        MessageViewModelFactory(application, (application as MainApplication).messageRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = SharedPref().getUserIDPref(this) ?: ""

        setOnClickListener()
        observeMessages()
        setRecyclerViewOfSearchHistory()
        checkAdEnableStatus()

    }

    private fun setOnClickListener() {

        binding.imageViewBackMessage.setOnClickListener {
            finish()
        }

        binding.swipeRefreshLayoutMessage.setOnRefreshListener {

            binding.swipeRefreshLayoutMessage.isRefreshing = false
            getMessages()

        }

    }

    private fun setRecyclerViewOfSearchHistory() {

        adapterForMessageParent = AdapterForMessageParent(userId, this)
        binding.recyclerViewMessages.apply {
            adapter = adapterForMessageParent
            layoutManager = LinearLayoutManager(this@MessageActivity)
        }

        adapterForMessageParent.setOnMessageClickedListener { modelMessage ->

            var receiverId = ""
            var receiverName = ""

            if (modelMessage.sender_id == userId) {
                receiverId = modelMessage.receiver_id ?: ""
                receiverName = modelMessage.receiver_name ?: ""
            }
            else {
                receiverId = modelMessage.sender_id ?: ""
                receiverName = modelMessage.sender_name ?: ""
            }

            val intent = Intent(this, MessageTransactionActivity::class.java).apply {
                putExtra(MessageTransactionActivity.MESSAGE_ID, "${modelMessage.id}")
                putExtra(MessageTransactionActivity.RECEIVER_ID, receiverId)
                putExtra(MessageTransactionActivity.RECEIVER_NAME, receiverName)
            }
            startActivity(intent)

        }

    }

    private fun getMessages(){
        messageViewModel.getMessagesByUserIdFun(userId)
    }
    private fun observeMessages() {

        messageViewModel.getMessagesByUserId.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                adapterForMessageParent.differ.submitList(responseResult.Messages)

                                val hasUnReadMessage = responseResult.Messages?.any { !it.is_read } == true
                                SharedPref().setBoolean(this, Constants.hasUnReadMessage, hasUnReadMessage)
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
                                    showSnackBarMessage(errorMessage)
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

    override fun onResume() {
        super.onResume()
        getMessages()
    }


    private fun checkAdEnableStatus() {
        val banner_ad = SharedPref().getBooleanPref(this, Constants.banner_ad)
        if (banner_ad) {
            loadBannerAdView()
        }
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