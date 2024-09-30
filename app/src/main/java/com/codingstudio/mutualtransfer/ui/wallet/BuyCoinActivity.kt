package com.codingstudio.mutualtransfer.ui.wallet

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityBuyCoinsBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.UserDetails
import com.codingstudio.mutualtransfer.ui.payment.AlertConfirmationDialog
import com.codingstudio.mutualtransfer.ui.payment.viewmodel.PaymentViewModel
import com.codingstudio.mutualtransfer.ui.wallet.adapter.AdapterForCoinTransaction
import com.codingstudio.mutualtransfer.ui.wallet.viewmodel.WalletViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


@AndroidEntryPoint
class BuyCoinActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private val TAG = "BuyCoinActivity"

    private var _binding : ActivityBuyCoinsBinding ?= null
    private val binding get() = _binding!!

    private lateinit var adapterForCoinTransaction : AdapterForCoinTransaction

    private val UseGooglePay = "com.google.android.apps.nbu.paisa.user"
    private val UsePhonePe = "com.phonepe.app"
    private val UsePaytm = "net.one97.paytm"

    private var selectedAmount = ""
    private var selectedCoin = ""
    private var online_payment_id = ""
    private var paymentOrderId = ""
    private var paymentInitiatingMessage = "Payment is initiating..."

    private val B2B_PG_REQUEST_CODE = 777

    private val localUserDetailsViewModel : LocalUserDetailsViewModel by viewModels()
    private val paymentViewModel : PaymentViewModel by viewModels()
    private val walletViewModel : WalletViewModel by viewModels()
    private var userDetails : UserDetails ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBuyCoinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWalletOffers()
        onClickListeners()
        observeUserDetailsLocalData()
        observeCreateOrder()
        observePaymentVerification()
        observeWalletOffers()
        observePaymentFresh()

        buttonEnableUsingFirebase()
    }

    private fun onClickListeners(){

        binding.imageViewBackWallet.setOnClickListener {
            finish()
        }

        binding.linearLayout1.setOnClickListener {

            paymentOffersClick(1)
            enableBuyButton()

            selectedAmount = "50"
            selectedCoin = "60"

        }
        binding.linearLayout2.setOnClickListener {

            paymentOffersClick(2)
            enableBuyButton()

            selectedAmount = "100"
            selectedCoin = "130"
        }
        binding.linearLayout3.setOnClickListener {

            paymentOffersClick(3)
            enableBuyButton()

            selectedAmount = "200"
            selectedCoin = "300"
        }
        binding.linearLayout4.setOnClickListener {

            paymentOffersClick(4)
            enableBuyButton()

            selectedAmount = "500"
            selectedCoin = "800"
        }

        binding.btnBuyInWalletViaWhatsapp.setOnClickListener {

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Buy Coins*\n\nUser Mobile No: $userPhone\nSelected Amount: ₹ $selectedAmount\nCoins: \uD83E\uDE99 $selectedCoin")
        }

        binding.textViewPaymentInitiatingNeedHelpPaymentPage.setOnClickListener {

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Need Help In Payment - Payment Initiating*\n\nUser Mobile No: $userPhone\nOrder ID: $paymentOrderId\n\nWhat is your query: ")
        }
        binding.textViewPaymentSuccessfulNeedHelpPaymentPage.setOnClickListener {

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Need Help In Payment - Payment Success*\n\nUser Mobile No: $userPhone\nOrder ID: $paymentOrderId\n\nWhat is your query: ")
        }
        binding.textViewPaymentFailedNeedHelpPaymentPage.setOnClickListener {

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Need Help In Payment - Payment Failed*\n\nUser Mobile No: $userPhone\nOrder ID: $paymentOrderId\n\nWhat is your query: ")
        }
        binding.textViewPaymentProcessingNeedHelpPaymentPage.setOnClickListener {

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Need Help In Payment - Payment Processing*\n\nUser Mobile No: $userPhone\nOrder ID: $paymentOrderId\n\nWhat is your query: ")
        }
        binding.textViewAnyPaymentRelatedIssue.setOnClickListener {

            val userPhone = SharedPref().getStringPref(this, Constants.user_phone)
            sendWhatsappMessage("*Need Help In Payment*\n\nUser Mobile No: $userPhone\n\nWhat is your issue: ")
        }



        binding.btnBuyInWalletViaOnlinePayment.setOnClickListener {
            createOrder()
        }


        binding.buttonGoToWallet.setOnClickListener {
            finish()
        }
        binding.buttonBackToWallet.setOnClickListener {
            finish()
        }
        binding.buttonBackToWalletPaymentProcessing.setOnClickListener {
            finish()
        }

        binding.buttonRefreshPaymentStatus.setOnClickListener {
            refreshPaymentStatus()
        }

        binding.buttonRetryPayment.setOnClickListener {
            createOrder()
        }
        binding.buttonCancelPayment.setOnClickListener {
            finish()
        }

    }

    private fun getWalletOffers() {

        walletViewModel.getWalletOffersFun()
    }

    private fun observeWalletOffers() {

        walletViewModel.getWalletOffersObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                val walletOffers = responseResult.walletOffers

                                walletOffers?.let {

                                    try {

                                        if (it.size > 0){

                                            binding.cardView1.visibility = View.VISIBLE

                                            binding.textViewWalletOfferPrice1.text = "₹${it[0].total_amount}.00"
                                            binding.textViewWalletOfferCoins1.text = "${it[0].total_coin} Coins ${it[0].message}"

                                            if (it[0].discount == 0){
                                                binding.textViewWalletOfferDiscount1.visibility = View.GONE
                                            }
                                            else {
                                                binding.textViewWalletOfferDiscount1.visibility = View.VISIBLE
                                                binding.textViewWalletOfferDiscount1.text = "${it[0].discount}% Discount"
                                            }
                                            if (it[0].is_new == 1) {
                                                binding.textViewWalletOfferRecommended1.visibility = View.VISIBLE
                                            }
                                            else {
                                                binding.textViewWalletOfferRecommended1.visibility = View.GONE
                                            }
                                        }

                                        if (it.size > 1){

                                            binding.cardView2.visibility = View.VISIBLE

                                            binding.textViewWalletOfferPrice2.text = "₹${it[1].total_amount}.00"
                                            binding.textViewWalletOfferCoins2.text = "${it[1].total_coin} Coins ${it[1].message}"

                                            if (it[1].discount == 0){
                                                binding.textViewWalletOfferDiscount2.visibility = View.GONE
                                            }
                                            else {
                                                binding.textViewWalletOfferDiscount2.visibility = View.VISIBLE
                                                binding.textViewWalletOfferDiscount2.text = "${it[1].discount}% Discount"
                                            }
                                            if (it[1].is_new == 1) {
                                                binding.textViewWalletOfferRecommended2.visibility = View.VISIBLE
                                            }
                                            else {
                                                binding.textViewWalletOfferRecommended2.visibility = View.GONE
                                            }
                                        }

                                        if (it.size > 2){

                                            binding.cardView3.visibility = View.VISIBLE

                                            binding.textViewWalletOfferPrice3.text = "₹${it[2].total_amount}.00"
                                            binding.textViewWalletOfferCoins3.text = "${it[2].total_coin} Coins ${it[2].message}"

                                            if (it[2].discount == 0){
                                                binding.textViewWalletOfferDiscount3.visibility = View.GONE
                                            }
                                            else {
                                                binding.textViewWalletOfferDiscount3.visibility = View.VISIBLE
                                                binding.textViewWalletOfferDiscount3.text = "${it[2].discount}% Discount"
                                            }
                                            if (it[2].is_new == 1) {
                                                binding.textViewWalletOfferRecommended3.visibility = View.VISIBLE
                                            }
                                            else {
                                                binding.textViewWalletOfferRecommended3.visibility = View.GONE
                                            }
                                        }

                                        if (it.size > 3){

                                            binding.cardView4.visibility = View.VISIBLE

                                            binding.textViewWalletOfferPrice4.text = "₹${it[3].total_amount}.00"
                                            binding.textViewWalletOfferCoins4.text = "${it[3].total_coin} Coins ${it[3].message}"

                                            if (it[3].discount == 0){
                                                binding.textViewWalletOfferDiscount4.visibility = View.GONE
                                            }
                                            else {
                                                binding.textViewWalletOfferDiscount4.visibility = View.VISIBLE
                                                binding.textViewWalletOfferDiscount4.text = "${it[3].discount}% Discount"
                                            }
                                            if (it[3].is_new == 1) {
                                                binding.textViewWalletOfferRecommended4.visibility = View.VISIBLE
                                            }
                                            else {
                                                binding.textViewWalletOfferRecommended4.visibility = View.GONE
                                            }
                                        }

                                    }
                                    catch (ex: Exception) {
                                        showSnackBarMessage("No wallet offers available. Try again later.")
                                    }
                                }
                            }
                            else {
                                showSnackBarMessage("No wallet offers available.")
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

    private fun refreshPaymentStatus() {

        val userId = SharedPref().getUserIDPref(this)

        paymentViewModel.refreshPaymentStatusWithOrderIdFun(
            user_id = userId ?: "",
            online_payment_id = online_payment_id,
            razorpay_order_id = paymentOrderId,
        )
    }

    private fun observePaymentFresh() {

        paymentViewModel.refreshPaymentStatusWithOrderIdObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                val onlinePayment = responseResult.onlinePayment

                                onlinePayment?.let {

                                    if (onlinePayment.status == "paid") {
                                        paymentSuccessfulScreenChanges()
                                    }
                                    else if (onlinePayment.status == "attempted") {
                                        paymentProcessingScreenChanges()
                                    }
                                    else {
                                        paymentFailedScreenChanges()
                                    }
                                }
                            }
                            else {
                                paymentProcessingScreenChanges()
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


    private fun buttonEnableUsingFirebase() {

        val db = FirebaseFirestore.getInstance()

        val currentVersionCode = packageManager.getPackageInfo(packageName, 0).versionCode

        Log.e(TAG, "checkAppUpdate: currentVersionCode $currentVersionCode", )

        db.collection("app_config").document("enable_buttons")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val pay_using_upi = document.getBoolean("pay_using_upi") ?: false
                    val pay_via_whatsapp = document.getBoolean("pay_via_whatsapp") ?: true

                    if (pay_using_upi) {
                        binding.btnBuyInWalletViaOnlinePayment.visibility = View.VISIBLE
                    }
                    else {
                        binding.btnBuyInWalletViaOnlinePayment.visibility = View.GONE
                    }

                    if(pay_via_whatsapp) {
                        binding.btnBuyInWalletViaWhatsapp.visibility = View.VISIBLE
                    }
                    else {
                        binding.btnBuyInWalletViaWhatsapp.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { exception ->
                binding.btnBuyInWalletViaOnlinePayment.visibility = View.GONE
                binding.btnBuyInWalletViaWhatsapp.visibility = View.VISIBLE

            }



    }

    private fun enableBuyButton(){

        binding.btnBuyInWalletViaOnlinePayment.isEnabled = true
        binding.btnBuyInWalletViaOnlinePayment.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_1)

        binding.btnBuyInWalletViaWhatsapp.isEnabled = true
        binding.btnBuyInWalletViaWhatsapp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_1)

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

    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.GONE
    }
    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.relativeLayoutParent, message, Snackbar.LENGTH_LONG).show()
    }

    private fun observeUserDetailsLocalData(){

        localUserDetailsViewModel.getUserDetailsByUserIdObserver.observe(this, Observer { _userDetails ->

            userDetails = _userDetails

        })

    }

    override fun onResume() {
        super.onResume()

        val userId = SharedPref().getUserIDPref(this)
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    private fun paymentInitiateScreenChanges() {

        binding.btnBuyInWalletViaOnlinePayment.visibility = View.GONE

        binding.constraintLayoutPaymentPagePaymentInitiating.visibility = View.VISIBLE
        binding.constraintLayoutPaymentPagePaymentSuccess.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentFailed.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentProcessing.visibility = View.GONE

        binding.textViewPaymentInitiatingNeedHelpPaymentPage.paintFlags = binding.textViewPaymentInitiatingNeedHelpPaymentPage.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.textViewPaymentInitiating.text = paymentInitiatingMessage
        binding.textViewPaymentInitiatingPayingText.text = "₹$selectedAmount is paying for \uD83E\uDE99 $selectedCoin Coins"
    }

    private fun paymentSuccessfulScreenChanges() {

        binding.constraintLayoutPaymentPagePaymentInitiating.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentSuccess.visibility = View.VISIBLE
        binding.constraintLayoutPaymentPagePaymentFailed.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentProcessing.visibility = View.GONE

        binding.circle.visibility = View.VISIBLE
        binding.successAnimationView.visibility = View.VISIBLE

        binding.textViewPaymentSuccessful.text = "Payment Successful"
        binding.textViewPaymentSuccessfulPayingText.text = "Paid ₹$selectedAmount for \uD83E\uDE99 $selectedCoin Coins"

        binding.textViewPaymentSuccessfulNeedHelpPaymentPage.paintFlags = binding.textViewPaymentSuccessfulNeedHelpPaymentPage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Relative Layout Animation
        val animation = AnimationUtils.loadAnimation(this@BuyCoinActivity, R.anim.circle_explosion_anim).apply {
            duration = 1800
            interpolator = AccelerateDecelerateInterpolator()
        }

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Do something when the animation starts (optional)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Set background color to green when the animation ends
                binding.constraintLayoutPaymentPagePaymentSuccess.setBackgroundColor(ContextCompat.getColor(this@BuyCoinActivity, R.color.color_4))
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // Do something if the animation repeats (optional)
            }
        })

        binding.circle.startAnimation(animation)

    }

    private fun paymentFailedScreenChanges(){

        binding.constraintLayoutPaymentPagePaymentInitiating.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentSuccess.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentFailed.visibility = View.VISIBLE
        binding.constraintLayoutPaymentPagePaymentProcessing.visibility = View.GONE

        binding.circleFailed.visibility = View.VISIBLE
        binding.failedAnimationView.visibility = View.VISIBLE

        binding.textViewPaymentFailed.text = "Payment Failed"
        binding.textViewPaymentFailedPayingText.text = "Payment failed. If any amount was deducted from your bank account, it will be refunded within 7 business days."

        binding.textViewPaymentFailedNeedHelpPaymentPage.paintFlags = binding.textViewPaymentFailedNeedHelpPaymentPage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Relative Layout Animation
        val animation = AnimationUtils.loadAnimation(this@BuyCoinActivity, R.anim.circle_explosion_anim).apply {
            duration = 1800
            interpolator = AccelerateDecelerateInterpolator()
        }

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Do something when the animation starts (optional)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Set background color to green when the animation ends
                binding.constraintLayoutPaymentPagePaymentFailed.setBackgroundColor(ContextCompat.getColor(this@BuyCoinActivity, R.color.color_1_1))
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // Do something if the animation repeats (optional)
            }
        })

        binding.circleFailed.startAnimation(animation)


    }

    private fun paymentProcessingScreenChanges(){

        binding.constraintLayoutPaymentPagePaymentInitiating.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentSuccess.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentFailed.visibility = View.GONE
        binding.constraintLayoutPaymentPagePaymentProcessing.visibility = View.VISIBLE

        binding.circleProcessing.visibility = View.VISIBLE
        binding.processingAnimationView.visibility = View.VISIBLE

        binding.textViewPaymentProcessing.text = "Payment Is Processing"
        binding.textViewPaymentProcessingPayingText.text = "Payment done of ₹$selectedAmount for \uD83E\uDE99 $selectedCoin Coins"
        binding.textViewPaymentProcessingBackPressedText.text = "Your payment is being processed. Please wait while we complete the transaction. You can refresh the payment status shortly."

        binding.textViewPaymentProcessingNeedHelpPaymentPage.paintFlags = binding.textViewPaymentProcessingNeedHelpPaymentPage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Relative Layout Animation
        val animation = AnimationUtils.loadAnimation(this@BuyCoinActivity, R.anim.circle_explosion_anim).apply {
            duration = 1800
            interpolator = AccelerateDecelerateInterpolator()
        }

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Do something when the animation starts (optional)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Set background color to green when the animation ends
                binding.constraintLayoutPaymentPagePaymentProcessing.setBackgroundColor(ContextCompat.getColor(this@BuyCoinActivity, R.color.color_7))
                binding.circleProcessing.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // Do something if the animation repeats (optional)
            }
        })

        binding.circleProcessing.startAnimation(animation)


    }



    /**
     * Razor Pay Payment Gateway Integrate
     */
    private fun createOrder() {

        paymentInitiatingMessage = "Payment is initiating..."

        val userId = SharedPref().getUserIDPref(this)

        paymentViewModel.createRazorPayOrderFun(
            user_id = userId ?: "",
            coins = selectedCoin,
            amount = "${selectedAmount}00"
        )
    }

    private fun observeCreateOrder() {

        paymentViewModel.createRazorPayOrderObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                val order_id = responseResult.onlinePayment?.order_id
                                online_payment_id = responseResult.onlinePayment?.online_payment_id ?: ""

                                order_id?.let {
                                    initialiseRazorpayPaymentGateway(it)
                                    paymentOrderId = order_id
                                }
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
                        //showProgressBar()
                        paymentInitiateScreenChanges()
                    }
                }

            }

        })

    }

    private fun observePaymentVerification() {

        paymentViewModel.verifyRazorPayPaymentObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseResult ->

                            if (responseResult.status == 200){

                                val onlinePayment = responseResult.onlinePayment

                                onlinePayment?.let {

                                    if (onlinePayment.status == "paid") {

                                        paymentSuccessfulScreenChanges()

                                    }
                                    else if (onlinePayment.status == "attempted") {

                                        paymentProcessingScreenChanges()

                                    }
                                    else {

                                        paymentFailedScreenChanges()
                                    }

                                }
                            }
                            else {

                                paymentProcessingScreenChanges()
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
                        paymentInitiateScreenChanges()
                    }
                }

            }

        })

    }

    private fun initialiseRazorpayPaymentGateway(order_id: String) {

        Checkout.preload(applicationContext)
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_poAXMhBwy9mqNZ")

        startPayment(checkout, order_id)
    }

    private fun startPayment(checkout : Checkout, order_id: String) {

        userDetails?.let {

            try {
                val options = JSONObject()
                options.put("name","Mutual Transfer")
                options.put("description","Buy Coins")
                options.put("image","https://themutualtransfer.in/mt_logo.png")
                options.put("theme.color", "#3399cc");
                options.put("currency","INR");
                options.put("order_id", order_id)
                options.put("amount","${selectedAmount}00")

                val retryObj = JSONObject();
                retryObj.put("enabled", true)
                retryObj.put("max_count", 4)
                options.put("retry", retryObj)

                val prefill = JSONObject()
                prefill.put("email", "${it.email}")
                prefill.put("contact","${it.phone}")

                options.put("prefill",prefill)
                checkout.open(this,options)

            }catch (e: Exception){
                Log.e(TAG, "Error in payment: "+ e.message )
                e.printStackTrace()
            }
        }

    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {

        val user_id = SharedPref().getUserIDPref(this)

        /*Log.e(TAG, "onPaymentSuccess: $razorpayPaymentId " )
        Log.e(TAG, "signature: ${paymentData?.signature} " )
        Log.e(TAG, "paymentId: ${paymentData?.paymentId} " )
        Log.e(TAG, "orderId: ${paymentData?.orderId} " )*/

        paymentInitiatingMessage = "Payment is processing. Please wait...!!"

        paymentViewModel.verifyRazorPayPaymentFun(
            user_id = user_id ?: "",
            online_payment_id = online_payment_id,
            razorpay_signature = paymentData?.signature ?: "",
            razorpay_order_id = paymentData?.orderId ?: "",
            razorpay_payment_id = paymentData?.paymentId ?: "",
        )

    }

    override fun onPaymentError(errorCode: Int, response: String?, paymentData: PaymentData?) {

        /*Log.e(TAG, "onPaymentError: $errorCode $response" )
        Log.e(TAG, "PaymentData: ${paymentData?.data} " )*/

        paymentFailedScreenChanges()

    }



}