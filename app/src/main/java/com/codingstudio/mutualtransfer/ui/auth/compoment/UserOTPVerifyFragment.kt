package com.codingstudio.mutualtransfer.ui.auth.compoment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.FragmentUserOtpVerifyBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.User
import com.codingstudio.mutualtransfer.ui.auth.viewmodel.AuthViewModel
import com.codingstudio.mutualtransfer.ui.search.UserHomeActivity
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.msg91.sendotp.OTPWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@AndroidEntryPoint
class UserOTPVerifyFragment : Fragment() {

    private val TAG = "UserOTPVerifyFragment"
    private var FCM_TOKEN = ""
    private lateinit var localContext: Context
    private var _binding: FragmentUserOtpVerifyBinding?= null
    private val binding get() = _binding!!
    private var phoneNumber : String ?= null
    private var reqId : String ?= null

    /*private val authViewModel : AuthViewModel by viewModels {
        AuthViewModelFactory(requireActivity().application, (requireActivity().application as MainApplication).authRepository)
    }*/
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserOtpVerifyBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            localContext = it
        }

        phoneNumber = arguments?.getString(PHONE_NUMBER)
        reqId = arguments?.getString(REQUEST_ID)
        binding.textViewSubHeader.text = "We have sent you an OTP for verification on $phoneNumber this phone number."

        showSnackBarMessage("OTP sent")

        binding.btnOTPVerify.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
        binding.btnOTPVerify.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
        binding.btnOTPVerify.isEnabled = false

        setOnClickListeners()
        textChangeListeners()
        //observeSchoolAdoptionData()
        getFCMToken()
        askNotificationPermission()
        observer()
    }

    private fun setOnClickListeners() {

        binding.imageViewBackButton.setOnClickListener {

            activity?.onBackPressed()

        }

        binding.btnOTPVerify.setOnClickListener {

            if (binding.editTextOTP1.text.toString().trim().isNullOrEmpty()) {
                binding.editTextOTP1.requestFocus()

            } else if (binding.editTextOTP2.text.toString().trim().isNullOrEmpty()) {
                binding.editTextOTP2.requestFocus()

            } else if (binding.editTextOTP3.text.toString().trim().isNullOrEmpty()) {
                binding.editTextOTP3.requestFocus()

            } else if (binding.editTextOTP4.text.toString().trim().isNullOrEmpty()) {
                binding.editTextOTP4.requestFocus()

            } else {

                val otp = "${binding.editTextOTP1.text}${binding.editTextOTP2.text}${binding.editTextOTP3.text}${binding.editTextOTP4.text}"

                handleVerifyOtp(otp)

            }
        }

        binding.textViewChangePhoneNumber.setOnClickListener {

            activity?.onBackPressed()
        }

    }

    private fun textChangeListeners() {

        binding.editTextOTP1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (!text.isNullOrEmpty()) {
                    if (text.length == 1) {
                        binding.editTextOTP2.requestFocus()
                        binding.editTextOTP2.setSelectAllOnFocus(true)

                    } else {
                        binding.editTextOTP1.requestFocus()
                        binding.editTextOTP1.setSelectAllOnFocus(true)
                    }
                }
                else {
                    binding.editTextOTP1.requestFocus()
                    binding.editTextOTP1.setSelectAllOnFocus(true)
                }

                checkValidOTP()

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextOTP2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (!text.isNullOrEmpty()) {
                    if (text.length == 1) {
                        binding.editTextOTP3.requestFocus()
                        binding.editTextOTP3.setSelectAllOnFocus(true)

                    } else {
                        binding.editTextOTP1.requestFocus()
                        binding.editTextOTP1.setSelectAllOnFocus(true)
                    }
                }
                else {
                    binding.editTextOTP1.requestFocus()
                    binding.editTextOTP1.setSelectAllOnFocus(true)
                }

                checkValidOTP()

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextOTP3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (!text.isNullOrEmpty()) {
                    if (text.length == 1) {
                        binding.editTextOTP4.requestFocus()
                        binding.editTextOTP4.setSelectAllOnFocus(true)

                    } else {
                        binding.editTextOTP2.requestFocus()
                        binding.editTextOTP2.setSelectAllOnFocus(true)
                    }
                } else {
                    binding.editTextOTP2.requestFocus()
                    binding.editTextOTP2.setSelectAllOnFocus(true)
                }
                checkValidOTP()

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextOTP4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (!text.isNullOrEmpty()) {
                    if (text.length != 1) {
                        binding.editTextOTP3.requestFocus()
                        binding.editTextOTP3.setSelectAllOnFocus(true)

                    }
                }
                else {
                    binding.editTextOTP3.requestFocus()
                    binding.editTextOTP3.setSelectAllOnFocus(true)
                }

                checkValidOTP()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun observer(){

        authViewModel.otpVerifyObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseLogin ->

                            if (responseLogin.status == 200){

                                saveLoginDataInCache(responseLogin.user)

                                if (responseLogin.userDetails != null) {

                                    if (!responseLogin.userDetails.user_details_id.isNullOrEmpty()){

                                        startActivity(Intent(requireContext(), UserHomeActivity::class.java))
                                        activity?.finish()
                                    }
                                    else {
                                        val intent = Intent(localContext, UserAuthenticationActivity::class.java)
                                        intent.putExtra(
                                            UserAuthenticationActivity.NAVIGATION_TYPE,
                                            UserAuthenticationActivity.NAVIGATION_SET_PROFILE
                                        )
                                        startActivity(intent)

                                        activity?.finish()
                                    }
                                }
                                else {
                                    val intent = Intent(localContext, UserAuthenticationActivity::class.java)
                                    intent.putExtra(
                                        UserAuthenticationActivity.NAVIGATION_TYPE,
                                        UserAuthenticationActivity.NAVIGATION_SET_PROFILE
                                    )
                                    startActivity(intent)

                                    activity?.finish()
                                }
                            }
                            else{
                                showSnackBarMessage(responseLogin.message)
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

    private fun handleVerifyOtp(otp: String) {

        showProgressBar()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val widgetId = Constants.MSG91_WIDGET_ID
                val tokenAuth = Constants.MSG91_AUTH_TOKEN

                val identifier = "91$phoneNumber"

                val result = withContext(Dispatchers.IO) {
                    OTPWidget.verifyOTP(widgetId, tokenAuth, reqId ?: "", otp)
                }

                val jsonResponse = JSONObject(result)
                val type = jsonResponse.getString("type")
                val message = jsonResponse.getString("message")

                if(type == "success") {
                    authViewModel.otpVerifyFun(
                        fcm_token = FCM_TOKEN,
                        otp = otp,
                        phone = phoneNumber ?: ""
                    )
                }
                else{
                    showSnackBarMessage(message)
                    hideProgressBar()
                }

            } catch (e: Exception) {
                Log.e(TAG, "handleVerifyOtp: Exception $e")
            }
        }
    }

    private fun checkValidOTP() {

        val otpText = "${binding.editTextOTP1.text}${binding.editTextOTP2.text}${binding.editTextOTP3.text}${binding.editTextOTP4.text}"

        if (!otpText.isNullOrEmpty()) {
            if (otpText.length == 4) {
                binding.btnOTPVerify.isEnabled = true
                binding.btnOTPVerify.background.setColorFilter(ContextCompat.getColor(localContext, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
                binding.btnOTPVerify.setTextColor(ContextCompat.getColor(localContext, R.color.white))

            } else {
                binding.btnOTPVerify.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
                binding.btnOTPVerify.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
                binding.btnOTPVerify.isEnabled = false
            }
        } else {
            binding.btnOTPVerify.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
            binding.btnOTPVerify.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
            binding.btnOTPVerify.isEnabled = false
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

    private fun getFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener {

            if (it.isSuccessful){
                FCM_TOKEN = it.result
                Log.e(TAG, "FCM Token: $FCM_TOKEN")
            }
            else {
                Log.e(TAG, "Fetching FCM registration token failed" + it.exception)
            }
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(localContext, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    companion object {

        const val PHONE_NUMBER = "PHONE_NUMBER"
        const val REQUEST_ID = "REQUEST_ID"

        @JvmStatic
        fun newInstance(phoneNumber: String, reqId: String) : UserOTPVerifyFragment {
            return UserOTPVerifyFragment().apply {
                arguments = Bundle().apply {
                    putString(PHONE_NUMBER, phoneNumber)
                    putString(REQUEST_ID, reqId)
                }
            }
        }

    }


    private fun saveLoginDataInCache(user: User?){

        user?.let {
            SharedPref().setUserID(requireContext(), user.user_id)
            SharedPref().setString(requireContext(), Constants.user_phone, user.phone)
            SharedPref().setBoolean(requireContext(), Constants.IsLogIn, true)
        }

    }


}