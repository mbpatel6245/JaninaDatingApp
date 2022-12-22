package com.jda.application.fragments.edit_profile

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.activity.BaseActivity
import com.jda.application.fragments.editQuestionModule.EditQuestionsFragment
import com.jda.application.fragments.profileModule.ProfileFetchResponse
import com.jda.application.utils.CommonUtility.closeKeyBoard
import com.jda.application.utils.Constants
import com.jda.application.utils.mainReplaceStackBundle
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity() {

    private var myPResponse: ProfileFetchResponse? = null
    private var mIsFromSubscription = false
    override fun onPermissionGranted(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
    }

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        getIntentData()
        loadBannerAd(ad_view)
        if (mIsFromSubscription)
            openQuestionsFragment()
        else
            openEditProfileFragment()
    }

    private fun getIntentData() {
        intent?.extras?.let {
            val profile = it.getString(Constants.BundleParams.PROFILE_DATA)
            myPResponse = Gson().fromJson(profile, ProfileFetchResponse::class.java)
            mIsFromSubscription = it.getBoolean(Constants.BundleParams.sFromSubscription)
        }
    }

    private fun openEditProfileFragment() {
        val bundle = Bundle()
        bundle.putParcelable(Constants.BundleParams.DATA, myPResponse)
        mainReplaceStackBundle(EditProfileFragment.newInstance(), bundle)
    }

    @ExperimentalStdlibApi
    private fun openQuestionsFragment() {
        val bundle = Bundle()
        bundle.putParcelable(Constants.BundleParams.DATA, myPResponse)
        mainReplaceStackBundle(EditQuestionsFragment.newInstance(), bundle)
    }

    override fun onBackPressed() {
        closeKeyBoard(this)
        if (JDAApplication.mInstance.getRequestObject() != null) {
            JDAApplication.mInstance.getRequestObject()?.cancel()
        }
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.mainContainerFL)
        if (fragment is EditProfileFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}