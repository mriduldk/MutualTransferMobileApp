package com.codingstudio.mutualtransfer.ui.auth.newcomponent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.databinding.FragmentUserPreferenceNewBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.district.ModelDistrict
import com.codingstudio.mutualtransfer.ui.home.UserHomeActivityNew
import com.codingstudio.mutualtransfer.ui.search.viewmodel.district.DistrictViewModel
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsNewViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsNewViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetProfilePreferencesNew : Fragment() {

    private val TAG = "UserSetProfilePreferencesNew"
    private lateinit var localContext: Context
    private var _binding: FragmentUserPreferenceNewBinding?= null
    private val binding get() = _binding!!

    private var fragmentType : String? = ""

    private var listOfDistrict : List<String> ?= null

    private var selectedPreferenceDistrict1 = ""
    private var selectedPreferenceDistrict2 = ""
    private var selectedPreferenceDistrict3 = ""

    private val districtViewModel : DistrictViewModel by viewModels()
    private val userDetailsNewViewModel: UserDetailsNewViewModel by viewModels()
    private val localUserDetailsNewViewModel: LocalUserDetailsNewViewModel by viewModels()

    private var dataIsPopulated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserPreferenceNewBinding.inflate(inflater, container, false)
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

        setOnClickListeners()
        //getDistrictList()
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

        binding.btnSaveDistrictPreference.setOnClickListener {

            if (selectedPreferenceDistrict1.isNullOrEmpty()) {
                showSnackBarMessage("Select 1st Preference")
            } else if (selectedPreferenceDistrict2.isNullOrEmpty()) {
                showSnackBarMessage("Select 2nd Preference")
            } else if (selectedPreferenceDistrict3.isNullOrEmpty()) {
                showSnackBarMessage("Select 3rd Preference")
            } else {

                val user_id = SharedPref().getUserIDPref(requireContext())

                userDetailsNewViewModel.saveUserPreferredDistrictFun(
                    preferred_district_1 = selectedPreferenceDistrict1,
                    preferred_district_2 = selectedPreferenceDistrict2,
                    preferred_district_3 = selectedPreferenceDistrict3,
                    user_id = user_id ?: "",
                )

                subscribeToTopicForPushMessage()

            }
        }

    }

    /**
     *  Get District List From Search And Observe
     * */
    private fun getDistrictList(state_name: String) {

        districtViewModel.getDistrictByStateNameFun(state_name)

    }

    private fun observer(){

        districtViewModel.getDistrictByStateNameObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseDistricts ->

                            if (responseDistricts.status == 200){

                                responseDistricts.districts?.let { districtList ->
                                    setAutoCompleteTextViews(districtList)
                                }

                            }
                            else{
                                showSnackBarMessage(responseDistricts.message)
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

        userDetailsNewViewModel.saveUserPreferredDistrictObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserDetails ->

                            if (responseUserDetails.status == 200){

                                SharedPref().setBoolean(localContext, Constants.ProfileStep4, true)

                                if (fragmentType == Constants.GO_TO_BACK) {
                                    requireActivity().finish()
                                }
                                else{

                                    startActivity(Intent(localContext, UserHomeActivityNew::class.java))
                                    requireActivity().finish()
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

                if (!dataIsPopulated){
                    getDistrictList(userDetails.job_address_state ?: "")
                    dataIsPopulated = true
                }

                listOfDistrict?.let {

                    binding.spinnerFirstPreferenceDistrict.setText(userDetails.preferred_district_1)
                    binding.spinnerSecondPreferenceDistrict.setText(userDetails.preferred_district_2)
                    binding.spinnerThirdPreferenceDistrict.setText(userDetails.preferred_district_3)

                    selectedPreferenceDistrict1 = userDetails.preferred_district_1 ?: ""
                    selectedPreferenceDistrict2 = userDetails.preferred_district_2 ?: ""
                    selectedPreferenceDistrict3 = userDetails.preferred_district_3 ?: ""

                }
            }
        })

    }

    private fun setAutoCompleteTextViews(districtList : List<ModelDistrict>) {

        val districtNames : List<String> = districtList.map { it.district_name ?: "" }
        listOfDistrict = districtNames

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtNames)
        binding.spinnerFirstPreferenceDistrict.setAdapter(adapter)
        binding.spinnerFirstPreferenceDistrict.setOnItemClickListener { parent, view, position, id ->

            val selectedDistrictName = parent.getItemAtPosition(position).toString()
            val selectedDistrict = districtList.find { it.district_name == selectedDistrictName }

            selectedPreferenceDistrict1 = selectedDistrict?.district_name ?: ""
        }


        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtNames)
        binding.spinnerSecondPreferenceDistrict.setAdapter(adapter2)
        binding.spinnerSecondPreferenceDistrict.setOnItemClickListener { parent, view, position, id ->

            val selectedDistrictName = parent.getItemAtPosition(position).toString()
            val selectedDistrict = districtList.find { it.district_name == selectedDistrictName }

            selectedPreferenceDistrict2 = selectedDistrict?.district_name ?: ""
        }


        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtNames)
        binding.spinnerThirdPreferenceDistrict.setAdapter(adapter3)
        binding.spinnerThirdPreferenceDistrict.setOnItemClickListener { parent, view, position, id ->

            val selectedDistrictName = parent.getItemAtPosition(position).toString()
            val selectedDistrict = districtList.find { it.district_name == selectedDistrictName }

            selectedPreferenceDistrict3 = selectedDistrict?.district_name ?: ""
        }

        getLocalData()
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

        fun newInstance(fragmentType: String): UserSetProfilePreferencesNew {
            val fragment = UserSetProfilePreferencesNew()
            val args = Bundle()
            args.putString(ARG_FRAGMENT, fragmentType)
            fragment.arguments = args
            return fragment
        }

    }

    private fun subscribeToTopicForPushMessage() {

        Firebase.messaging.subscribeToTopic("PREFERRED_DISTRICT_${selectedPreferenceDistrict1.replace("\\s".toRegex(), "_")}")
        Firebase.messaging.subscribeToTopic("PREFERRED_DISTRICT_${selectedPreferenceDistrict2.replace("\\s".toRegex(), "_")}")
        Firebase.messaging.subscribeToTopic("PREFERRED_DISTRICT_${selectedPreferenceDistrict3.replace("\\s".toRegex(), "_")}")

    }

}