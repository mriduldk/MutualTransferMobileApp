package com.codingstudio.mutualtransfer.ui.auth.compoment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.FragmentUserDetailsThreeBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.UserDetails
import com.codingstudio.mutualtransfer.model.block.ModelBlock
import com.codingstudio.mutualtransfer.model.district.ModelDistrict
import com.codingstudio.mutualtransfer.ui.search.viewmodel.block.BlockViewModel
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
class UserSetProfileThreeFragment : Fragment() {

    private val TAG = "UserSetProfileThreeFragment"
    private lateinit var localContext: Context
    private var _binding: FragmentUserDetailsThreeBinding?= null
    private val binding get() = _binding!!

    private var listOfState = listOf<String>("Assam")
    private var listOfDistrict = listOf<String>()
    private var listOfBlock = listOf<String>()

    private var selectedDistrict = ""
    private var selectedDistrictModel : ModelDistrict ?= null
    private var selectedBlock = ""
    private var selectedState = ""

    private var fragmentType : String ?= ""

    private var districtSelectedBoolean = false
    private var blockSelectedBoolean = false

    /*private val userDetailsViewModel : UserDetailsViewModel by viewModels {
        UserDetailsViewModelFactory(requireActivity().application, (requireActivity().application as MainApplication).userDetailsRepository)
    }*/
    private val userDetailsViewModel: UserDetailsViewModel by viewModels()
    private val localUserDetailsViewModel: LocalUserDetailsViewModel by viewModels()
    private val districtViewModel : DistrictViewModel by viewModels()
    private val blockViewModel : BlockViewModel by viewModels()

    private var localUserDetails : UserDetails ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserDetailsThreeBinding.inflate(inflater, container, false)
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

        binding.btnSaveAndProceed.background.setColorFilter(ContextCompat.getColor(localContext, R.color.color_divider), PorterDuff.Mode.MULTIPLY)
        binding.btnSaveAndProceed.setTextColor(ContextCompat.getColor(localContext, R.color.text_color_regular))
        binding.btnSaveAndProceed.isEnabled = false

        setOnClickListeners()
        spinnerListeners()
        textChangeListeners()
        observer()
        observeUserDetailsLocalData()
        getLocalData()
        getDistrictList()
    }

    private fun getLocalData(){

        val userId = SharedPref().getUserIDPref(requireContext())
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    /**
     *  Get All District List
     * */
    private fun getDistrictList() {

        districtViewModel.getAllDistrictsFun()

    }

    /**
     *  Get Block List From District Name
     * */
    private fun getBlockListByDistrictName(modelDistrict: ModelDistrict) {

        blockViewModel.getBlocksByDistrictFun(modelDistrict.district_id)

    }

    private fun setOnClickListeners() {

        binding.imageViewBackButton.setOnClickListener {

            activity?.onBackPressed()

        }

        binding.btnSaveAndProceed.setOnClickListener {

            val user_id = SharedPref().getUserIDPref(requireContext())



            userDetailsViewModel.saveUserSchoolDetailsFun(
                school_address_block = selectedBlock,
                school_address_district = selectedDistrict,
                school_address_pin = binding.editTextSchoolAddressPin.text.toString().trim(),
                school_address_state = selectedState,
                school_address_vill = binding.editTextSchoolAddressVillage.text.toString().trim(),
                school_name = binding.editTextUserSchoolName.text.toString().trim(),
                udice_code = binding.editTextUDICECode.text.toString().trim(),
                user_id = user_id ?: "",
                amalgamation = if (binding.checkboxAmalgamated.isChecked) { 1 } else{ 0 }
            )

            subscribeToTopicForPushMessage()

        }

        binding.textViewBlockNotFoundFeedback.setOnClickListener {

            val phoneNumber = Constants.FEEDBACK_WHATSAPP_NUMBER
            val message = "*Block Not Available.*\n\nEnter District Name:\nEnter Block Name:"
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                intent.setPackage("com.whatsapp") // Regular WhatsApp package name
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observer(){

        userDetailsViewModel.saveUserSchoolDetailsObserver.observe(requireActivity(), Observer { res ->

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

                                    val fragment = UserSetProfilePreferences()
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

        districtViewModel.getAllDistrictsObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseDistricts ->

                            if (responseDistricts.status == 200){

                                responseDistricts.districts?.let { districtList ->
                                    setDistrictList(districtList)
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

        blockViewModel.getBlocksByDistrictObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseBlocks ->

                            if (responseBlocks.status == 200){

                                responseBlocks.blocks?.let { blockist ->
                                    setBlockList(blockist)
                                }

                            }
                            else{
                                showSnackBarMessage(responseBlocks.message)
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

                localUserDetails = it

                binding.editTextUserSchoolName.setText(userDetails.school_name)
                binding.editTextUDICECode.setText(userDetails.udice_code)
                binding.editTextSchoolAddressVillage.setText(userDetails.school_address_vill)
                binding.editTextSchoolAddressPin.setText(userDetails.school_address_pin)

                binding.spinnerSchoolAddressDistrict.setSelection(listOfDistrict.indexOf(userDetails.school_address_district))
                binding.spinnerSchoolAddressBlock.setSelection(listOfBlock.indexOf(userDetails.school_address_block))
                binding.spinnerSchoolAddressState.setSelection(listOfState.indexOf(userDetails.school_address_state))

                binding.checkboxAmalgamated.isChecked = userDetails.amalgamation == 1

            }

        })

    }

    private fun setDistrictList(districtList : List<ModelDistrict>) {

        val districtNames : List<String> = districtList.map { it.district_name ?: "" }
        listOfDistrict = districtNames

        val adapterDistrict = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfDistrict)
        binding.spinnerSchoolAddressDistrict.adapter = adapterDistrict

        binding.spinnerSchoolAddressDistrict.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedDistrict = listOfDistrict[position]
                selectedDistrictModel = districtList[position]
                districtSelectedBoolean = true
                saveButtonEnable()

                selectedDistrictModel?.let {
                    getBlockListByDistrictName(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {

            binding.spinnerSchoolAddressDistrict.setSelection(listOfDistrict.indexOf(it.school_address_district))
        }

    }

    private fun setBlockList(blockList : List<ModelBlock>) {

        val blockNames : List<String> = blockList.map { it.block_name ?: "" }
        listOfBlock = blockNames

        val adapterBlock = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfBlock)
        binding.spinnerSchoolAddressBlock.adapter = adapterBlock

        binding.spinnerSchoolAddressBlock.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedBlock = listOfBlock[position]
                blockSelectedBoolean = true
                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {
            binding.spinnerSchoolAddressBlock.setSelection(listOfBlock.indexOf(it.school_address_block))
        }

    }

    private fun spinnerListeners() {

        /*val adapterDistrict = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfDistrict)
        binding.spinnerSchoolAddressDistrict.adapter = adapterDistrict

        binding.spinnerSchoolAddressDistrict.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedDistrict = listOfDistrict[position]
                districtSelectedBoolean = true
                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }*/


        /*val adapterBlock = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfBlock)
        binding.spinnerSchoolAddressBlock.adapter = adapterBlock

        binding.spinnerSchoolAddressBlock.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedBlock = listOfBlock[position]
                blockSelectedBoolean = true
                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }*/

        val adapterState = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfState)
        binding.spinnerSchoolAddressState.adapter = adapterState

        binding.spinnerSchoolAddressState.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedState = listOfState[position]
                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


    }

    private fun textChangeListeners() {

        binding.editTextUserSchoolName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextUDICECode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextSchoolAddressVillage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextSchoolAddressPin.addTextChangedListener(object : TextWatcher {
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

        if (blockSelectedBoolean
            && districtSelectedBoolean
            && binding.editTextUserSchoolName.text.toString().isNotEmpty()
            && binding.editTextUDICECode.text.toString().isNotEmpty()
            && binding.editTextSchoolAddressVillage.text.toString().isNotEmpty()
            && binding.editTextSchoolAddressPin.text.toString().isNotEmpty()
        ) {
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

        const val SelectBlock = "Select Block"
        const val SelectDistrict = "Select District"

        private const val ARG_FRAGMENT = "ARG_FRAGMENT"

        fun newInstance(fragmentType: String): UserSetProfileThreeFragment {
            val fragment = UserSetProfileThreeFragment()
            val args = Bundle()
            args.putString(ARG_FRAGMENT, fragmentType)
            fragment.arguments = args
            return fragment
        }
    }

    private fun subscribeToTopicForPushMessage() {

        Firebase.messaging.subscribeToTopic("SCHOOL_DISTRICT_${selectedDistrict.replace("\\s".toRegex(), "_")}")

    }

}