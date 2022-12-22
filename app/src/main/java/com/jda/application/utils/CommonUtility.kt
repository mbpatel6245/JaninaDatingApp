package com.jda.application.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jda.application.R
import com.jda.application.base.service.URLs
import id.zelory.compressor.Compressor
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.request.RequestOptions.bitmapTransform


object CommonUtility {
    var mPermissionMsg: String? = null
    private val mPermissions = ArrayList<String>()
    private var mChoosePicFromDialog: Int = -1
    private val mPermissionsRejected = ArrayList<String>()
    private var mAlertDialog: AlertDialog? = null
//
//    fun hideKeyboard(pContext: Context?, view: View?) {
//        if (view != null) {
//            val imm = pContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//        }
//    }

    fun closeKeyBoard(pContext: Activity?): Boolean {
        val imm = pContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.hideSoftInputFromWindow(
                pContext.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun choosePicture(pContext: Activity) {
        val pictureDialog = AlertDialog.Builder(pContext)
        pictureDialog.setTitle(pContext.resources?.getString(com.jda.application.R.string.choose_picture))
        val pictureDialogItems = arrayOf(
                pContext.resources?.getString(com.jda.application.R.string.select_from_gallery),
                pContext.resources?.getString(com.jda.application.R.string.capture_from_camera)
        )
        pictureDialog.setItems(
                pictureDialogItems
        ) { _, which ->
            mChoosePicFromDialog = which
            choosePics(pContext, mChoosePicFromDialog)
        }
        pictureDialog.show()
    }

    private fun openGallery(pContext: Activity?) {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pContext?.startActivityForResult(galleryIntent, Constants.ChoosePhotoAction.GALLERY.values)
    }


    fun checkCameraAndGalleryPermission(pContext: Activity, requestCode: Int): Boolean {
        if (!PermissionsUtility.hasPermission(
                        pContext,
                        Manifest.permission.CAMERA
                ) || !PermissionsUtility.hasPermission(
                        pContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) || !PermissionsUtility.hasPermission(
                        pContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        ) {
            mPermissionMsg =
                    pContext.resources.getString(com.jda.application.R.string.permission_msg_camera_and_gallery)
            mPermissions.add(Manifest.permission.CAMERA)
            mPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            mPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            PermissionsUtility.requestPermissions(
                    pContext,
                    mPermissions.toTypedArray(), requestCode
            )
            return false
        }
        return true
    }

    private fun openCamera(pContext: Activity?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pContext?.startActivityForResult(intent, Constants.ChoosePhotoAction.CAMERA.values)
    }


    fun handlePermissionResult(pContext: Activity, pRequestCode: Int, permissions: Array<String>) {
        when (pRequestCode) {
            Constants.RequestCodes.ALL_PERMISSIONS_RESULT -> {
                mPermissionsRejected.clear()
                for (perm in mPermissions) {
                    if (!PermissionsUtility.hasPermission(pContext, perm)) {
                        mPermissionsRejected.add(perm)
                    }
                }
                if (mPermissionsRejected.size > 0) {

                    var needToOpenSetting = false
                    for (i in 0 until permissions.size) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                        pContext,
                                        permissions[i]
                                )
                        ) {
                            needToOpenSetting = true
                            break
                        }
                    }


                    if (needToOpenSetting) {
                        showPermissionDialog(pContext, true)
                    } else {
                        showPermissionDialog(pContext, false)
                    }
                } else {
                    choosePicture(pContext)
                }
            }
        }
    }

    private fun showPermissionDialog(pContext: Activity, pGoToSettings: Boolean) {
        mAlertDialog = AlertDialog.Builder(pContext)
                .setMessage(mPermissionMsg)
                .setNegativeButton(com.jda.application.R.string.cancel) { dialog, i ->
                    dialog.dismiss()
                }
                .setPositiveButton(com.jda.application.R.string.ok_message) { dialog, i ->
                    dialog.dismiss()
                    if (pGoToSettings) {
                        PermissionsUtility.goToAppSettings(pContext)
                    } else {
                        PermissionsUtility.requestPermissions(
                                pContext,
                                mPermissionsRejected.toTypedArray(),
                                Constants.RequestCodes.ALL_PERMISSIONS_RESULT
                        )
                    }
                }
                .create()
        mAlertDialog!!.setOnShowListener {
            mAlertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                            ContextCompat.getColor(
                                    pContext, com.jda.application.R.color.black
                            )
                    )
            mAlertDialog!!.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(
                            ContextCompat.getColor(
                                    pContext, com.jda.application.R.color.black
                            )
                    )
        }
        mAlertDialog!!.show()
    }

    fun handleOnActivityResultCode(pContext: Activity, pRequestCode: Int, pData: Intent?): Bitmap? {
        var bitmap: Bitmap? = null
        when (pRequestCode) {
            Constants.ChoosePhotoAction.GALLERY.values -> {
                if (pData != null) {
                    val contentURI = pData.data
                    bitmap = MediaStore.Images.Media.getBitmap(pContext.contentResolver, contentURI)
                }
            }
            Constants.ChoosePhotoAction.CAMERA.values -> {
                if (pData?.extras != null) {
                    bitmap = pData.extras!!.get("data") as Bitmap
                }
            }
        }
        return bitmap
    }

    fun getImagePath(activity: Activity, bitmapImage: Bitmap): String {

        val contextWrapper = ContextWrapper(activity)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = contextWrapper.getDir(Constants.sDirectroy, Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(
                directory,
                DateFormatUtility.getTimeStampInMilliseconds()
                        .toString().plus("_").plus(Constants.sProfilePic)
        )
        if (mypath.exists()) {
            mypath.delete()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return compressImage(activity, mypath.absolutePath) ?: mypath.absolutePath
    }

    fun compressImage(activity: Activity, path: String?): String? {
        if (path != null) {
            var file = File(path)
            try {
                file = Compressor(activity).compressToFile(file)
            } catch (e: Exception) {
                return null
            }
            return file.absolutePath
        }
        return null
    }


    private fun choosePics(pContext: Activity, pWhich: Int) {
        when (pWhich) {
            Constants.PicturePermission.GALLERY.ordinal -> {
                openGallery(pContext)
            }
            Constants.PicturePermission.CAMERA.ordinal -> {
                openCamera(pContext)
            }
        }
    }

    fun setGlideImage(context: Context, url: String?, view: ImageView) {
        var newUrl: String? = null

        if (!url!!.contains(Constants.sUploads) && url!!.contains(Constants.sPathUrl)) {
            newUrl = (if (URLs.Retrofit.sUSE_LIVE_SERVER) URLs.Retrofit.sAPI_BASE_LIVE_URL_IMAGE
            else URLs.Retrofit.sAPI_LOCAL_BASE_URL) + url
//            newUrl = URLs.Retrofit.sAPI_BASE_LIVE_URL_IMAGE + url
        } else {
            newUrl = url
        }
        Log.d("ImageURL", "setGlideImage: $newUrl")
        Glide.with(context)
                .load(newUrl)
                .placeholder(com.jda.application.R.drawable.loading)
                .apply(RequestOptions().fitCenter())
                .error(com.jda.application.R.drawable.default_image)
                .into(view)
    }

    fun setGlideBlurImage(context: Context, url: String?, view: ImageView) {
        var newUrl: String? = null

        newUrl = if (!url!!.contains(Constants.sUploads) && url!!.contains(Constants.sPathUrl)) {
            (if (URLs.Retrofit.sUSE_LIVE_SERVER) URLs.Retrofit.sAPI_BASE_LIVE_URL_IMAGE
            else URLs.Retrofit.sAPI_LOCAL_BASE_URL) + url
        } else {
            url
        }
        Log.d("ImageURL", "setGlideImage: $newUrl")
        Glide.with(context)
                .load(newUrl)
                .placeholder(com.jda.application.R.drawable.loading)
                .apply(RequestOptions().fitCenter())
                .transform(BlurTransformation(5,2))
                .error(com.jda.application.R.drawable.default_image)
                .into(view)
    }

    fun openFullImage(context: Context, url: String?,isShowDelete: Boolean, listener: OnDialogClickListener?) {


        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.jda.application.R.layout.open_image_view)
        val imageView = dialog.findViewById<ImageView>(R.id.imageView) as ImageView
        val backBtn = dialog.findViewById<ImageView>(R.id.backBt) as ImageView
        val deleteBtn = dialog.findViewById<ImageView>(R.id.deleteBt) as ImageView
        deleteBtn.visibility = if(isShowDelete) View.VISIBLE  else View.GONE
        dialog.show()
        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        deleteBtn.setOnClickListener {
            //delete image from server
            listener?.onDeleteClick(dialog)
//            dialog.dismiss()
        }

        var newUrl: String? = null

        if (!url!!.contains(Constants.sUploads) && url!!.contains(Constants.sPathUrl)) {
//            newUrl = URLs.Retrofit.sAPI_BASE_LIVE_URL_IMAGE + url
            newUrl = (if (URLs.Retrofit.sUSE_LIVE_SERVER) URLs.Retrofit.sAPI_BASE_LIVE_URL_IMAGE
            else URLs.Retrofit.sAPI_LOCAL_BASE_URL) + url
        } else {
            newUrl = url
        }
        Log.d("ImageURL", "setGlideImage: $newUrl")
        Glide.with(context)
                .load(newUrl)
                .placeholder(com.jda.application.R.drawable.loading)
                .apply(RequestOptions().fitCenter())
                .error(com.jda.application.R.drawable.default_image)
                .into(imageView)
    }

    fun setGifGlide(context: Context, url: Uri?, view: ImageView) {
        Glide.with(context)
                .asGif()
                .load(url)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .error(com.jda.application.R.drawable.default_image)
                .into(view)
    }

    fun setGlideServerImage(context: Context, url: String?, view: ImageView) {
        val interval: Long = 3 * 1000
        val options = RequestOptions().frame(interval).fitCenter()
        Glide.with(context).asBitmap()
                .load(url)
                .apply(options)
                .error(com.jda.application.R.drawable.default_image)
                .transform(BlurTransformation(20, 2))
                .into(view)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateFormat(format: String, source: String?): String {
        source?.let {
            val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val sdfO: DateFormat = SimpleDateFormat(format)
            val date: Date? = sdf.parse(it)
            return sdfO.format(date!!)
        } ?: return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun covertTimeToText(context: Context, dataDate: String?): String? {
        var convTime: String? = null
        val suffix = "ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val pasTime = dateFormat.parse(dataDate!!)
            val nowTime = Calendar.getInstance().time
            val dateDiff = nowTime.time - pasTime!!.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = "$second sec $suffix"
            } else if (minute < 60) {
                convTime = "$minute min $suffix"
            } else if (hour < 24) {
                convTime = "$hour hours $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " years " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " months " + suffix
                } else {
                    (day / 7).toString() + " week " + suffix
                }
            } else if (day < 7) {
                if (day == 1L) {
                    convTime = context.getString(com.jda.application.R.string.today)
                } else if (day == 2L) {
                    convTime = context.getString(com.jda.application.R.string.yesterday)
                } else {
                    convTime = "$day days $suffix"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convTime
    }

    @SuppressLint("SimpleDateFormat")
    fun covertTimeToTextNew(context: Context, dataDate: String?): String? {
        var convTime: String? = null
        val suffix = "ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val pasTime = dateFormat.parse(dataDate!!)
            val nowTime = Calendar.getInstance().time
            val dateDiff = nowTime.time - pasTime!!.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

            if (second < 60) {
//                convTime = if(second==0L) "Just Now" else "$second sec $suffix"
                convTime = "Just Now"
            } else if (minute < 60) {
                convTime = "$minute mins $suffix"
            } else if (hour < 24) {
                convTime = "$hour hours $suffix"
            } else if (day >= 7) {
                convTime = when {
                    day > 360 -> {
                        (day / 360).toString() + " years " + suffix
                    }
                    day > 30 -> {
                        (day / 30).toString() + " months " + suffix
                    }
                    else -> {
                        (day / 7).toString() + " week " + suffix
                    }
                }
            } else if (day < 7) {
                convTime = if (day == 1L) {
                    "$day day $suffix"
//                    context.getString(com.jda.application.R.string.today)
                } else
                    if (day == 2L) {
                    context.getString(com.jda.application.R.string.yesterday)
                } else {
                    "$day days $suffix"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convTime
    }

    @SuppressLint("SimpleDateFormat")
    fun convertStringToLong(source: String): Long {
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val date: Date? = sdf.parse(source)
        return date!!.time
    }

    @SuppressLint("SimpleDateFormat")
    fun convertStringToDate(source: String): Date {
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val date: Date = sdf.parse(source)!!
        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun isBeforeOneHour(source: String): Boolean {
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val date: Date = sdf.parse(source)!!

        val currentCal = Calendar.getInstance()
        currentCal.add(Calendar.HOUR, -1)
        return date.before(currentCal.time)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun capitaliseOnlyFirstLetter(data: String?): String? {
        var capitalString = ""
        if (data?.length != 0) {
            capitalString = data?.substring(0, 1)?.uppercase(Locale.ROOT) + data?.substring(1)
        }
        return capitalString
    }
}