package com.jda.application.acivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import com.jda.application.R
import com.jda.application.base.activity.BaseActivity
import com.jda.application.fragments.chatModule.ChatFragment
import com.jda.application.fragments.edit_profile.EditProfileFragment
import com.jda.application.fragments.homeFragment.HomeFragment
import com.jda.application.fragments.profileModule.ProfileFragment
import com.jda.application.fragments.settingsModule.SettingsFragment
import com.jda.application.fragments.subscriptions.SubscriptionActivity
import com.jda.application.utils.Constants
import com.jda.application.utils.UserAlertUtility
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.write_review_design.*


class DashboardActivity : BaseActivity() {
    private var doubleBackToExitPressedOnce = false
    var mUserIdFromNotification: String? = null

    override fun onPermissionGranted(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()
        handleNotification(intent)
        Log.i(Constants.LOG_TAG, "onCreate: In DashboardActivity")
    }

    override fun onResume() {
        Log.e("Socket", "Connected-call")
        JDAApplication.mInstance.socketHelperObject?.socketConnect()
        loadBannerAd(ad_view)
        super.onResume()
    }

    override fun onPause() {
        Log.e("Socket", "Dis-Connected-call")
        JDAApplication.mInstance.socketHelperObject?.socketDisconnect()
        super.onPause()
    }

    private fun initialise() {
//        JDAApplication.mInstance.socketHelperObject?.socketConnect()
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.menu.getItem(0).setIcon(R.drawable.home_selected)
        setBottomNavigationView()
        Log.e("FirebaseToken", JDAApplication.mInstance.getDeviceToken())
    }

    private fun handleNotification(intent: Intent?) {
        if (intent != null && intent.hasExtra(Constants.Notification.type)) {
//            bottomNavigationView.menu.getItem(1).setIcon(R.drawable.chat_selected)
            bottomNavigationView.selectedItemId = bottomNavigationView.menu.getItem(1).itemId
            setUserId()
        }
    }

    // set user id if we receive from notification.
    private fun setUserId() {
        if (intent.hasExtra(Constants.Notification.sNewChatId)) {
            Log.d(
                    "BroadcastReceiver",
                    "handleNotification: ${JDAApplication.mInstance.getCurrentFragment()}  $mUserIdFromNotification"
            )
            mUserIdFromNotification = intent.getStringExtra(Constants.Notification.sNewChatId)
            Log.d(
                    "BroadcastReceiver",
                    "handleNotification: $mUserIdFromNotification"
            )
        }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeTv -> {
                    setDefaultNavIcon()
                    item.icon = ContextCompat.getDrawable(this, R.drawable.home_selected)
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContainerFL, HomeFragment.newInstance())
                            .disallowAddToBackStack().commit()
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.chat -> {
                    Log.i("Dashboard", "Chat tab clicked")
                    //Only Enable chat option if subscribed
                    Log.i("Dashboard", "Chat tab is Active :" + JDAApplication.mInstance.getProfile()?.result?.isActive)

                    if (Constants.sIsSubscribed) {
//                        loadBannerAd(ad_view)
                        setDefaultNavIcon()
                        item.icon = ContextCompat.getDrawable(this, R.drawable.chat_selected)
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.mainContainerFL, ChatFragment.newInstance())
                                .disallowAddToBackStack().commit()
                        return@setOnNavigationItemSelectedListener true
                    } else {
                        UserAlertUtility.openCustomDialog(
                                this, "Subscription",
                                getString(R.string.purchase_plan_to_enable_chat), getString(R.string.subscribe_txt), getString(R.string.cancel),
                                object : UserAlertUtility.CustomDialogClickListener {
                                    override fun onYesClick() {
                                        val intent = Intent(this@DashboardActivity, SubscriptionActivity::class.java)
                                        startActivity(intent)
                                        Constants.sIsFromChatDialog = false
                                    }

                                    override fun onNoClick() {
                                        Constants.sIsFromChatDialog = false
                                    }
                                }, true)
                    }
                }
                R.id.profile -> {
                    setDefaultNavIcon()
                    item.icon = ContextCompat.getDrawable(this, R.drawable.profile_selected)

                    supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContainerFL, ProfileFragment.newInstance())
                            .disallowAddToBackStack().commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    setDefaultNavIcon()
                    item.icon = ContextCompat.getDrawable(this, R.drawable.setting_selected)

                    supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContainerFL, SettingsFragment.newInstance())
                            .disallowAddToBackStack().commit()

                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottomNavigationView.selectedItemId = R.id.homeTv
    }

    private fun setDefaultNavIcon() {
        bottomNavigationView.menu.getItem(0).setIcon(R.drawable.home_unselected)
        bottomNavigationView.menu.getItem(1).setIcon(R.drawable.chat_unselected)
        bottomNavigationView.menu.getItem(2).setIcon(R.drawable.profile_unselected)
        bottomNavigationView.menu.getItem(3).setIcon(R.drawable.setting_unselected)
    }

    override fun onBackPressed() {
        closeKeyBoard()
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

//    override fun onDestroy() {
//        Log.e("Socket", "Dis-Connected-call")
//        JDAApplication.mInstance.socketHelperObject?.socketDisconnect()
//        super.onDestroy()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.mainContainerFL)
        if (fragment is EditProfileFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}