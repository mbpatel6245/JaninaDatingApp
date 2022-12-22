package com.jda.application.acivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.jda.application.R
import com.jda.application.base.activity.BaseActivity
import com.jda.application.fragments.questionModule.editQuestion.QuestionsSecondFragment
import com.jda.application.fragments.questionModule.editQuestion.QuestionsThirdFragment
import com.jda.application.fragments.questionModule.signupQuestion.QuestionsFragment
import com.jda.application.fragments.signInModule.preferencesFragment.PreferencesFragment
import com.jda.application.fragments.signInModule.signInFragment.SignInFragment
import com.jda.application.utils.*


class LoginActivity : BaseActivity() {
    private var doubleBackToExitPressedOnce = false
    var mPreferencesApiHit = false

    @ExperimentalStdlibApi
    var mQuestionSecondFragment: QuestionsSecondFragment? = null

    @ExperimentalStdlibApi
    var mQuestionThirdFragment: QuestionsThirdFragment? = null

    override fun onPermissionGranted(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        CommonUtility.handlePermissionResult(this, requestCode, permissions)
        if (requestCode == Constants.RequestCodesSelfie.sCamera) {
            if (PermissionsUtility.checkPermissions(this, permissions, requestCode)) {
//                    loginReplaceStack(BioCaptureFragment())
            }
        }
    }

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        JDAApplication.mInstance.setCurrentActivity(this)

        Log.i(Constants.LOG_TAG, "LoginActivity, onCreate: ")

        if (JDAApplication.mInstance.getProfile() != null) {
            when (JDAApplication.mInstance.getLoginStatus()) {
                Constants.ScreenStatus.sLOGGEDIN -> {
                    loginReplace(PreferencesFragment.newInstance())
                    Log.i(Constants.LOG_TAG, "LoginActivity, onCreate: load PreferencesFragment")
                }

                Constants.ScreenStatus.sPREFERENCES_SET -> {
                    if (Constants.sIsSubscribed && !JDAApplication.mInstance.getProfile()!!.result?.areQuestionsSet!!) {
//                        UserAlertUtility.openCustomDialog(
//                                this@LoginActivity, getString(R.string.set_profile),
//                                getString(R.string.set_full_profile_msg), null, null,
//                                object : UserAlertUtility.CustomDialogClickListener {
//                                    override fun onYesClick() {
//                                      loginReplace(QuestionsFragment.newInstance())
//                                    }
//
//                                    override fun onNoClick() {
//                                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
//                                        showToast(getString(R.string.login_success))
//                                    }
//                                }, true)
                        loginReplace(QuestionsFragment.newInstance())
                        Log.i(Constants.LOG_TAG, "LoginActivity, onCreate: load QuestionsFragment")
                    } else {
                        Log.i(Constants.LOG_TAG, "LoginActivity, onCreate: move to dashboard")
                        startActivity(Intent(this, DashboardActivity::class.java))
                        showToast(getString(R.string.login_success))
                        finish()
                    }
                }

                Constants.ScreenStatus.sPROFILE_COMPLETED, Constants.ScreenStatus.sQUESTIONS_ANSWERED -> {
                    loginReplace(SignInFragment.newInstance())
                    Log.i(Constants.LOG_TAG, "LoginActivity, onCreate: load SignInFragment")
                }
            }
        } else {
            loginReplace(SignInFragment.newInstance())
            Log.i(Constants.LOG_TAG, "LoginActivity, onCreate: load SignInFragment")
        }
    }

    override fun onBackPressed() {
        Log.i(Constants.LOG_TAG, "LoginActivity, onBackPressed: ")
        if (JDAApplication.mInstance.getRequestObject() != null) {
            JDAApplication.mInstance.getRequestObject()?.cancel()
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            exitTheApp()
        }
    }

    private fun exitTheApp() {
        Log.i(Constants.LOG_TAG, "LoginActivity, exitTheApp: ")
        if (doubleBackToExitPressedOnce) {
            finish()
        } else {
            showToast(getString(R.string.press_back_again_to_exit))
            doubleBackToExitPressedOnce = true
            Handler().postDelayed(Runnable {
                doubleBackToExitPressedOnce = false
            }, 3000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(Constants.LOG_TAG, "LoginActivity, onActivityResult: ")
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    @ExperimentalStdlibApi
    fun saveQuestionSecondFragment(fragment: QuestionsSecondFragment) {
        Log.i(Constants.LOG_TAG, "LoginActivity, saveQuestionSecondFragment: ")
        mQuestionSecondFragment = fragment
    }

    @ExperimentalStdlibApi
    fun saveQuestionThirdFragment(fragment: QuestionsThirdFragment) {
        Log.i(Constants.LOG_TAG, "LoginActivity, saveQuestionThirdFragment: ")
        mQuestionThirdFragment = fragment
    }

    override fun onResume() {
        super.onResume()
    }
}