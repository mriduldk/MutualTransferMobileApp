package com.codingstudio.mutualtransfer.ui.auth.compoment

import android.Manifest
import android.content.Context
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
import com.codingstudio.mutualtransfer.databinding.FragmentUserPhoneNumberBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.auth.viewmodel.AuthViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.msg91.sendotp.OTPWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class UserPhoneNumberFragment : Fragment() {

    private val TAG = "UserPhoneNumberFragment"
    private var FCM_TOKEN = ""
    private lateinit var localContext: Context
    private var _binding: FragmentUserPhoneNumberBinding ?= null
    private val binding get() = _binding!!

    /*private val authViewModel : AuthViewModel by viewModels {
        AuthViewModelFactory(requireActivity().application, (requireActivity().application as MainApplication).authRepository)
    }*/
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_user_phone_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            localContext = it
        }

        binding.btnLoginContinue.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
        binding.btnLoginContinue.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
        binding.btnLoginContinue.isEnabled = false


        setOnClickListeners()

        //observeSchoolAdoptionData()
        getFCMToken()
        askNotificationPermission()
        observer()
    }

    private fun setOnClickListeners(){

        binding.btnLoginContinue.setOnClickListener {

            if (binding.editTextLoginMobileNumber.text.toString().trim().isNullOrEmpty()) {
                binding.editTextLoginMobileNumber.error = "Enter Your Phone Number"
                binding.editTextLoginMobileNumber.requestFocus()
            } else {

               authViewModel.checkUserPhoneNumberFun(
                    phone = binding.editTextLoginMobileNumber.text.toString().trim(),
                )
            }
        }

        binding.editTextLoginMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (!text.isNullOrEmpty()) {
                    if (text.length == 10) {
                        binding.btnLoginContinue.isEnabled = true
                        binding.btnLoginContinue.background.setColorFilter(ContextCompat.getColor(localContext, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
                        binding.btnLoginContinue.setTextColor(ContextCompat.getColor(localContext, R.color.white))

                    } else {
                        binding.btnLoginContinue.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
                        binding.btnLoginContinue.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
                        binding.btnLoginContinue.isEnabled = false
                    }
                } else {
                    binding.btnLoginContinue.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
                    binding.btnLoginContinue.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
                    binding.btnLoginContinue.isEnabled = false
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.imageViewBackBtn.setOnClickListener {

            activity?.finish()

        }

    }

    private fun observer(){

        authViewModel.checkUserPhoneNumberObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseLogin ->

                            if (responseLogin.status == 200){

                                handleSendOTP(binding.editTextLoginMobileNumber.text.toString().trim())

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

    private fun handleSendOTP(phoneNumber: String) {

        showProgressBar()

        CoroutineScope(Dispatchers.Main).launch {
            val widgetId = Constants.MSG91_WIDGET_ID
            val tokenAuth = Constants.MSG91_AUTH_TOKEN

            val identifier = "91$phoneNumber"

            try {

                val result = withContext(Dispatchers.IO) {
                    OTPWidget.sendOTP(widgetId, tokenAuth, identifier)
                }

                val jsonResponse = JSONObject(result)
                val type = jsonResponse.getString("type")
                val message = jsonResponse.getString("message")

                if(type == "success") {
                    goToOTPVerifyPage(phoneNumber = phoneNumber, reqId = message)
                }
                else{
                    showSnackBarMessage("Failed to sent OTP.")
                }
                Log.e(TAG, "handleSendOTP: $result" )
                hideProgressBar()

            } catch (e: Exception) {
                Log.e(TAG, "handleSendOTP: Exception $e" )
            }
        }
    }

    private fun goToOTPVerifyPage(phoneNumber: String, reqId: String) {

        val fragment = UserOTPVerifyFragment.newInstance(
            phoneNumber = phoneNumber,
            reqId = reqId
        )
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragment_container, fragment, TAG)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(localContext, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}