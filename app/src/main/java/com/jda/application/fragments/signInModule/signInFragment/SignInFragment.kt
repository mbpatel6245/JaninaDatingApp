package com.jda.application.fragments.signInModule.signInFragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.base.service.URLs
import com.jda.application.databinding.FragmentSignInBinding
import com.jda.application.fragments.questionModule.signupQuestion.QuestionsFragment
import com.jda.application.fragments.signInModule.preferencesFragment.PreferencesFragment
import com.jda.application.socialLogin.FacebookLoginActivity
import com.jda.application.socialLogin.GoogleLoginActivity
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.UserAlertUtility
import com.jda.application.utils.loginReplace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection


class SignInFragment : BaseFragment(), OnItemClickListener {
    private var mBinding: FragmentSignInBinding? = null
    private var mPresenter: SignInPresenter? = null
    lateinit var mLinkedInAuthURLFull: String
    lateinit var mLinkedInDialog: Dialog
    lateinit var mLinkedInCode: String

    companion object {
        val TAG: String = SignInFragment::class.java.simpleName
        fun newInstance() = SignInFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        Log.i(Constants.LOG_TAG, "onCreateView: ")
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.clickHandle = this
        mPresenter = SignInPresenterImpl(this)
        Log.i(Constants.LOG_TAG, "onViewCreated: ")
        val state = "linkedin" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        mLinkedInAuthURLFull =
                Constants.LinkedInConstants.AUTHURL + "?response_type=code&client_id=" + Constants.LinkedInConstants.CLIENT_ID + "&scope=" + Constants.LinkedInConstants.SCOPE + "&state=" + state + "&redirect_uri=" + Constants.LinkedInConstants.REDIRECT_URI
    }

    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.googleLL -> {
                Log.i(Constants.LOG_TAG, "onItemClick: Google login clicked")
                val intent = Intent(mActivity!!, GoogleLoginActivity::class.java)
                resultLauncherGoogleLogin.launch(intent)
            }
            R.id.facebookLL -> {
                Log.i(Constants.LOG_TAG, "onItemClick: Facebook login clicked")
                val intent = Intent(mActivity!!, FacebookLoginActivity::class.java)
                resultLauncherFacebookLogin.launch(intent)
            }
            R.id.linkedInLL -> {
                Log.i(Constants.LOG_TAG, "onItemClick: LinkedIn login clicked")
                setupLinkedInWebViewDialog(mLinkedInAuthURLFull)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupLinkedInWebViewDialog(url: String) {
        Log.i(Constants.LOG_TAG, "setupLinkedInWebViewDialog: ")
        mLinkedInDialog = Dialog(mActivity!!)
        val webView = WebView(mActivity!!)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = LinkedInWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        mLinkedInDialog.setContentView(webView)
        mLinkedInDialog.show()
    }

    @Suppress("OverridingDeprecatedMember")
    inner class LinkedInWebViewClient : WebViewClient() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(Constants.LinkedInConstants.REDIRECT_URI)) {
                handleUrl(request?.url.toString())

                Log.i(Constants.LOG_TAG, "shouldOverrideUrlLoading: ")
                // Close the dialog after getting the authorization code
                if (request?.url.toString().contains("?code=")) {
                    mLinkedInDialog.dismiss()
                }
                return true
            }
            return false
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(Constants.LinkedInConstants.REDIRECT_URI)) {
                handleUrl(url)
                Log.i(Constants.LOG_TAG, "shouldOverrideUrlLoading: ")
                // Close the dialog after getting the authorization code
                if (url.contains("?code=")) {
                    mLinkedInDialog.dismiss()
                }
                return true
            }
            return false
        }

        // Check webview url for access token code or error
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)

            Log.i(Constants.LOG_TAG, "handleUrl: LinkedInWebViewClient")

            if (url.contains("code")) {
                mLinkedInCode = uri.getQueryParameter("code") ?: ""
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, mActivity, false)
                linkedInRequestForAccessToken()
                Log.i(Constants.LOG_TAG, "handleUrl: linkedInRequestForAccessToken")
            } else if (url.contains("error")) {
                val error = uri.getQueryParameter("error") ?: ""
                Log.e("Error: ", error)
                Log.i(Constants.LOG_TAG, "handleUrl: $error")
            }
        }

        private fun linkedInRequestForAccessToken() {
            Log.i(Constants.LOG_TAG, "linkedInRequestForAccessToken: ")
            GlobalScope.launch(Dispatchers.Default) {
                val grantType = Constants.LinkedInConstants.sGRANT_TYPE
                val postParams =
                        "grant_type=" + grantType + "&code=" + mLinkedInCode + "&redirect_uri=" + Constants.LinkedInConstants.REDIRECT_URI + "&client_id=" + Constants.LinkedInConstants.CLIENT_ID + "&client_secret=" + Constants.LinkedInConstants.CLIENT_SECRET
                val url = URL(Constants.LinkedInConstants.TOKENURL)
                val httpsURLConnection =
                        withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                httpsURLConnection.requestMethod = Constants.LinkedInConstants.sPOST
                httpsURLConnection.setRequestProperty(
                        Constants.LinkedInConstants.sCONTENT_TYPE,
                        Constants.LinkedInConstants.sCONTENT_TYPE_JSON
                )
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = true
                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                withContext(Dispatchers.IO) {
                    outputStreamWriter.write(postParams)
                    outputStreamWriter.flush()
                }
                val response = httpsURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                val jsonObject = JSONTokener(response).nextValue() as JSONObject

                val accessToken = jsonObject.getString(Constants.LinkedInConstants.sACCESS_TOKEN) //The access token
                Log.d("accessToken is: ", accessToken)
                Log.i(Constants.LOG_TAG, "linkedInRequestForAccessToken: accessToken $accessToken")

                val expiresIn = jsonObject.getInt(Constants.LinkedInConstants.sEXPIRES_IN) //When the access token expires
                Log.d("expires in: ", expiresIn.toString())
                Log.i(Constants.LOG_TAG, "linkedInRequestForAccessToken: expiresIn $expiresIn")


                withContext(Dispatchers.Main) {
                    // Get user's id, first name, last name, profile pic url
                    Log.i(Constants.LOG_TAG, "linkedInRequestForAccessToken: fetchLinkedInUserProfile")
                    fetchLinkedInUserProfile(accessToken)
                }
            }
        }

        private fun fetchLinkedInUserProfile(token: String) {
            Log.i(Constants.LOG_TAG, "fetchLinkedInUserProfile: called...")
            GlobalScope.launch(Dispatchers.Default) {
                val tokenURLFull = URLs.APIs.sLinkedInProfileUrl + token
                val url = URL(tokenURLFull)
                val httpsURLConnection =
                        withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                httpsURLConnection.requestMethod = Constants.LinkedInConstants.sGET
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = false
                val response = httpsURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                val linkedInProfileModel =
                        Json { ignoreUnknownKeys = true }.decodeFromString(
                                LinkedInProfileModel.serializer(),
                                response
                        )
                withContext(Dispatchers.Main) {
                    Log.d("LinkedIn Access Token: ", token)
                    Log.d(Constants.LOG_TAG, token)
                    // LinkedIn Id
                    val linkedInId = linkedInProfileModel.id
                    Log.d("LinkedIn Id: ", linkedInId)
                    Log.d(Constants.LOG_TAG, linkedInId)
                    // LinkedIn First Name
                    val linkedInFirstName = linkedInProfileModel.firstName.localized.enUS
                    Log.d("LinkedIn First Name: ", linkedInFirstName)
                    Log.d(Constants.LOG_TAG, linkedInFirstName)
                    // LinkedIn Last Name
                    val linkedInLastName = linkedInProfileModel.lastName.localized.enUS
                    Log.d("LinkedIn Last Name: ", linkedInLastName)
                    Log.d(Constants.LOG_TAG, linkedInLastName)

                    // LinkedIn Profile Picture URL
                    /*
                         Change row of the 'elements' array to get diffrent size of the profile pic
                         elements[0] = 100x100
                         elements[1] = 200x200
                         elements[2] = 400x400
                         elements[3] = 800x800
                    */
                    val linkedInProfilePicUrl =
                            linkedInProfileModel.profilePicture?.displayImage?.elements?.get(2)?.identifiers?.get(0)?.identifier
                    Log.d("LinkedIn Profile URL: ", linkedInProfilePicUrl ?: "")
                    Log.d(Constants.LOG_TAG, linkedInProfilePicUrl ?: "")

                    // Get user's email address
                    fetchLinkedInEmailAddress(
                            token,
                            linkedInId,
                            linkedInFirstName,
                            linkedInLastName,
                            linkedInProfilePicUrl
                    )
                }
            }
        }

        private fun fetchLinkedInEmailAddress(
                pToken: String,
                pId: String?,
                pFirstName: String?,
                pLastName: String?,
                pProfilePicUrl: String?
        ) {

            Log.i(Constants.LOG_TAG, "fetchLinkedInEmailAddress: ")
            val tokenURLFull =
                    URLs.APIs.sLinkedInEmailUrl + pToken

            GlobalScope.launch(Dispatchers.Default) {
                val url = URL(tokenURLFull)
                val httpsURLConnection =
                        withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                httpsURLConnection.requestMethod = Constants.LinkedInConstants.sGET
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = false
                val response = httpsURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                val linkedInProfileModel =
                        Json {
                            ignoreUnknownKeys = true
                        }.decodeFromString(LinkedInEmailModel.serializer(), response)
                withContext(Dispatchers.Main) {
                    // LinkedIn Email
                    val linkedInEmail =
                            linkedInProfileModel.elements[0].elementHandle.emailAddress
                    Log.d("LinkedIn Email: ", linkedInEmail)
                    Log.i(Constants.LOG_TAG, "fetchLinkedInEmailAddress: ")
                    UserAlertUtility.hideProgressDialog()
                    hitSocialLoginApiFromLinkedIn(
                            pId!!,
                            pFirstName,
                            pLastName,
                            pProfilePicUrl,
                            linkedInEmail
                    )
                }
            }
        }
    }

    private fun hitSocialLoginApiFromLinkedIn(
            pId: String,
            pFirstName: String?,
            pLastName: String?,
            pProfilePicUrl: String?,
            pEmail: String?
    ) {
        val params = SocialLoginModel(
                "ssss",
                JDAApplication.mInstance.getDeviceToken(),
                pEmail,
                pFirstName,
                pLastName,
                pProfilePicUrl,
                Constants.LoginType.sLINKED_LOGIN,
                pId
        )
        Log.i(Constants.LOG_TAG, "hitSocialLoginApiFromLinkedIn: -> apiSocialLogin")
        mPresenter!!.apiSocialLogin(params)
    }

    var resultLauncherGoogleLogin =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                Log.e(Constants.LOG_TAG, "resultLauncherGoogleLogin: result from google login"+result.data)

                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes

                    Log.i(Constants.LOG_TAG, "resultLauncherGoogleLogin: result from google data is ${Gson().toJson(result.data)}")

                    val data = result.data
                    val token = data!!.getStringExtra(Constants.SocialLoginKeys.sUniqueID)
                    val email = data!!.getStringExtra(Constants.SocialLoginKeys.sEmailAddress)
                    val firstName = data!!.getStringExtra(Constants.SocialLoginKeys.sFirstName)
                    val lastName = data!!.getStringExtra(Constants.SocialLoginKeys.sLastName)
                    val image = data!!.getStringExtra(Constants.SocialLoginKeys.sImageUrl)
                    if (email != null) {
                        Log.d("Google Login", "$email\n $firstName\n $lastName ")
                        Log.d(Constants.LOG_TAG, "$email\n $firstName\n $lastName ")
                    }
                    val params = SocialLoginModel(
                            "ssss",
                            JDAApplication.mInstance.getDeviceToken(),
                            email,
                            firstName,
                            lastName,
                            image,
                            Constants.LoginType.sGOOGLE_LOGIN,
                            token
                    )
                    Log.i(Constants.LOG_TAG, "resultLauncherGoogleLogin: -> apiSocialLogin")
                    mPresenter!!.apiSocialLogin(params)
                }
            }

    var resultLauncherFacebookLogin =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.i(Constants.LOG_TAG, "resultLauncherFacebookLogin: result from google login")
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes

                    Log.i(Constants.LOG_TAG, "resultLauncherFacebookLogin: result from google data is ${Gson().toJson(result.data)}")

                    val data = result.data
                    val socialId = data!!.getStringExtra(Constants.SocialLoginKeys.sUniqueID)
                    val email = data!!.getStringExtra(Constants.SocialLoginKeys.sEmailAddress)
                    val firstName = data!!.getStringExtra(Constants.SocialLoginKeys.sFirstName)
                    val lastName = data!!.getStringExtra(Constants.SocialLoginKeys.sLastName)
                    val image = data!!.getStringExtra(Constants.SocialLoginKeys.sImageUrl)
                    if (email != null) {
                        Log.d("Google Login", "$email/n$firstName")
                    }
                    val params = SocialLoginModel(
                            "ssss",
                            JDAApplication.mInstance.getDeviceToken(),
                            email,
                            firstName,
                            lastName,
                            image,
                            Constants.LoginType.sFACEBOOK_LOGIN,
                            socialId
                    )
                    Log.i(Constants.LOG_TAG, "resultLauncherFacebookLogin: -> apiSocialLogin")
                    mPresenter!!.apiSocialLogin(params)
                }
            }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        Log.i(Constants.LOG_TAG, "SignInFragment, onSuccess: ")
        handleSuccessResponse(pResponse)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun handleSuccessResponse(pResponse: Any?) {
        val response = pResponse as SocialLoginSuccessModel
        Log.i("MySubscription", "Login Api Gives Active Status : " + pResponse.result?.isActive)
        Log.i(Constants.LOG_TAG, "Login Api Gives Active Status : " + pResponse.result?.isActive)
        Constants.sIsSubscribed = pResponse.result?.isActive ?: false
//        Constants.sIsSubscribed = false
        when (pResponse.result?.status) {
            Constants.ScreenStatus.sLOGGEDIN -> {
                Log.i(Constants.LOG_TAG, "handleSuccessResponse: sLOGGEDIN")
                JDAApplication.mInstance.setProfile(response)
                JDAApplication.mInstance.setLoginStatus(response.result?.status)
                mActivity?.loginReplace(PreferencesFragment.newInstance())
            }
            Constants.ScreenStatus.sQUESTIONS_ANSWERED ->{
                Log.i(Constants.LOG_TAG, "handleSuccessResponse: sPREFERENCES_SET")
                JDAApplication.mInstance.setProfile(response)
                JDAApplication.mInstance.setLoginStatus(response.result?.status)
                startActivity(Intent(mActivity, DashboardActivity::class.java))
                mActivity?.showToast(getString(R.string.login_success))
            }
            Constants.ScreenStatus.sPREFERENCES_SET -> {

                //--- move to setting questions
                Log.i(Constants.LOG_TAG, "handleSuccessResponse: sLOGGEDIN")
                JDAApplication.mInstance.setProfile(response)
                JDAApplication.mInstance.setLoginStatus(response.result?.status)
                mActivity?.loginReplace(QuestionsFragment.newInstance())

//                Log.i(Constants.LOG_TAG, "handleSuccessResponse: sPREFERENCES_SET")
//                JDAApplication.mInstance.setProfile(response)
//                JDAApplication.mInstance.setLoginStatus(response.result?.status)
//                startActivity(Intent(mActivity, DashboardActivity::class.java))
//                mActivity?.showToast(getString(R.string.login_success))

/*                UserAlertUtility.openCustomDialog(
                        activity, getString(R.string.set_profile),
                        getString(R.string.set_full_profile_msg), null, null,
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {
                                mActivity?.loginReplace(QuestionsFragment.newInstance())
                            }

                            override fun onNoClick() {
                                startActivity(Intent(mActivity, DashboardActivity::class.java))
                                mActivity?.showToast(getString(R.string.login_success))
                            }
                        }, true)
                if (Constants.sIsSubscribed) {
                    mActivity?.loginReplace(QuestionsFragment.newInstance())
                } else {
                    startActivity(Intent(mActivity, DashboardActivity::class.java))
                    mActivity?.showToast(getString(R.string.login_success))
                }*/
            }
            Constants.ScreenStatus.sPROFILE_COMPLETED, Constants.ScreenStatus.sQUESTIONS_ANSWERED -> {
                Log.i(Constants.LOG_TAG, "handleSuccessResponse: sQUESTIONS_ANSWERED")
                val data = response
                data.result?.arePreferencesSet = true
                data.result?.areQuestionsSet = true
                JDAApplication.mInstance.setProfile(data)
                JDAApplication.mInstance.setLoginStatus(data.result?.status)
                startActivity(Intent(mActivity, DashboardActivity::class.java))
                mActivity?.showToast(getString(R.string.login_success))
                mActivity?.finish()
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        Log.i(Constants.LOG_TAG, "showProgress: ")
        when (tClass) {
            SocialLoginSuccessModel::class.java -> {
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, mActivity, false)
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        Log.i(Constants.LOG_TAG, "hideProgress: ")
        when (tClass) {
            SocialLoginSuccessModel::class.java -> {
                UserAlertUtility.hideProgressDialog()
            }
        }
    }
}