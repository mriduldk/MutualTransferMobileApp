package com.codingstudio.mutualtransfer.ui.message

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

    }

    private fun setOnClickListener() {

        binding.imageViewBackMessage.setOnClickListener {
            finish()
        }

    }

    private fun setRecyclerViewOfSearchHistory() {

        adapterForMessageParent = AdapterForMessageParent(userId ?: "", this)
        binding.recyclerViewMessages.apply {
            adapter = adapterForMessageParent
            layoutManager = LinearLayoutManager(this@MessageActivity)
        }

        adapterForMessageParent.setOnMessageClickedListener { modelMessage ->

            /*val intent = Intent(this, ViewDetailsOfPersonActivity::class.java).apply {
                putExtra(ViewDetailsOfPersonActivity.SEARCH_PERSON_ID, personDetails.user_details_id)
            }
            startActivity(intent)*/

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

                                adapterForMessageParent.differ.submitList(responseResult.MessageContent)

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



}