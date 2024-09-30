package com.codingstudio.mutualtransfer.ui.auth.compoment

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
import com.codingstudio.mutualtransfer.databinding.FragmentUserPreferenceBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.district.ModelDistrict
import com.codingstudio.mutualtransfer.ui.search.UserHomeActivity
import com.codingstudio.mutualtransfer.ui.search.viewmodel.district.DistrictViewModel
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetProfilePreferences : Fragment() {

    private val TAG = "UserSetProfilePreferences"
    private lateinit var localContext: Context
    private var _binding: FragmentUserPreferenceBinding?= null
    private val binding get() = _binding!!

    private var fragmentType : String? = ""

    private var listOfDistrict : List<String> ?= null

    private var selectedPreferenceDistrict1 = ""
    private var selectedPreferenceDistrict2 = ""
    private var selectedPreferenceDistrict3 = ""

    private var canProceed = false

    /*private val districtViewModel : DistrictViewModel by viewModels {
        DistrictViewModelFactory(requireActivity().application, (requireActivity().application as MainApplication).districtRepository, (requireActivity().application as MainApplication).historyRepository)
    }*/
    private val districtViewModel : DistrictViewModel by viewModels()

    /*private val userDetailsViewModel : UserDetailsViewModel by viewModels {
        UserDetailsViewModelFactory(requireActivity().application, (requireActivity().application as MainApplication).userDetailsRepository)
    }*/
    private val userDetailsViewModel: UserDetailsViewModel by viewModels()
    private val localUserDetailsViewModel: LocalUserDetailsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserPreferenceBinding.inflate(inflater, container, false)
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
        //getLocalData()
        getDistrictList()
        observer()
        observeUserDetailsLocalData()

    }

    private fun getLocalData(){

        val userId = SharedPref().getUserIDPref(requireContext())
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

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

                userDetailsViewModel.saveUserPreferredDistrictFun(
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
    private fun getDistrictList() {

        districtViewModel.getAllDistrictsFun()

    }

    private fun observer(){

        districtViewModel.getAllDistrictsObserver.observe(requireActivity(), Observer { res ->

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

        userDetailsViewModel.saveUserPreferredDistrictObserver.observe(requireActivity(), Observer { res ->

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

                                    startActivity(Intent(localContext, UserHomeActivity::class.java))
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

        localUserDetailsViewModel.getUserDetailsByUserIdObserver.observe(requireActivity(), Observer { userDetails ->

            userDetails?.let {

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
            //Toast.makeText(requireContext(), "Selected District: ${selectedDistrict?.district_name}", Toast.LENGTH_SHORT).show()

            selectedPreferenceDistrict1 = selectedDistrict?.district_name ?: ""
        }


        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtNames)
        binding.spinnerSecondPreferenceDistrict.setAdapter(adapter2)
        binding.spinnerSecondPreferenceDistrict.setOnItemClickListener { parent, view, position, id ->

            val selectedDistrictName = parent.getItemAtPosition(position).toString()
            val selectedDistrict = districtList.find { it.district_name == selectedDistrictName }
            //Toast.makeText(requireContext(), "Selected District: ${selectedDistrict?.district_name}", Toast.LENGTH_SHORT).show()

            selectedPreferenceDistrict2 = selectedDistrict?.district_name ?: ""
        }


        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtNames)
        binding.spinnerThirdPreferenceDistrict.setAdapter(adapter3)
        binding.spinnerThirdPreferenceDistrict.setOnItemClickListener { parent, view, position, id ->

            val selectedDistrictName = parent.getItemAtPosition(position).toString()
            val selectedDistrict = districtList.find { it.district_name == selectedDistrictName }
            //Toast.makeText(requireContext(), "Selected District: ${selectedDistrict?.district_name}", Toast.LENGTH_SHORT).show()

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

        const val SelectSchoolType = "Select School Type"
        const val SelectTeacherType = "Select Teacher Type"
        const val SelectSubject = "Select Subject"

        const val LP = "LP"
        const val UP = "UP"
        const val HighSchool = "High School"
        const val HigherSecondary = "Higher Secondary"

        const val AssistantTeacher = "Assistant Teacher"
        const val ScienceTeacher = "Science Teacher"
        const val SocialTeacher = "Social Teacher"
        const val SubjectTeacher = "Subject Teacher"

        private const val ARG_FRAGMENT = "ARG_FRAGMENT"

        fun newInstance(fragmentType: String): UserSetProfilePreferences {
            val fragment = UserSetProfilePreferences()
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