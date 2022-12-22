package com.jda.application.socialLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jda.application.base.activity.BaseActivity
import com.jda.application.base.service.URLs
import com.jda.application.fragments.signInModule.signInFragment.SignInPresenter
import com.jda.application.fragments.signInModule.signInFragment.SignInPresenterImpl
import com.jda.application.utils.Constants

class FacebookLoginActivity : BaseActivity() {
    private var mUserFirstName: String? = null
    private var mUserLastName: String? = null
    private var mUserEmail: String? = null
    private var mUserId: String? = null
    private var mCallbackManager: CallbackManager? = null
    private var mAccessToken: String? = null
    private var mFacebookRequestModel: FacebookRequestModel? = null
    private var mFacebookPictureModel: Picture? = null
    private var mPresenter: SignInPresenter? = null

    override fun onPermissionGranted(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onPermissionGranted: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onCreate: ")
        initialise()
        onFacebookLogin()
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf(
                        Constants.FacebookPermissionKeys.sPUBLIC_PROFILE,
                        Constants.FacebookPermissionKeys.sUSER_EMAIL
                )
        )
    }

    private fun initialise() {
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, initialise: ")
        mPresenter = SignInPresenterImpl(this)
        mFacebookPictureModel = Picture()
        mCallbackManager = CallbackManager.Factory.create()
    }

    //FaceBook Login
    private fun onFacebookLogin() {
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onFacebookLogin: ")
        LoginManager.getInstance().registerCallback(mCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onSuccess: ")
                        if (loginResult != null) {
                            mAccessToken = loginResult.accessToken.toString()
                            getFacebookUserInfo(loginResult)
                            Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onSuccess: $mAccessToken ")
                        }
                    }

                    override fun onCancel() {
                        finish()
                        // App code
                        Log.i("_response", "cancel")
                        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onCancel: ")
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        Log.i("_response", "error")
                        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onError: ${exception.printStackTrace()}")
                    }

                })
    }

    //Get User Information From Facebook
    private fun getFacebookUserInfo(pLoginResult: LoginResult?) {
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, getFacebookUserInfo: ")
        val request = GraphRequest.newMeRequest(pLoginResult!!.accessToken) { `object`, response ->
            try {
                //here is the data that we want
                if (`object`?.has(Constants.FacebookPermissionKeys.sUSER_FIRST_NAME)==true) {
                    mUserFirstName = `object`.getString(Constants.FacebookPermissionKeys.sUSER_FIRST_NAME)
                    Log.i(Constants.LOG_TAG, "FacebookLoginActivity, sUSER_FIRST_NAME: $mUserFirstName")
                }
                if (`object`?.has(Constants.FacebookPermissionKeys.sUSER_LAST_NAME)==true) {
                    mUserLastName = `object`.getString(Constants.FacebookPermissionKeys.sUSER_LAST_NAME)
                    Log.i(Constants.LOG_TAG, "FacebookLoginActivity, sUSER_LAST_NAME: $mUserLastName")
                }
                if (`object`?.has(Constants.FacebookPermissionKeys.sUSER_EMAIL)==true) {
                    mUserEmail = `object`.getString(Constants.FacebookPermissionKeys.sUSER_EMAIL)
                    Log.i(Constants.LOG_TAG, "FacebookLoginActivity, sUSER_LAST_NAME: $mUserEmail")
                }
                if (`object`?.has(Constants.FacebookPermissionKeys.sUSER_ID)==true) {
                    mUserId = `object`.getString(Constants.FacebookPermissionKeys.sUSER_ID)
                    Log.i(Constants.LOG_TAG, "FacebookLoginActivity, sUSER_LAST_NAME: $mUserId")
                }
                getFacebookProfilePic(pLoginResult)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(Constants.LOG_TAG, "FacebookLoginActivity, getFacebookUserInfo: Exception, ${e.printStackTrace()}")
            }
        }

        val parameters = Bundle()
        parameters.putString(
                Constants.FacebookPermissionKeys.sFIELDS,
                Constants.FacebookPermissionKeys.sNAME_EMAIL_ID
        )
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getFacebookProfilePic(pLoginResult: LoginResult?) {
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, getFacebookProfilePic: ")
        val data = HashMap<String, Any>()
        if (pLoginResult!!.accessToken != null) {
            data[URLs.ApisParamsName.sWidth] = 200
            data[URLs.ApisParamsName.sHeight] = 200
            data[URLs.ApisParamsName.sRedirect] = false
            mPresenter?.apiFacebookProfilePic(mUserId!!, data)
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        Log.i(Constants.LOG_TAG, "FacebookLoginActivity, onSuccess: ")
        when (pResponse) {
            is Picture -> {
                mFacebookPictureModel = pResponse
                val profile = Profile()
                profile.email = mUserEmail
                profile.userID = mUserId
                profile.firstName = mUserFirstName
                profile.lastName = mUserLastName
                profile.picture = mFacebookPictureModel
                Log.d("FaceBook", "onSuccess: ${ mFacebookPictureModel?.data?.url} ")
                Log.d(Constants.LOG_TAG, "FacebookLoginActivity, onSuccess: ${ mFacebookPictureModel?.data?.url} ")
                mFacebookRequestModel = FacebookRequestModel(profile)
                val resultIntent = Intent()
                resultIntent.putExtra(Constants.SocialLoginKeys.sUniqueID, mUserId)
                resultIntent.putExtra(Constants.SocialLoginKeys.sEmailAddress, mUserEmail)
                resultIntent.putExtra(Constants.SocialLoginKeys.sFirstName, mUserFirstName)
                resultIntent.putExtra(Constants.SocialLoginKeys.sLastName, mUserLastName)
                resultIntent.putExtra(Constants.SocialLoginKeys.sImageUrl, mFacebookPictureModel?.data?.url)
                setResult(RESULT_OK, resultIntent)
                Log.d(Constants.LOG_TAG, "FacebookLoginActivity, activity finished: ")
                this.finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        Log.d(Constants.LOG_TAG, "FacebookLoginActivity, onActivityResult: ")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(Constants.LOG_TAG, "FacebookLoginActivity, onBackPressed: ")
        Log.d(Constants.LOG_TAG, "FacebookLoginActivity, finished: ")
        this.finish()
    }
}