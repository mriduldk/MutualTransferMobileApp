package com.codingstudio.mutualtransfer.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.ui.auth.compoment.UserAuthenticationActivity
import com.codingstudio.mutualtransfer.ui.search.UserHomeActivity
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private val TAG = "SplashScreenActivity"

    private lateinit var appUpdateManager : AppUpdateManager
    private val UPDATE_REQUEST_CODE = 3120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        appUpdateCheck()

    }

    private fun appUpdateCheck() {

        val update_check_time = SharedPref().getLongPref(this, Constants.UPDATE_CHECK_TIME)
        val update_available = SharedPref().getBooleanPref(this, Constants.UPDATE_AVAILABLE)

        val currentTimeLong = System.currentTimeMillis()

        if (currentTimeLong - update_check_time >= (1000 * 60 * 60) || update_available ) {

            SharedPref().setLong(this, Constants.UPDATE_CHECK_TIME, currentTimeLong)

            appUpdateManager = AppUpdateManagerFactory.create(this@SplashScreenActivity)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            CoroutineScope(Dispatchers.Main).launch {

                delay(1000)
                appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

                    if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                        startTheUpdate(appUpdateInfo)
                        SharedPref().setBoolean(this@SplashScreenActivity, Constants.UPDATE_AVAILABLE, true)

                    } else {
                        Log.e(TAG, "onCreate: Update not Available" )
                        SharedPref().setBoolean(this@SplashScreenActivity, Constants.UPDATE_AVAILABLE, false)
                        loginRedirect()
                    }

                }.addOnFailureListener { exception ->

                    Log.e(TAG, "onCreate: Update Check Availability Failed:  $exception" )
                    loginRedirect()
                }
            }
        }
        else{
            loginRedirect()
        }

    }

    private fun startTheUpdate(appUpdateInfo : AppUpdateInfo) {

        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_REQUEST_CODE){

            if (resultCode != RESULT_OK){

                Log.e(TAG, "Update flow failed! Result code: $resultCode" )

                val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                CoroutineScope(Dispatchers.Main).launch {

                    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

                        if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                            startTheUpdate(appUpdateInfo)
                            SharedPref().setBoolean(this@SplashScreenActivity, Constants.UPDATE_AVAILABLE, true)

                        } else {
                            Log.e(TAG, "onCreate: Update not Available" )
                            SharedPref().setBoolean(this@SplashScreenActivity, Constants.UPDATE_AVAILABLE, false)
                            loginRedirect()
                        }

                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "onCreate: Update Check Availability Failed:  $exception" )
                        loginRedirect()
                    }
                }

            }else {
                loginRedirect()
            }
        }

    }


    private fun loginRedirect() {

        val userId = SharedPref().getUserIDPref(this)
        val isLogIn = SharedPref().getBooleanPref(this, Constants.IsLogIn)

        if (isLogIn) {

            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()

        }
        else {

            val intent = Intent(this, UserAuthenticationActivity::class.java)
            intent.putExtra(UserAuthenticationActivity.NAVIGATION_TYPE, UserAuthenticationActivity.NAVIGATION_LOGIN)
            startActivity(intent)
            finish()
        }


    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

            if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_REQUEST_CODE
                )
            }
        }
    }


    companion object {

        const val NAVIGATION_TYPE = "NAVIGATION_TYPE"
        const val NAVIGATION_LOGIN = "NAVIGATION_LOGIN"
        const val NAVIGATION_SET_PROFILE = "NAVIGATION_SET_PROFILE"

    }



}