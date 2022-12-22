package com.jda.application.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionsUtility {

    fun requestLocation(pActivity: Activity?): Boolean {
        if (pActivity != null) {
            if (ActivityCompat.checkSelfPermission(
                            pActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        pActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.ResponseCode.sFINE_LOC_REQ_CODE
                )
                return false
            }
            return true
        }
        return false
    }

    fun checkPermission(pActivity: Activity, pPermission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
                pActivity,
                pPermission
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun useRunTimePermissions(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    fun hasPermission(activity: Activity, permission: String?): Boolean {
        return if (useRunTimePermissions()) {
            activity.checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    fun requestPermissions(
            pActivity: Activity,
            pPermissionsList: Array<String>,
            requestCode: Int
    ) {
        if (useRunTimePermissions()) {
            ActivityCompat.requestPermissions(pActivity, pPermissionsList, requestCode)
        }

    }

    fun requestPermissions(
            pActivity: Activity,
            pPermissionsList: ArrayList<String>,
            requestCode: Int
    ) {
        if (useRunTimePermissions()) {
            ActivityCompat.requestPermissions(
                    pActivity, pPermissionsList.toTypedArray(), requestCode)
        }

    }


    fun shouldShowRational(activity: Activity, permission: String?): Boolean {
        return if (useRunTimePermissions()) {
            activity.shouldShowRequestPermissionRationale(permission!!)
        } else false
    }

    fun shouldAskForPermission(
            activity: Activity,
            permission: String?
    ): Boolean {
        return if (useRunTimePermissions()) {
            !hasPermission(activity, permission) && (!hasAskedForPermission(activity, permission) || shouldShowRational(activity, permission))
        } else false
    }

    public fun checkPermissions(pActivity: Activity, pPermission: Array<String>, MULTIPLE_PERMISSIONS: Int): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in pPermission) {
            result = ContextCompat.checkSelfPermission(pActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(pActivity, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    fun goToAppSettings(activity: Activity) {
        val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    fun hasAskedForPermission(
            activity: Activity?,
            permission: String?
    ): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(permission, false)
    }

}
