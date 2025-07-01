package com.codingstudio.mutualtransfer.ui.auth.newcomponent

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.FragmentUserDetailsUserTypeBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.user_type.UserType
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserSetProfileOneFragment
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsViewModel
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import com.codingstudio.mutualtransfer.viewmodels.common.CommonViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetUserTypeFragment : Fragment() {

    private val TAG = "UserSetUserTypeFragment"
    private lateinit var localContext: Context
    private var _binding: FragmentUserDetailsUserTypeBinding?= null
    private val binding get() = _binding!!
    private var userTypeSelected = false
    private var userTypeName = ""
    private var userTypeId = ""
    private var fragmentType : String ?= ""

    private val userDetailsViewModel: UserDetailsViewModel by viewModels()
    private val localUserDetailsViewModel: LocalUserDetailsViewModel by viewModels()
    private val commonViewModel : CommonViewModel by viewModels()


    /*private val userTypeList = listOf(
        UserType(Constants.TETTeacher, R.drawable.teacher),
        UserType(Constants.PolicePersonnel, R.drawable.police),
        UserType(Constants.StateGovernmentEmployee, R.drawable.state_govt),
        UserType(Constants.CentralGovernmentEmployee, R.drawable.central_govt),
        UserType(Constants.HealthcareWorkers, R.drawable.healthcare),
        UserType(Constants.RailwayEmployees, R.drawable.railway),
        UserType(Constants.PostalDepartmentEmployees, R.drawable.postal),
        UserType(Constants.ParamedicalAndSupportStaff, R.drawable.paramedical),
        UserType(Constants.EngineersAndTechnicalStaff, R.drawable.engineer),
        UserType(Constants.UniversityCollege, R.drawable.college)
    )*/

    private lateinit var userTypeList: List<UserType>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserDetailsUserTypeBinding.inflate(inflater, container, false)
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

        getLocalData()
        setOnClickListeners()
        getUserType()
        observe()
    }

    private fun getLocalData(){

        userTypeName = SharedPref().getStringPref(localContext, Constants.user_type_name) ?: ""
        userTypeId = SharedPref().getStringPref(localContext, Constants.user_type_id) ?: ""

    }

    private fun getUserType(){

        commonViewModel.getAllUserTypesFun()

    }

    private fun observe() {

        commonViewModel.getAllUserTypesObserver.observe(requireActivity(), Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserType ->

                            if (responseUserType.status == 200){

                                responseUserType.userTypes?.let {
                                    userTypeList = it
                                    setGridView()
                                }

                            }
                            else{
                                showSnackBarMessage(responseUserType.message)
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

    private fun setOnClickListeners() {

        binding.imageViewBackButton.setOnClickListener {

            activity?.onBackPressed()

        }

        binding.btnSaveAndProceed.setOnClickListener {

            if (userTypeSelected) {
                SharedPref().setString(localContext, Constants.user_type_name, userTypeName)
                SharedPref().setString(localContext, Constants.user_type_id, userTypeId)

                if (userTypeId == Constants.TETTeacherID) {

                    val fragment = UserSetProfileOneFragment()
                    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.fragment_container, fragment, TAG)
                    fragmentTransaction?.commit()

                } else {

                    val fragment = UserSetProfileOneFragmentNew()
                    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.fragment_container, fragment, TAG)
                    fragmentTransaction?.commit()
                }
            }
            else {
                showSnackBarMessage("Please Select Your Service Type.")
            }
        }

    }

    companion object {
        private const val ARG_FRAGMENT = "ARG_FRAGMENT"

        fun newInstance(fragmentType: String): UserSetUserTypeFragment {
            val fragment = UserSetUserTypeFragment()
            val args = Bundle()
            args.putString(ARG_FRAGMENT, fragmentType)
            fragment.arguments = args
            return fragment
        }
    }

    private fun setGridView() {

        var selectedPosition = -1

        val adapter = object : ArrayAdapter<UserType>(localContext, R.layout.layout_user_type_select, userTypeList) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.layout_user_type_select, parent, false)
                val item = userTypeList[position]

                val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayoutParentUserType)
                val imageView = view.findViewById<ImageView>(R.id.imageViewUserType)
                val textView = view.findViewById<TextView>(R.id.textViewUserType)

                Glide.with(this@UserSetUserTypeFragment).load(item.user_type_icon).into(imageView)
                textView.text = item.user_type_name

                /* For IF present then set the data */
                if (item.user_type_id == userTypeId) {
                    selectedPosition = position
                }

                if (position == selectedPosition) {
                    linearLayout.background = ContextCompat.getDrawable(localContext, R.drawable.bg_selected)
                } else {
                    linearLayout.background = ContextCompat.getDrawable(localContext, R.drawable.bg_not_selected)
                }


                linearLayout.setOnClickListener {

                    selectedPosition = position
                    userTypeSelected = true
                    userTypeName = userTypeList[position].user_type_name
                    userTypeId = userTypeList[position].user_type_id

                    saveButtonEnable()

                    notifyDataSetChanged()
                }

                return view
            }

        }

        binding.gridView.adapter = adapter

    }

    private fun saveButtonEnable() {

        if (userTypeSelected) {
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

}