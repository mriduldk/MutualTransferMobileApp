package com.codingstudio.mutualtransfer.ui.auth.newcomponent

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.FragmentUserDetailsNewOneBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsNewViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsNewViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetProfileOneFragmentNew : Fragment() {

    private val TAG = "UserSetProfileOneFragmentNew"
    private lateinit var localContext: Context
    private var _binding: FragmentUserDetailsNewOneBinding?= null
    private val binding get() = _binding!!
    private var genderSelected = false
    private var gender = ""
    private var fragmentType : String ?= ""
    private var userTypeName = ""
    private var userTypeId = ""

    private val userDetailsNewViewModel: UserDetailsNewViewModel by viewModels()
    private val localUserDetailsNewViewModel: LocalUserDetailsNewViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserDetailsNewOneBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            localContext = it
        }

        arguments?.let {
            fragmentType = it.getString(ARG_FRAGMENT)
        }

        userTypeName = SharedPref().getStringPref(localContext, Constants.user_type_name) ?: ""
        userTypeId = SharedPref().getStringPref(localContext, Constants.user_type_id) ?: ""

        binding.btnSaveAndProceed.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
        binding.btnSaveAndProceed.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
        binding.btnSaveAndProceed.isEnabled = false

        setOnClickListeners()
        textChangeListeners()
        observer()
        observeUserDetailsLocalData()

        getLocalData()

    }

    private fun getLocalData(){

        val userId = SharedPref().getUserIDPref(requireContext())
        localUserDetailsNewViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    private fun setOnClickListeners() {

        binding.imageViewBackButton.setOnClickListener {

            activity?.onBackPressed()

        }

        binding.btnSaveAndProceed.setOnClickListener {

            if (binding.editTextUserFullName.text.toString().trim().isNullOrEmpty()) {
                binding.editTextUserFullName.requestFocus()

            } else if (!genderSelected) {
                Snackbar.make(binding.relativeLayoutParent, "Please select gender", Snackbar.LENGTH_LONG).show()

            } else {

                val user_id = SharedPref().getUserIDPref(requireContext())

                userDetailsNewViewModel.saveUserPersonalInformationFun(
                    name = binding.editTextUserFullName.text.toString().trim(),
                    user_id = user_id ?: "",
                    email = binding.editTextUserEmail.text.toString().trim(),
                    gender = gender,
                    user_type = userTypeName,
                    user_type_id = userTypeId
                )

            }
        }

        binding.textViewGenderMale.setOnClickListener {

            binding.textViewGenderMale.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_selected)
            binding.textViewGenderFeMale.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_not_selected)
            binding.textViewGenderOthers.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_not_selected)

            binding.textViewGenderMale.setTextColor(ContextCompat.getColor(localContext, R.color.white))
            binding.textViewGenderFeMale.setTextColor(ContextCompat.getColor(localContext, R.color.color_text))
            binding.textViewGenderOthers.setTextColor(ContextCompat.getColor(localContext, R.color.color_text))

            genderSelected = true
            gender = "Male"
            saveButtonEnable()

        }

        binding.textViewGenderFeMale.setOnClickListener {

            binding.textViewGenderMale.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_not_selected)
            binding.textViewGenderFeMale.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_selected)
            binding.textViewGenderOthers.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_not_selected)

            binding.textViewGenderMale.setTextColor(ContextCompat.getColor(localContext, R.color.color_text))
            binding.textViewGenderFeMale.setTextColor(ContextCompat.getColor(localContext, R.color.white))
            binding.textViewGenderOthers.setTextColor(ContextCompat.getColor(localContext, R.color.color_text))

            genderSelected = true
            gender = "Female"
            saveButtonEnable()
        }

        binding.textViewGenderOthers.setOnClickListener {

            binding.textViewGenderMale.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_not_selected)
            binding.textViewGenderFeMale.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_not_selected)
            binding.textViewGenderOthers.background = ContextCompat.getDrawable(localContext, R.drawable.bg_gender_selected)

            binding.textViewGenderMale.setTextColor(ContextCompat.getColor(localContext, R.color.color_text))
            binding.textViewGenderFeMale.setTextColor(ContextCompat.getColor(localContext, R.color.color_text))
            binding.textViewGenderOthers.setTextColor(ContextCompat.getColor(localContext, R.color.white))

            genderSelected = true
            gender = "Other"
            saveButtonEnable()
        }

    }

    private fun observer(){

        userDetailsNewViewModel.saveUserPersonalInformationObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserDetails ->

                            if (responseUserDetails.status == 200){

                                if (fragmentType == Constants.GO_TO_BACK) {
                                    requireActivity().finish()
                                }
                                else{

                                    SharedPref().setBoolean(localContext, Constants.ProfileStep1, true)

                                    val fragment = UserSetProfileThreeFragmentNew()
                                    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                                    fragmentTransaction?.replace(R.id.fragment_container, fragment, TAG)
                                    fragmentTransaction?.addToBackStack(null)
                                    fragmentTransaction?.commit()
                                }

                            }
                            else{
                                showSnackBarMessage(responseUserDetails.message)
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

    private fun observeUserDetailsLocalData(){

        localUserDetailsNewViewModel.getUserDetailsByUserIdObserver.observe(requireActivity(), Observer { userDetails ->

            userDetails?.let {

                binding.editTextUserFullName.setText(userDetails.name)
                binding.editTextUserEmail.setText(userDetails.email)

                when (userDetails.gender) {
                    "Male" -> {
                        binding.textViewGenderMale.performClick()
                    }
                    "Female" -> {
                        binding.textViewGenderFeMale.performClick()
                    }
                    else -> {
                        binding.textViewGenderOthers.performClick()
                    }
                }
            }

        })

    }

    private fun textChangeListeners() {

        binding.editTextUserFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun saveButtonEnable() {

        if (genderSelected && binding.editTextUserFullName.text.toString().isNotEmpty()) {
            binding.btnSaveAndProceed.isEnabled = true
            binding.btnSaveAndProceed.background.setColorFilter(ContextCompat.getColor(localContext, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
            binding.btnSaveAndProceed.setTextColor(ContextCompat.getColor(localContext, R.color.white))
        }
        else {
            binding.btnSaveAndProceed.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
            binding.btnSaveAndProceed.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
            binding.btnSaveAndProceed.isEnabled = false
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


    companion object {
        private const val ARG_FRAGMENT = "ARG_FRAGMENT"

        fun newInstance(fragmentType: String): UserSetProfileOneFragmentNew {
            val fragment = UserSetProfileOneFragmentNew()
            val args = Bundle()
            args.putString(ARG_FRAGMENT, fragmentType)
            fragment.arguments = args
            return fragment
        }
    }


}