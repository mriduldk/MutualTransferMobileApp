package com.codingstudio.mutualtransfer.ui.message

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivityMessageBinding
import com.codingstudio.mutualtransfer.databinding.ActivityMessageTransactionsBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.message.adapter.AdapterForMessageParent
import com.codingstudio.mutualtransfer.ui.message.adapter.AdapterForMessageTransactions
import com.codingstudio.mutualtransfer.ui.message.viewmodel.MessageViewModel
import com.codingstudio.mutualtransfer.ui.message.viewmodel.MessageViewModelFactory
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
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
        message_id = intent.extras?.getString(MESSAGE_ID) ?: ""
        receiver_id = intent.extras?.getString(RECEIVER_ID) ?: ""
        receiver_name = intent.extras?.getString(RECEIVER_NAME) ?: ""

        setData()
        setOnClickListener()
        observeMessages()
        setRecyclerViewOfMessages()

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

    }

    private fun setRecyclerViewOfMessages() {

        adapterForMessageTransactions = AdapterForMessageTransactions(userId)
        binding.recyclerViewMessages.apply {
            adapter = adapterForMessageTransactions
            layoutManager = LinearLayoutManager(this@MessageTransactionActivity)
        }

    }

    private fun getMessages(){
        messageViewModel.getMessageTransactionsByMessageIdFun(message_id, userId)
    }
    private fun observeMessages() {

        messageViewModel.getMessageTransactionsByMessageId.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                adapterForMessageTransactions.differ.submitList(responseResult.MessageContent)

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


    }


    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.GONE
    }

    private fun showProgressBarSendMessage() {
        binding.progressBarSendMessages.visibility = View.VISIBLE
        binding.buttonSend.visibility = View.GONE
    }
    private fun hideProgressBarSendMessage() {
        binding.progressBarSendMessages.visibility = View.GONE
        binding.buttonSend.visibility = View.VISIBLE
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


}