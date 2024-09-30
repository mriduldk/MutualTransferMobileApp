package com.codingstudio.mutualtransfer.ui.wallet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityWalletBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.wallet.Wallet
import com.codingstudio.mutualtransfer.ui.wallet.adapter.AdapterForCoinTransaction
import com.codingstudio.mutualtransfer.ui.wallet.viewmodel.WalletViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.WalletConstants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class WalletActivity : AppCompatActivity() {

    private val TAG = "WalletActivity"

    private var _binding : ActivityWalletBinding ?= null
    private val binding get() = _binding!!

    private val walletViewModel : WalletViewModel by viewModels()
    private lateinit var adapterForCoinTransaction : AdapterForCoinTransaction

    private val UseGooglePay = "com.google.android.apps.nbu.paisa.user"
    private val UsePhonePe = "com.phonepe.app"
    private val UsePaytm = "net.one97.paytm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickListeners()
        setRecyclerView()
        observeWalletData()
        //initiateGooglePayTransaction()
    }

    private fun onClickListeners(){

        binding.imageViewBackWallet.setOnClickListener {
            finish()
        }

        binding.linearLayout1.setOnClickListener {

            paymentOffersClick(1)
            enableBuyButton()
        }
        binding.linearLayout2.setOnClickListener {

            paymentOffersClick(2)
            enableBuyButton()
        }
        binding.linearLayout3.setOnClickListener {

            paymentOffersClick(3)
            enableBuyButton()
        }
        binding.linearLayout4.setOnClickListener {

            paymentOffersClick(4)
            enableBuyButton()
        }

        binding.buttonBuyCoinsWalletActivity.setOnClickListener {

            startActivity(Intent(this, BuyCoinActivity::class.java))

        }

        binding.textViewGoToOnlinePaymentPage.setOnClickListener {
            startActivity(Intent(this, OnlinePaymentActivity::class.java))
        }
    }

    private fun enableBuyButton(){

        binding.btnBuyInWallet.isEnabled = true
        binding.btnBuyInWallet.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_1)

    }

    private fun paymentOffersClick(index: Int){

        if (index == 1){
            binding.linearLayout1.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_7)
        }
        else{
            binding.linearLayout1.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        }

        if (index == 2){
            binding.linearLayout2.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_7)
        }
        else{
            binding.linearLayout2.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        }

        if (index == 3){
            binding.linearLayout3.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_7)
        }
        else{
            binding.linearLayout3.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        }

        if (index == 4){
            binding.linearLayout4.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_7)
        }
        else{
            binding.linearLayout4.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        }


    }

    private fun setRecyclerView() {

        adapterForCoinTransaction = AdapterForCoinTransaction(this)
        binding.recyclerViewRecentlyCoinTransaction.apply {
            layoutManager = LinearLayoutManager(this@WalletActivity)
            adapter = adapterForCoinTransaction
        }

        adapterForCoinTransaction.setOnCoinTransactionClickedListener { coinTransaction ->

        }

    }

    private fun getWalletData() {

        val user_id = SharedPref().getUserIDPref(this)
        walletViewModel.getWalletDataByUserFun(user_id ?: "")
    }

    private fun observeWalletData() {

        walletViewModel.getWalletDataByUserObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { walletResponse ->

                            if (walletResponse.status == 200){

                                setWalletInformation(walletResponse.wallet)
                            }
                            else{
                                showSnackBarMessage(walletResponse.message)
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

    private fun setWalletInformation(wallet : Wallet?) {

        wallet?.let {

            binding.textViewYourWalletBalanceCoin.text = "${wallet.total_amount}"

            adapterForCoinTransaction.differ.submitList(it.coin_transactions)

        }
    }

    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        getWalletData()
    }

    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.relativeLayoutParent, message, Snackbar.LENGTH_LONG).show()
    }


    private lateinit var paymentsClient: PaymentsClient

    private fun initiateGooglePayTransaction() {

        // Initialize Google Pay API Client
        paymentsClient = com.google.android.gms.wallet.Wallet.getPaymentsClient(
            this,
            com.google.android.gms.wallet.Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Use ENVIRONMENT_PRODUCTION for production
                .build()
        )

        //checkGooglePayAvailability()

    }

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    private val UPI_PAYMENT_REQUEST_CODE = 992

    private fun requestPayment() {

        /*val upiId = "6001025603@okbizaxis"
        val name = "Mridul Das"
        val amount = "1.00"
        val transactionNote = "Payment for Mridul Da"
        val currency = "INR"

        val uri = Uri.parse("upi://pay?pa=$upiId&pn=$name&tn=$transactionNote&am=$amount&cu=$currency")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage(UseGooglePay)  // Google Pay package name

        startActivityForResult(intent, UPI_PAYMENT_REQUEST_CODE)*/

        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", "6001025603@okbizaxis")
            .appendQueryParameter("pn", "Test Merchant")
            .appendQueryParameter("mc", "1234")
            .appendQueryParameter("tr", "123456789")
            .appendQueryParameter("tn", "test transaction note")
            .appendQueryParameter("am", "1.00")
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("url", "https://test.merchant.website")
            .build()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        intent.setPackage(UseGooglePay)
        startActivityForResult(intent, UPI_PAYMENT_REQUEST_CODE)


        /*val paymentDataRequestJson = JSONObject(loadPaymentDataJson())
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        // Launch Google Pay sheet
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request),
            this,
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )*/

    }

    private fun loadPaymentDataJson(): String {
        val inputStream = resources.openRawResource(R.raw.payment_config)
        return inputStream.bufferedReader().use { it.readText() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.let {
                            val paymentData = PaymentData.getFromIntent(it)
                            handlePaymentSuccess(paymentData)
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        // Payment cancelled by user
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                        // Handle error
                    }
                }
            }

            UPI_PAYMENT_REQUEST_CODE -> {

                if (resultCode == Activity.RESULT_OK || resultCode == 11) {
                    if (data != null) {
                        val response = data.getStringExtra("response")
                        val status = getUPITransactionStatus(response)
                        if (status == "success") {
                            Toast.makeText(this, "Payment Success", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Payment Cancelled by the user", Toast.LENGTH_LONG).show()
                }
            }


        }
    }

    private fun getUPITransactionStatus(response: String?): String {
        return if (response == null) {
            "failed"
        } else {
            val responseMap = response.split("&").associate {
                val (key, value) = it.split("=")
                key to value
            }
            responseMap["Status"] ?: "failed"
        }
    }


    private fun handlePaymentSuccess(paymentData: PaymentData?) {
        paymentData?.let {
            val paymentInformation = it.toJson()

            // Tokenize or send payment information to your server
            val paymentMethodData = JSONObject(paymentInformation)
                .getJSONObject("paymentMethodData")
            val tokenizationData = paymentMethodData
                .getJSONObject("tokenizationData")
            val token = tokenizationData.getString("token")

            // Send token to payment gateway or server for processing
        }
    }

    private fun checkGooglePayAvailability() {
        val isReadyToPayJson = JSONObject(loadPaymentDataJson()) // Load your payment JSON
        val isReadyToPayRequest: IsReadyToPayRequest = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())

        val task: Task<Boolean> = paymentsClient.isReadyToPay(isReadyToPayRequest)
        task.addOnCompleteListener { completedTask ->
            try {
                val result = completedTask.getResult(ApiException::class.java)
                if (result == true) {
                    // Show Google Pay Button
                    binding.buttonBuyCoinsWalletActivity.visibility = View.VISIBLE
                } else {
                    // Google Pay not available
                    binding.buttonBuyCoinsWalletActivity.visibility = View.GONE
                }
            } catch (exception: ApiException) {
                exception.printStackTrace()
            }
        }
    }


}