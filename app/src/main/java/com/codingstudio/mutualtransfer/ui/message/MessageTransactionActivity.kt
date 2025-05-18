package com.codingstudio.mutualtransfer.ui.message

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityMessageTransactionsBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.message.adapter.AdapterForMessageTransactions
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
class MessageTransactionActivity : AppCompatActivity() {

    private val TAG = "MessageTransactionActivity"
    private var _binding : ActivityMessageTransactionsBinding ?= null
    private val binding get() = _binding!!

    private lateinit var userId : String
    private lateinit var message_id : String
    private lateinit var receiver_id : String
    private lateinit var receiver_name : String

    private lateinit var adapterForMessageTransactions: AdapterForMessageTransactions

    private val messageViewModel : MessageViewModel by viewModels {
        MessageViewModelFactory(application, (application as MainApplication).messageRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMessageTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = SharedPref().getUserIDPref(this) ?: ""
        //message_id = intent.extras?.getString(MESSAGE_ID) ?: ""
        receiver_id = intent.extras?.getString(RECEIVER_ID) ?: ""
        receiver_name = intent.extras?.getString(RECEIVER_NAME) ?: ""

        /*if (message_id.isEmpty()) {
            insertAcceptMessage()
        }*/

        setData()
        setOnClickListener()
        observeMessages()
        setRecyclerViewOfMessages()
        checkAdEnableStatus()

    }

    private fun setData() {
        binding.receiverName.text = receiver_name
    }

    private fun setOnClickListener() {

        binding.imageViewBackMessageTransaction.setOnClickListener {
            finish()
        }

        binding.buttonSend.setOnClickListener {

            if (binding.editTextMessage.text.toString().isNotBlank()) {
                messageViewModel.storeMessageTransactionsFun(
                    message_id = message_id,
                    message_content = binding.editTextMessage.text.toString(),
                    sender_id = userId,
                    receiver_id = receiver_id,
                )
                hideKeyboard()
            }

        }

        binding.buttonSendInitial.setOnClickListener {
            insertAcceptMessage()
        }

        binding.buttonAccept.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to accept this message?")
                .setPositiveButton("Accept") { dialog, _ ->

                    messageViewModel.acceptMessageFun(
                        message_id = message_id,
                        status = "accepted",
                        user_id = userId,
                    )

                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Handle "No" action here
                    dialog.dismiss()
                }
                .show()

        }

        binding.buttonReject.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to reject this message?")
                .setPositiveButton("Reject") { dialog, _ ->

                    messageViewModel.acceptMessageFun(
                        message_id = message_id,
                        status = "rejected",
                        user_id = userId,
                    )

                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Handle "No" action here
                    dialog.dismiss()
                }
                .show()

        }

        binding.swipeRefreshLayoutMessageTransaction.setOnRefreshListener {
            binding.swipeRefreshLayoutMessageTransaction.isRefreshing = false
            getMessages()
        }

    }

    private fun setRecyclerViewOfMessages() {

        adapterForMessageTransactions = AdapterForMessageTransactions(userId)
        binding.recyclerViewMessages.apply {
            adapter = adapterForMessageTransactions
            layoutManager = LinearLayoutManager(this@MessageTransactionActivity)
        }

    }

    private fun getMessages(){

        val userTypeId = SharedPref().getStringPref(this, Constants.user_type_id)

        messageViewModel.getMessageTransactionsBySenderAndReceiverIdFun(userId, receiver_id, userId, userTypeId?: "")
    }

    private fun observeMessages() {

        messageViewModel.getMessageTransactionsBySenderAndReceiverId.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200) {

                                var message = responseResult.Message

                                message_id = "${message?.id}"

                                if (message?.status == "accepted") {
                                    binding.chatInputLayout.visibility = VISIBLE
                                    binding.layoutNotAcceptedText.visibility = GONE
                                    binding.chatInputLayoutInitial.visibility = GONE
                                }
                                else if (message?.status == "pending") {
                                    binding.chatInputLayout.visibility = GONE
                                    binding.chatInputLayoutInitial.visibility = GONE
                                    binding.textViewNotAcceptedText.text = "Your message is pending. $receiver_name has not accepted your message yet. Please wait until it is accepted."

                                    if (message.last_message_sent_by != userId) {
                                        binding.linearLayoutAcceptReject.visibility = VISIBLE
                                        binding.layoutNotAcceptedText.visibility = GONE
                                    }
                                    else {
                                        binding.linearLayoutAcceptReject.visibility = GONE
                                        binding.layoutNotAcceptedText.visibility = VISIBLE
                                    }
                                }
                                else if (message?.status == "rejected") {
                                    binding.chatInputLayout.visibility = GONE
                                    binding.layoutNotAcceptedText.visibility = VISIBLE
                                    binding.chatInputLayoutInitial.visibility = GONE
                                    binding.textViewNotAcceptedText.text = "Message is rejectd by ${receiver_name}."
                                    binding.textViewNotAcceptedText.setTextColor(ContextCompat.getColor(this, R.color.red))
                                }
                                else {
                                    binding.chatInputLayout.visibility = GONE
                                    binding.layoutNotAcceptedText.visibility = GONE
                                    binding.chatInputLayoutInitial.visibility = VISIBLE
                                }

                                adapterForMessageTransactions.differ.submitList(responseResult.MessageContent)
                                binding.recyclerViewMessages.scrollToPosition(binding.recyclerViewMessages.adapter?.itemCount?.minus(1) ?: 0)

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
                                    //showSnackBarMessage(errorMessage)
                                }
                                Constants.NOT_FOUND -> {
                                    //showSnackBarMessage(errorMessage)

                                    binding.chatInputLayoutInitial.visibility = VISIBLE
                                    binding.chatInputLayout.visibility = GONE
                                }
                                else -> {
                                    //showSnackBarMessage(errorMessage)
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

        messageViewModel.storeMessageTransactions.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBarSendMessage()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){
                                getMessages()
                                binding.editTextMessage.setText("")
                            }
                            else{
                                showSnackBarMessage(responseResult.message)
                            }
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBarSendMessage()

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
                        showProgressBarSendMessage()
                    }
                }

            }


        })

        messageViewModel.storeMessage.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBarSendInitialMessage()

                        response.data?.let { responseResult ->

                            getMessages()
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBarSendInitialMessage()

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
                        showProgressBarSendInitialMessage()
                    }
                }

            }


        })

        messageViewModel.acceptMessage.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBarSendInitialMessage()

                        response.data?.let { responseResult ->

                            showSnackBarMessage(responseResult.message)
                            getMessages()
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        binding.linearLayoutAcceptReject.visibility = VISIBLE

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
                        binding.linearLayoutAcceptReject.visibility = GONE
                    }
                }

            }


        })

    }

    private fun insertAcceptMessage(){

        val userTypeId = SharedPref().getStringPref(this, Constants.user_type_id)

        messageViewModel.storeMessageFun(
            sender_id = userId,
            receiver_id = receiver_id,
            last_message_content = "Hey, I want to talk to you about the mutual transfer. If you're interested, let's chat!",
            userTypeId ?: ""
        )
    }

    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = VISIBLE
    }
    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = GONE
    }

    private fun showProgressBarSendMessage() {
        binding.progressBarSendMessages.visibility = VISIBLE
        binding.buttonSend.visibility = GONE
    }
    private fun hideProgressBarSendMessage() {
        binding.progressBarSendMessages.visibility = GONE
        binding.buttonSend.visibility = VISIBLE
    }

    private fun showProgressBarSendInitialMessage() {
        binding.progressBarSendMessagesInitial.visibility = VISIBLE
        binding.buttonSendInitial.visibility = GONE
    }
    private fun hideProgressBarSendInitialMessage() {
        binding.progressBarSendMessagesInitial.visibility = GONE
        binding.buttonSendInitial.visibility = VISIBLE
    }

    fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.relativeLayoutParent, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        getMessages()
    }
    companion object {

        const val MESSAGE_ID = "MESSAGE_ID"
        const val RECEIVER_ID = "RECEIVER_ID"
        const val RECEIVER_NAME = "RECEIVER_NAME"

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