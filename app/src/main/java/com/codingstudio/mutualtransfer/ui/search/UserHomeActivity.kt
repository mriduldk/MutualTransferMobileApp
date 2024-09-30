package com.codingstudio.mutualtransfer.ui.search

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.databinding.ActivityMainBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ModelSearch
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserAuthenticationActivity
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserDetailsChangeActivity
import com.codingstudio.mutualtransfer.ui.payment.AlertConfirmationDialog
import com.codingstudio.mutualtransfer.ui.profile.ReferAndEarnActivity
import com.codingstudio.mutualtransfer.ui.profile.UserProfileActivity
import com.codingstudio.mutualtransfer.ui.recently_viewed.RecentlyViewedActivity
import com.codingstudio.mutualtransfer.ui.userDetails.viewmodel.UserDetailsViewModel
import com.codingstudio.mutualtransfer.ui.wallet.OnlinePaymentActivity
import com.codingstudio.mutualtransfer.ui.wallet.WalletActivity
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.codingstudio.mutualtransfer.viewmodels.LocalUserDetailsViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserHomeActivity : AppCompatActivity() {

    private val TAG = "UserHomeActivity"
    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private var switchLocalChange = false

    private lateinit var modelSearch : ModelSearch
    private val localUserDetailsViewModel: LocalUserDetailsViewModel by viewModels()
    private val userDetailsViewModel : UserDetailsViewModel by viewModels()

    private lateinit var appUpdateManager : AppUpdateManager
    private val REQUEST_NOTIFICATION_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelSearch = ModelSearch()

        //startActivity(Intent(this@UserHomeActivity, HomeNavigationActivity::class.java))

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlue)
        val decorView = window.decorView
        decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()



        /**
         * For Drawer Navigation View
         */
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayoutHome, binding.constraintLayoutToolbarHome,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayoutHome.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = Color.WHITE
        binding.constraintLayoutToolbarHome.navigationIcon = ContextCompat.getDrawable(this, R.drawable.round_menu_24)

        //If Item Of DrawerMenu is clicked
        ///binding.navigationView.setNavigationItemSelectedListener(this)


        binding.navigationView.setNavigationItemSelectedListener { item ->

            Log.e(TAG, "setNavigationItemSelectedListener : ${item.itemId}")

            when (item.itemId) {
                R.id.nav_menu_profile -> {
                    val intent = Intent(this@UserHomeActivity, UserProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_menu_saved_profiles -> {
                    //val intent = Intent(this@UserHomeActivity, Saver::class.java)
                    //startActivity(intent)
                }
                R.id.nav_menu_recently_viewed -> {
                    val intent = Intent(this@UserHomeActivity, RecentlyViewedActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_menu_wallet -> {
                    val intent = Intent(this@UserHomeActivity, WalletActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_menu_online_payment -> {
                    val intent = Intent(this@UserHomeActivity, OnlinePaymentActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_menu_refer -> {
                    val intent = Intent(this@UserHomeActivity, ReferAndEarnActivity::class.java)
                    startActivity(intent)
                }
            }

            binding.drawerLayoutHome.closeDrawer(GravityCompat.START)
            true
        }


        onClickListeners()
        observeUserDetailsLocalData()
        observer()
        permissionForPushNotification()

        checkAdEnableStatus()
        checkAppUpdate()
    }

    private fun onClickListeners() {

        binding.textViewSelectDistrict.setOnClickListener {

            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_DISTRICT)
            getSearchedContent.launch(intent)
        }

        binding.textViewSelectBlock.setOnClickListener {

            if (!modelSearch.searchDistrictId.isNullOrEmpty()){

                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_BLOCK)
                intent.putExtra(SearchActivity.SEARCH_DISTRICT_ID, modelSearch.searchDistrictId)
                getSearchedContent.launch(intent)
            }
            else{
                showSnackBarMessage("Please select district first.")
            }
        }

        binding.textViewSelectSchool.setOnClickListener {

            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_SCHOOL)
            getSearchedContent.launch(intent)
        }

        binding.textViewSearchBtn.setOnClickListener {

            if (modelSearch.searchDistrictId.isNullOrEmpty()){
                showSnackBarMessage("Please Select District")
            }
            else {

                goToSearchActivity()

            }
        }

        binding.textViewSearchNoteTeacherPostChange.setOnClickListener {

            val intent = Intent(this, UserDetailsChangeActivity::class.java)
            intent.putExtra(UserDetailsChangeActivity.NAVIGATION_FRAGMENT, UserDetailsChangeActivity.FRAGMENT_TWO)
            startActivity(intent)

        }

        binding.textViewReferralTextHome.setOnClickListener {

            val intent = Intent(this, ReferAndEarnActivity::class.java)
            startActivity(intent)

        }

        binding.imageViewGoToReferralPage.setOnClickListener {

            val intent = Intent(this, ReferAndEarnActivity::class.java)
            startActivity(intent)

        }

        binding.switchActivelyLooking.setOnCheckedChangeListener { compoundButton, b ->

            if (switchLocalChange) {

                if (b) {

                    AlertConfirmationDialog.Builder(this)
                        .setTitle("Enable Actively Looking")
                        .setMessage("Are you sure you want to enable this option.")
                        .setPositiveButtonText("Enable")
                        .setNegativeButtonText("Cancel")
                        .setCancelable(false)
                        .onConfirm {
                            activelyLookingStatusChange(1)
                        }
                        .onCancel {
                            switchLocalChange = false
                            binding.switchActivelyLooking.isChecked = false
                            switchLocalChange = true
                        }
                        .build()
                        .show()

                }
                else {
                    AlertConfirmationDialog.Builder(this)
                        .setTitle("Disable Actively Looking")
                        .setMessage("Are you sure you want to disable this option. If you disable this option, your profile will not be visible in search results.")
                        .setPositiveButtonText("Disable")
                        .setNegativeButtonText("Cancel")
                        .setCancelable(false)
                        .onConfirm {
                            activelyLookingStatusChange(0)
                        }
                        .onCancel {
                            switchLocalChange = false
                            binding.switchActivelyLooking.isChecked = true
                            switchLocalChange = true
                        }
                        .build()
                        .show()
                }
            }


        }

        binding.textViewDownloadButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.codingstudio.mutualtransfer")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }

        binding.cardViewPushNotification.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }

        }

        binding.navInsta.setOnClickListener {

            val packageManager = packageManager
            try {
                packageManager.getPackageInfo("com.instagram.android", 0)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://instagram.com/_u/themutualtransfer") // Deep link to Instagram profile
                intent.setPackage("com.instagram.android")
                startActivity(intent)
            }
            catch (e: PackageManager.NameNotFoundException) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/themutualtransfer"))
                startActivity(intent)
            }

        }

        binding.navFacebook.setOnClickListener {

            val packageManager = packageManager
            try {

                packageManager.getPackageInfo("com.facebook.katana", 0)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.facebook.com/share/HFGA6P54B6VoqPD1/?mibextid=qi2Omg") // Deep link for Facebook
                intent.setPackage("com.facebook.katana")
                startActivity(intent)
            }
            catch (e: PackageManager.NameNotFoundException) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share/HFGA6P54B6VoqPD1/?mibextid=qi2Omg"))
                startActivity(intent)
            }

        }

        binding.imageViewBtnClearBlock.setOnClickListener {

            modelSearch.searchBlockText = ""
            modelSearch.searchBlockId = ""

            binding.textViewSelectBlock.text = ""
            binding.imageViewBtnClearBlock.visibility = View.GONE
        }

    }

    private fun activelyLookingStatusChange(is_actively_looking : Int) {

        val userId = SharedPref().getUserIDPref(this)

        userDetailsViewModel.changeActivelyLookingStatusFun(
            is_actively_looking = is_actively_looking,
            user_id = userId ?: ""
        )
    }

    private fun getLocalData(){

        val userId = SharedPref().getUserIDPref(this)
        localUserDetailsViewModel.getUserDetailsByUserIdFun(userId ?: "")

    }

    private fun observeUserDetailsLocalData(){

        localUserDetailsViewModel.getUserDetailsByUserIdObserver.observe(this, Observer { userDetails ->

            var teacherPostName = ""

            if (userDetails == null){
                getUserDetailsByUserIdAndPhone()
            }
            else if(userDetails.fk_user_id == null) {
                getUserDetailsByUserIdAndPhone()
            }

            userDetails?.let { it ->

                it.teacher_type?.let { _teacher_type ->
                    teacherPostName = _teacher_type
                }

                it.school_type?.let { _school_type ->
                    teacherPostName += "( $_school_type )"
                }

                binding.switchActivelyLooking.isChecked = it.is_actively_looking == 1

                switchLocalChange = true
            }

            binding.textViewSearchNoteTeacherPost.text = teacherPostName

        })


        binding.textViewVersionCode.text = "Version - ${BuildConfig.VERSION_NAME}"

    }

    private fun observer(){

        userDetailsViewModel.changeActivelyLookingStatusObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserDetails ->

                            if (responseUserDetails.status == 200){

                                showSnackBarMessage(responseUserDetails.message)
                                getLocalData()
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

        userDetailsViewModel.getUserDetailsByIdObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseUserDetails ->

                            if (responseUserDetails.status == 200){
                                getLocalData()
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
                                    //showSnackBarMessage(errorMessage)
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

    private val getSearchedContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result : ActivityResult ->

        if (result.resultCode == Activity.RESULT_OK) {

            val resultType = result.data?.extras?.getString(SEARCH_RESULT_TYPE)
            val resultText = result.data?.extras?.getString(SEARCH_RESULT_TEXT)
            val resultId = result.data?.extras?.getString(SEARCH_RESULT_ID)

            when(resultType) {
                SearchActivity.SEARCH_TYPE_DISTRICT -> {
                    binding.textViewSelectDistrict.text = resultText

                    modelSearch.searchDistrictText = resultText
                    modelSearch.searchDistrictId = resultId

                    modelSearch.searchBlockText = ""
                    modelSearch.searchBlockId = ""

                    binding.textViewSelectBlock.text = ""
                    binding.textViewSelectBlock.hint = "Select Block ( Optional )"
                    binding.imageViewBtnClearBlock.visibility = View.GONE

                }
                SearchActivity.SEARCH_TYPE_BLOCK -> {
                    binding.textViewSelectBlock.text = resultText

                    modelSearch.searchBlockText = resultText
                    modelSearch.searchBlockId = resultId

                    binding.imageViewBtnClearBlock.visibility = View.VISIBLE
                }
                SearchActivity.SEARCH_TYPE_SCHOOL -> {
                    binding.textViewSelectSchool.text = resultText

                    modelSearch.searchSchoolText = resultText
                    modelSearch.searchSchoolId = resultId
                }
            }

        }
        else{
            showSnackBarMessage("Nothing Selected" )
        }
    }

    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.relativeLayoutParent, message, Snackbar.LENGTH_LONG).show()
    }

    private fun permissionForPushNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }


    }

    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.GONE
    }

    companion object {

        const val SEARCH_TYPE = "SEARCH_TYPE"
        const val SEARCH_RESULT_TYPE = "SEARCH_RESULT_TYPE"
        const val SEARCH_RESULT_TEXT = "SEARCH_RESULT_TEXT"
        const val SEARCH_RESULT_ID = "SEARCH_RESULT_ID"

    }

    override fun onResume() {
        super.onResume()

        getLocalData()
        checkNotificationPermission()

        val isLogIn = SharedPref().getBooleanPref(this, Constants.IsLogIn)
        if (!isLogIn) {
            val intent = Intent(this, UserAuthenticationActivity::class.java)
            intent.putExtra(UserAuthenticationActivity.NAVIGATION_TYPE, UserAuthenticationActivity.NAVIGATION_LOGIN)
            startActivity(intent)
            finish()
        }
    }

    private fun getUserDetailsByUserIdAndPhone() {

        val user_id = SharedPref().getStringPref(this, Constants.user_id)
        val user_phone = SharedPref().getStringPref(this, Constants.user_phone)

        userDetailsViewModel.getUserDetailsById(user_phone ?: "", user_id ?: "")
    }

    private fun goToSearchActivity() {
        val intent = Intent(this, SearchResultPersonActivity::class.java).apply {
            putExtra(SearchResultPersonActivity.SEARCH_MODEL, modelSearch)
        }
        startActivity(intent)
    }

    private fun loadBannerAdView() {

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewHome.loadAd(adRequest)

        binding.adViewHome.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to execute when an ad finishes loading
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                binding.adViewHome.visibility = View.GONE
            }

            override fun onAdOpened() {
                // Code to execute when an ad opens an overlay that covers the screen
            }

            override fun onAdClicked() {
                // Code to execute when the user clicks on an ad
            }

            override fun onAdClosed() {
                // Code to execute when the user is about to return to the app after tapping on an ad
            }
        }


    }

    private fun checkNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, show the card
                binding.cardViewPushNotification.visibility = View.VISIBLE
            } else {
                // Permission is granted, hide the card
                binding.cardViewPushNotification.visibility = View.GONE
            }
        } else {
            // On versions below Android 13, permission is granted by default
            binding.cardViewPushNotification.visibility = View.GONE
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, hide the card
                binding.cardViewPushNotification.visibility = View.GONE
            } else {
                // Permission denied, maybe show a message to the user
                showSnackBarMessage("Notification permission is required!")
                val intent = Intent().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                }
                startActivity(intent)
            }
        }
    }

    private fun checkAppUpdate() {

        val db = FirebaseFirestore.getInstance()
        val currentVersionCode = packageManager.getPackageInfo(packageName, 0).versionCode

        db.collection("app_config").document("android_version")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val latestVersionCode = document.getLong("version_code")?.toInt()
                    val appUpdateMessage = document.getString("app_update_message")

                    // Compare the version codes
                    if (latestVersionCode != null && currentVersionCode < latestVersionCode) {
                        binding.cardViewAppUpdate.visibility = View.VISIBLE
                        binding.textViewAppUpdateText.text = appUpdateMessage
                    }
                    else {
                        binding.cardViewAppUpdate.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "checkAppUpdate: exception ${exception.message}", )
                binding.cardViewAppUpdate.visibility = View.GONE
            }


    }

    private fun checkAdEnableStatus() {

        val db = FirebaseFirestore.getInstance()

        db.collection("ad_config").document("banner_ad")
            .get()
            .addOnSuccessListener { document ->

                if (document != null) {

                    val enable = document.getBoolean("enable")
                    val banner_ad_enable = enable ?: false
                    if (banner_ad_enable) {
                        loadBannerAdView()
                    }

                }
            }
    }

}