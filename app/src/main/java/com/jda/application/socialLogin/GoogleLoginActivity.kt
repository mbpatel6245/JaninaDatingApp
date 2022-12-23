package com.jda.application.socialLogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.jda.application.utils.Constants

class GoogleLoginActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Constants.LOG_TAG, "GoogleLoginActivity, onCreate: ")
        mGoogleSignInClient?.signOut()
        startLogin()
        signIn()
    }

    private fun startLogin() {
        Log.i(Constants.LOG_TAG, "GoogleLoginActivity, startLogin: ")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        Log.i(Constants.LOG_TAG, "GoogleLoginActivity, signIn: ")
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
        activityResult.launch(signInIntent)
    }

    val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            Log.e(Constants.LOG_TAG, "GoogleLoginActivity, activityResult: "+result.data)
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(Constants.LOG_TAG, "GoogleLoginActivity, activityResult: RESULT_OK")
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                //Log.i(Constants.LOG_TAG, "GoogleLoginActivity, activityResult: ${Gson().toJson(result.data)}")
                handleSignInResult(task)
            }
            else {
//                val resultIntent = Intent()
//                resultIntent.putExtra(Constants.SocialLoginKeys.sUniqueID, "856231")
//                resultIntent.putExtra(
//                    Constants.SocialLoginKeys.sEmailAddress,
//                    "hfhfsasdk@chicmic.co.in"
//                )
//                resultIntent.putExtra(Constants.SocialLoginKeys.sName, "asdtestz")
//                resultIntent.putExtra(Constants.SocialLoginKeys.sImageUrl, "dszds")
//                setResult(RESULT_OK, resultIntent)
                Log.e(Constants.LOG_TAG, "GoogleLoginActivity, activityResult: RESULT_NOT_OK, finish()")
                finish()
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.i(Constants.LOG_TAG, "GoogleLoginActivity, handleSignInResult: ")
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.w("Result", account?.email!!)
            Log.w(Constants.LOG_TAG, account?.email!!)
            val resultIntent = Intent()
            resultIntent.putExtra(Constants.SocialLoginKeys.sUniqueID, account.id)
            resultIntent.putExtra(Constants.SocialLoginKeys.sEmailAddress, account.email)
            resultIntent.putExtra(Constants.SocialLoginKeys.sFirstName, account.givenName?:Constants.sEmpty_String)
            resultIntent.putExtra(Constants.SocialLoginKeys.sLastName, account.familyName?:Constants.sEmpty_String)
            resultIntent.putExtra(Constants.SocialLoginKeys.sImageUrl, account.photoUrl)
            setResult(RESULT_OK, resultIntent)
            mGoogleSignInClient!!.signOut()
            finish()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("jij", "signInResult:failed code=" + e.statusCode)
            Log.i(Constants.LOG_TAG, "GoogleLoginActivity, ApiException: ${e.printStackTrace()}")
            e.printStackTrace()
            finish()
        }
    }
}