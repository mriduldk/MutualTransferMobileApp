package com.codingstudio.mutualtransfer.ui.auth.compoment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.FragmentUserDetailsTwoBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsViewModel
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsViewModelFactory
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class UserSetProfileTwoFragment : Fragment() {

    private val TAG = "UserSetProfileOneFragment"
    private lateinit var localContext: Context
    private var _binding: FragmentUserDetailsTwoBinding?= null
    private val binding get() = _binding!!

    private var listOfSchoolType = listOf<String>(
        SelectSchoolType, LP,
        UP, HighSchool, HigherSecondary
    )
    private var listOfLPTeacherType = listOf<String>(AssistantTeacher)
    private var listOfUPTeacherType = listOf<String>(SelectTeacherType, AssistantTeacher, ScienceTeacher)
    private var listOfHighSchoolTeacherType = listOf<String>(SelectTeacherType, ScienceTeacher, SocialTeacher)
    private var listOfHigherSecondaryTeacherType = listOf<String>(SubjectTeacher)

    //private var listOfSubjects = listOf<String>(SelectSubject, "Biology", "Chemistry", "Physics", "Math", )
    private var listOfSubjects = listOf<String>(SelectSubject,"Biology","Chemistry","Physics","Math","Commerce","Advance Assamese","Advance Bengali","Arabic","Assamese","Bengali","Bodo","Botany","Economics","Education","English","Geography","Geology","Hindi","History","Logic & Philosophy","Mathematics","Nepali","Persian","Political Science","Sanskrit","Sociology","Statistics","Zoology")

    private var selectedSchoolType = ""
    private var selectedTeacherType = ""
    private var selectedSubject = ""

    private var fragmentType : String? = ""

    private var checkDropDownEnableButtonBoolean = false


    /*private val userDetailsViewModel : UserDetailsViewModel by viewModels {
        UserDetailsViewModelFactory(requireActivity().application, (requireActivity().application as MainApplication).userDetailsRepository)
    }*/
    private val userDetailsViewModel: UserDetailsViewModel by viewModels()
    private val localUserDetailsViewModel: LocalUserDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserDetailsTwoBinding.inflate(inflater, container, false)
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

    }

    private fun getLocalData(){

        val userId = SharedPref().getUserIDPref(requireContext())
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    private fun setOnClickListeners() {

        binding.imageViewBackButton.setOnClickListener {

            activity?.onBackPressed()

        }

        binding.btnSaveAndProceed.setOnClickListener {

            /*if (binding.editTextUserEmployeeCode.text.toString().trim().isNullOrEmpty()) {
                binding.editTextUserEmployeeCode.requestFocus()
                binding.editTextUserEmployeeCode.error = "Required Field"

            } else {

                val user_id = SharedPref().getUserIDPref(requireContext())

                userDetailsViewModel.saveUserEmployeeDetailsFun(
                    employee_code = binding.editTextUserEmployeeCode.text.toString().trim(),
                    school_type = selectedSchoolType,
                    teacher_type = selectedTeacherType,
                    user_id = user_id ?: "",
                    subject_type = selectedSubject
                )

            }*/

            val user_id = SharedPref().getUserIDPref(requireContext())

            userDetailsViewModel.saveUserEmployeeDetailsFun(
                employee_code = binding.editTextUserEmployeeCode.text.toString(),
                school_type = selectedSchoolType,
                teacher_type = selectedTeacherType,
                user_id = user_id ?: "",
                subject_type = selectedSubject
            )
        }

        binding.textViewSubjectNotFoundFeedback.setOnClickListener {

            val phoneNumber = Constants.FEEDBACK_WHATSAPP_NUMBER
            val message = "*Subject Not Available.*\n\nEnter Subject Name:"
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

        userDetailsViewModel.saveUserEmployeeDetailsObserver.observe(requireActivity(), Observer { res ->

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

                                    val fragment = UserSetProfileThreeFragment()
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

        localUserDetailsViewModel.getUserDetailsByUserIdObserver.observe(requireActivity(), Observer { userDetails ->

            userDetails?.let {

                binding.editTextUserEmployeeCode.setText(userDetails.employee_code)

                when (userDetails.school_type) {
                    LP -> {
                        binding.spinnerSchoolType.setSelection(listOfSchoolType.indexOf(LP))
                        binding.spinnerTeacherType.setSelection(listOfLPTeacherType.indexOf(userDetails.teacher_type))
                    }
                    UP -> {
                        binding.spinnerSchoolType.setSelection(listOfSchoolType.indexOf(UP))
                        //binding.spinnerTeacherType.setSelection(listOfUPTeacherType.indexOf(userDetails.teacher_type))
                        binding.spinnerTeacherType.post {
                            binding.spinnerTeacherType.setSelection(listOfUPTeacherType.indexOf(userDetails.teacher_type))
                        }
                    }
                    HighSchool -> {
                        binding.spinnerSchoolType.setSelection(listOfSchoolType.indexOf(HighSchool))
                        //binding.spinnerTeacherType.setSelection(listOfHighSchoolTeacherType.indexOf(userDetails.teacher_type))
                        binding.spinnerTeacherType.post {
                            binding.spinnerTeacherType.setSelection(listOfHighSchoolTeacherType.indexOf(userDetails.teacher_type))
                        }
                    }
                    HigherSecondary -> {
                        binding.spinnerSchoolType.setSelection(listOfSchoolType.indexOf(HigherSecondary))
                        binding.spinnerTeacherType.setSelection(listOfHigherSecondaryTeacherType.indexOf(userDetails.teacher_type))
                        binding.spinnerTeacherSubject.setSelection(listOfSubjects.indexOf(userDetails.subject_type))
                    }
                    else -> {

                    }
                }
            }

        })

    }

    private fun spinnerListeners() {

        val adapterSchoolType = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfSchoolType)
        binding.spinnerSchoolType.adapter = adapterSchoolType

        binding.spinnerSchoolType.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                checkDropDownEnableButtonBoolean = false
                selectedSchoolType = listOfSchoolType[position]

                when(position) {
                    1 -> {
                        val adapterTeacherType = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfLPTeacherType)
                        binding.spinnerTeacherType.adapter = adapterTeacherType
                        binding.linearLayoutTeacherType.visibility = View.VISIBLE
                    }
                    2 -> {
                        val adapterTeacherType = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfUPTeacherType)
                        binding.spinnerTeacherType.adapter = adapterTeacherType
                        binding.linearLayoutTeacherType.visibility = View.VISIBLE
                    }
                    3 -> {
                        val adapterTeacherType = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfHighSchoolTeacherType)
                        binding.spinnerTeacherType.adapter = adapterTeacherType
                        binding.linearLayoutTeacherType.visibility = View.VISIBLE
                    }
                    4 -> {
                        val adapterTeacherType = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfHigherSecondaryTeacherType)
                        binding.spinnerTeacherType.adapter = adapterTeacherType
                        binding.linearLayoutTeacherType.visibility = View.VISIBLE
                    }
                    0 -> {
                        binding.linearLayoutTeacherType.visibility = View.GONE
                    }
                }

                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.spinnerTeacherType.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when(selectedSchoolType) {
                    LP -> {
                        selectedTeacherType = listOfLPTeacherType[position]
                        checkDropDownEnableButtonBoolean = true
                        binding.linearLayoutTeacherSubject.visibility = View.GONE
                    }
                    UP -> {
                        selectedTeacherType = listOfUPTeacherType[position]

                        checkDropDownEnableButtonBoolean = selectedTeacherType != SelectTeacherType
                        binding.linearLayoutTeacherSubject.visibility = View.GONE
                    }
                    HighSchool -> {
                        selectedTeacherType = listOfHighSchoolTeacherType[position]
                        checkDropDownEnableButtonBoolean = selectedTeacherType != SelectTeacherType
                        binding.linearLayoutTeacherSubject.visibility = View.GONE
                    }
                    HigherSecondary -> {
                        selectedTeacherType = SubjectTeacher
                        checkDropDownEnableButtonBoolean = false

                        binding.linearLayoutTeacherSubject.visibility = View.VISIBLE
                    }
                    SelectSchoolType -> {
                        checkDropDownEnableButtonBoolean = false
                        binding.linearLayoutTeacherSubject.visibility = View.GONE
                        selectedTeacherType = ""
                    }
                }

                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        val adapterSubjects = ArrayAdapter(localContext, android.R.layout.simple_spinner_dropdown_item, listOfSubjects)
        binding.spinnerTeacherSubject.adapter = adapterSubjects

        binding.spinnerTeacherSubject.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedSubject = listOfSubjects[position]
                checkDropDownEnableButtonBoolean = selectedSubject != SelectSubject

                saveButtonEnable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


    }

    private fun textChangeListeners() {

        binding.editTextUserEmployeeCode.addTextChangedListener(object : TextWatcher {
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

        if (checkDropDownEnableButtonBoolean) {
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

        fun newInstance(fragmentType: String): UserSetProfileTwoFragment {
            val fragment = UserSetProfileTwoFragment()
            val args = Bundle()
            args.putString(ARG_FRAGMENT, fragmentType)
            fragment.arguments = args
            return fragment
        }
    }

}