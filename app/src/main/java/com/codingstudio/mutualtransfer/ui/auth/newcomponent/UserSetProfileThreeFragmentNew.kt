package com.codingstudio.mutualtransfer.ui.auth.newcomponent

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
import com.codingstudio.mutualtransfer.databinding.FragmentUserDetailsNewThreeBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.UserDetailsNew
import com.codingstudio.mutualtransfer.model.block.ModelBlock
import com.codingstudio.mutualtransfer.model.current_role.ModelCurrentRole
import com.codingstudio.mutualtransfer.model.department.ModelDepartment
import com.codingstudio.mutualtransfer.model.district.ModelDistrict
import com.codingstudio.mutualtransfer.model.state.ModelState
import com.codingstudio.mutualtransfer.model.zone_division.ModelZoneDivision
import com.codingstudio.mutualtransfer.ui.search.viewmodel.block.BlockViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.district.DistrictViewModel
import com.codingstudio.mutualtransfer.ui.state.viewmodel.StateViewModel
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsNewViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsNewViewModel
import com.codingstudio.mutualtransfer.viewmodels.common.CommonViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetProfileThreeFragmentNew : Fragment() {

    private val TAG = "UserSetProfileThreeFragmentNew"
    private lateinit var localContext: Context
    private var _binding: FragmentUserDetailsNewThreeBinding?= null
    private val binding get() = _binding!!
    private var userTypeName = ""
    private var userTypeId = ""

    private var listOfState = listOf<String>()
    private var listOfDistrict = listOf<String>()
    private var listOfBlock = listOf<String>()
    private var listOfServiceType = listOf<String>("Regular","Contractual")
    private var listOfCurrentRole = listOf<String>()
    private var listOfDepartment = listOf<String>()
    private var listOfZoneDivision = listOf<String>()

    private var selectedDistrict = ""
    private var selectedBlock = ""
    private var selectedState = ""
    private var selectedServiceType = ""
    private var selectedCurrentRole = ""
    private var selectedDepartment = ""
    private var selectedZoneDivision = ""

    private var selectedDistrictModel : ModelDistrict ?= null
    private var selectedStateModel : ModelState ?= null

    private var fragmentType : String ?= ""

    private var stateSelectedBoolean = false
    private var districtSelectedBoolean = false
    private var blockSelectedBoolean = false
    private var serviceTypeSelectedBoolean = false
    private var currentRoleSelectedBoolean = false
    private var departmentSelectedBoolean = false
    private var zoneDivisionSelectedBoolean = false


    private val userDetailsNewViewModel: UserDetailsNewViewModel by viewModels()
    private val localUserDetailsNewViewModel: LocalUserDetailsNewViewModel by viewModels()
    private val stateViewModel : StateViewModel by viewModels()
    private val districtViewModel : DistrictViewModel by viewModels()
    private val blockViewModel : BlockViewModel by viewModels()
    private val commonViewModel : CommonViewModel by viewModels()

    private var localUserDetails : UserDetailsNew ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserDetailsNewThreeBinding.inflate(inflater, container, false)
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
        spinnerListeners()
        textChangeListeners()
        observer()
        observeUserDetailsLocalData()
        getLocalData()
        getStateList()
        getCurrentRoleDepartmentZoneDivisionList()
        //getDistrictList()
    }

    private fun getLocalData(){

        val userId = SharedPref().getUserIDPref(requireContext())
        localUserDetailsNewViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    /**
     *  Get Current Role, Department & Zone/Divisions based on User Type
     * */
    private fun getCurrentRoleDepartmentZoneDivisionList() {

        commonViewModel.getCurrentRolesByUserTypeFun(user_type = userTypeName)
        commonViewModel.getDepartmentsByUserTypeFun(user_type = userTypeName)
        commonViewModel.getZoneDivisionByUserTypeFun(user_type = userTypeName)

    }

    /**
     *  Get All State List
     * */
    private fun getStateList() {

        stateViewModel.getAllStatesFun()

    }

    /**
     *  Get All District List
     * */
    private fun getDistrictListByStateId(state_id: String) {

        districtViewModel.getDistrictByStateFun(state_id)

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

            userDetailsNewViewModel.saveUserJobDetailsFun(
                user_id = user_id ?: "",
                current_role = selectedCurrentRole,
                employee_code = binding.editTextEmployeeCode.text.toString().trim(),
                department = selectedDepartment,
                zone_division = selectedZoneDivision,
                service_type = selectedServiceType,
                current_organisation_name = binding.editTextCurrentOrganisation.text.toString().trim(),
                job_address_village = binding.editTextServiceAddressVillage.text.toString().trim(),
                job_address_district = selectedDistrict,
                job_address_block = selectedBlock,
                job_address_state = selectedState,
                job_address_pin = binding.editTextServiceAddressPin.text.toString().trim()
            )

            subscribeToTopicForPushMessage()

        }

        binding.textViewBlockNotFoundFeedback.setOnClickListener {

            val phoneNumber = Constants.FEEDBACK_WHATSAPP_NUMBER
            val message = "*Need Help (Service Details).*\n\n"
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

        userDetailsNewViewModel.saveUserJobDetailsObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserDetails ->

                            if (responseUserDetails.status == 200){

                                SharedPref().setString(localContext, Constants.state_name, selectedStateModel?.state_name)
                                SharedPref().setString(localContext, Constants.state_id, selectedStateModel?.state_id)


                                if (fragmentType == Constants.GO_TO_BACK) {
                                    requireActivity().finish()
                                }
                                else{

                                    SharedPref().setBoolean(localContext, Constants.ProfileStep3, true)

                                    val fragment = UserSetProfilePreferencesNew()
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

        stateViewModel.getAllStatesObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseStates ->

                            if (responseStates.status == 200){

                                responseStates.states?.let { stateList ->
                                    setStateList(stateList)
                                }

                            }
                            else{
                                showSnackBarMessage(responseStates.message)
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

        districtViewModel.getDistrictByStateObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseDistricts ->

                            if (responseDistricts.status == 200){

                                responseDistricts.districts?.let { districtList ->
                                    setDistrictList(districtList)
                                }?: run {
                                    setDistrictList(listOf<ModelDistrict>())
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

        commonViewModel.getCurrentRolesByUserTypeObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseCurrentRoles ->

                            if (responseCurrentRoles.status == 200){

                                responseCurrentRoles.roles?.let { currentRoleList ->
                                    setCurrentRoleList(currentRoleList)
                                }

                            }
                            else{
                                showSnackBarMessage(responseCurrentRoles.message)
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

        commonViewModel.getDepartmentsByUserTypeObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseDepartments ->

                            if (responseDepartments.status == 200){

                                responseDepartments.departments?.let { departmentList ->
                                    setDepartmentList(departmentList)
                                }

                            }
                            else{
                                showSnackBarMessage(responseDepartments.message)
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

        commonViewModel.getZoneDivisionByUserTypeObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseZoneDivision ->

                            if (responseZoneDivision.status == 200){

                                responseZoneDivision.zoneDivisions?.let { zoneDivisionList ->
                                    setZoneDivisionList(zoneDivisionList)
                                }

                            }
                            else{
                                showSnackBarMessage(responseZoneDivision.message)
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

                localUserDetails = it

                binding.editTextEmployeeCode.setText(userDetails.employee_code)
                binding.editTextCurrentOrganisation.setText(userDetails.current_organisation_name)

                binding.editTextServiceAddressVillage.setText(userDetails.job_address_village)
                binding.editTextServiceAddressPin.setText(userDetails.job_address_pin)

                binding.spinnerServiceAddressState.setSelection(listOfState.indexOf(userDetails.job_address_state))
                binding.spinnerServiceAddressDistrict.setSelection(listOfDistrict.indexOf(userDetails.job_address_district))
                binding.spinnerServiceAddressBlock.setSelection(listOfBlock.indexOf(userDetails.job_address_block))

            }

        })

    }


    private fun setStateList(stateList : List<ModelState>) {

        val stateNames : List<String> = stateList.map { it.state_name ?: "" }
        listOfState = stateNames

        val adapterState = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfState)
        binding.spinnerServiceAddressState.adapter = adapterState

        binding.spinnerServiceAddressState.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedState = listOfState[position]
                selectedStateModel = stateList[position]
                stateSelectedBoolean = true
                saveButtonEnable()

                selectedStateModel?.let {
                    getDistrictListByStateId(it.state_id ?: "")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {

            binding.spinnerServiceAddressState.setSelection(listOfState.indexOf(it.job_address_state))
        }

    }

    private fun setDistrictList(districtList : List<ModelDistrict>) {

        val districtNames : List<String> = districtList.map { it.district_name ?: "" }
        listOfDistrict = districtNames

        val adapterDistrict = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfDistrict)
        binding.spinnerServiceAddressDistrict.adapter = adapterDistrict

        binding.spinnerServiceAddressDistrict.onItemSelectedListener = object : OnItemSelectedListener{
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
                    //getBlockListByDistrictName(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {

            binding.spinnerServiceAddressDistrict.setSelection(listOfDistrict.indexOf(it.job_address_district))
        }

    }

    private fun setBlockList(blockList : List<ModelBlock>) {

        val blockNames : List<String> = blockList.map { it.block_name ?: "" }
        listOfBlock = blockNames

        val adapterBlock = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfBlock)
        binding.spinnerServiceAddressBlock.adapter = adapterBlock

        binding.spinnerServiceAddressBlock.onItemSelectedListener = object : OnItemSelectedListener{
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
            binding.spinnerServiceAddressBlock.setSelection(listOfBlock.indexOf(it.job_address_block))
        }

    }

    private fun setCurrentRoleList(currentRoleList : List<ModelCurrentRole>) {

        val currentRoleNames : List<String> = currentRoleList.map { it.current_role_name ?: "" }
        listOfCurrentRole = currentRoleNames

        val adapterCurrentRole = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfCurrentRole)
        binding.spinnerCurrentRole.adapter = adapterCurrentRole

        binding.spinnerCurrentRole.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedCurrentRole = listOfCurrentRole[position]
                currentRoleSelectedBoolean = true
                saveButtonEnable()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {

            binding.spinnerCurrentRole.setSelection(listOfCurrentRole.indexOf(it.current_role))
        }

    }

    private fun setDepartmentList(departmentList : List<ModelDepartment>) {

        val departmentNames : List<String> = departmentList.map { it.department_name ?: "" }
        listOfDepartment = departmentNames

        val adapterDepartment = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfDepartment)
        binding.spinnerDepartment.adapter = adapterDepartment

        binding.spinnerDepartment.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedDepartment = listOfDepartment[position]
                departmentSelectedBoolean = true
                saveButtonEnable()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {

            binding.spinnerDepartment.setSelection(listOfDepartment.indexOf(it.department))
        }

    }

    private fun setZoneDivisionList(zoneDivisionList : List<ModelZoneDivision>) {

        listOfZoneDivision = zoneDivisionList.map { it.zone_division_name ?: "" }

        val adapterZoneDivision = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfZoneDivision)
        binding.spinnerZoneDivision.adapter = adapterZoneDivision

        binding.spinnerZoneDivision.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedZoneDivision = listOfZoneDivision[position]
                zoneDivisionSelectedBoolean = true
                saveButtonEnable()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        localUserDetails?.let {

            binding.spinnerZoneDivision.setSelection(listOfZoneDivision.indexOf(it.zone_division))
        }

    }



    private fun spinnerListeners() {

        val adapterServiceType = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfServiceType)
        binding.spinnerServiceType.adapter = adapterServiceType

        binding.spinnerServiceType.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedServiceType = listOfServiceType[position]
                serviceTypeSelectedBoolean = true
                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


    }

    private fun textChangeListeners() {

        binding.editTextEmployeeCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextCurrentOrganisation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextServiceAddressVillage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                saveButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.editTextServiceAddressPin.addTextChangedListener(object : TextWatcher {
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

        if (districtSelectedBoolean
            && stateSelectedBoolean
            && departmentSelectedBoolean
            && currentRoleSelectedBoolean
            && serviceTypeSelectedBoolean
            /*&& zoneDivisionSelectedBoolean*/
            /*&& binding.editTextEmployeeCode.text.toString().isNotEmpty()*/
            && binding.editTextCurrentOrganisation.text.toString().isNotEmpty()
            && binding.editTextServiceAddressVillage.text.toString().isNotEmpty()
            && binding.editTextServiceAddressPin.text.toString().isNotEmpty()
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

        fun newInstance(fragmentType: String): UserSetProfileThreeFragmentNew {
            val fragment = UserSetProfileThreeFragmentNew()
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