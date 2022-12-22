package com.jda.application.acivities

//import com.tda.application.Fragment.signup_module.signup_fragment.ProfileModel
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.facebook.FacebookSdk
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.fragments.signInModule.signInFragment.SocialLoginSuccessModel
import com.jda.application.utils.Constants
import com.jda.application.utils.SharedPreferencesUtility
import com.jda.application.utils.socket_utils.SocketHelper
import com.jda.application.utils.subscription.InAppPurchase
import retrofit2.Call
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


class JDAApplication : Application(), Application.ActivityLifecycleCallbacks {
    private var mCurrentActivity: AppCompatActivity? = null
    private var mFragment: Fragment? = null
    private var mFragmentManager: FragmentManager? = null
    private var mFragmentTransaction: FragmentTransaction? = null
    private var videoPath: Uri? = null
    private var call: Call<Any?>? = null
    private var userID: String? = null
    var socketHelperObject: SocketHelper? = null
    private var sInAppPurchase: InAppPurchase? = null
    var firebaseAnalytics: FirebaseAnalytics? = null

    companion object {
        lateinit var mInstance: JDAApplication private set
    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(getApplicationContext());
        mInstance = this
        socketHelperObject = SocketHelper()
        firebaseAnalytics = Firebase.analytics
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_place_key));
        }

//        saveApplicationLogs()

//        registerActivityLifecycleCallbacks(this);
    }

    fun getInAppPurchase(): InAppPurchase? {
        if (sInAppPurchase == null) {
            sInAppPurchase = InAppPurchase()
        }
        return sInAppPurchase
    }

    /**
     * Set latest fragment instance
     * */
    fun setCurrentFragment(pFragment: Fragment?) {
        mFragment = pFragment
    }

    /**
     * get fragment instance
     * */
    fun getCurrentFragment(): Fragment? {
        return mFragment
    }

    /**
     * Set latest activity instance
     * */
    fun setCurrentActivity(activity: AppCompatActivity?) {
        mCurrentActivity = activity
    }

    fun setUri(uri: Uri) {
        videoPath = uri
    }

    fun getUri(): Uri {
        return videoPath!!
    }

    fun setRequestObject(call: Call<Any?>?) {
        this.call = call
    }

    fun getRequestObject(): Call<Any?>? {
        return call
    }


    /**
     * get activity instance
     * */
    fun getCurrentActivity(): AppCompatActivity? {
        return mCurrentActivity
    }

    @SuppressLint("CommitTransaction")
    fun changeScreen(fragment: Fragment, action: Constants.FragmentAction, containerId: Int, isAddedTobackStack: Boolean = false) {
        this.mFragmentManager = this.getCurrentActivity()?.supportFragmentManager
        val tag = fragment.javaClass.name
        this.mFragmentTransaction = this.mFragmentManager?.beginTransaction()
        //        this.mFragmentTransaction?.setCustomAnimations(
        //            R.anim.slide_in_left,
        //            R.anim.slide_out_right
        //        )

        when (action) {
            Constants.FragmentAction.ADD -> {
                this.mFragmentTransaction?.add(containerId, fragment, tag)?.addToBackStack(tag)
            }
            Constants.FragmentAction.REPLACE -> {
                this.mFragmentTransaction?.replace(containerId, fragment, tag)
                when (isAddedTobackStack) {
                    true -> mFragmentTransaction?.addToBackStack(tag)
                    else -> {}
                }
            }
            Constants.FragmentAction.REMOVE -> {
                this.mFragmentTransaction?.remove(fragment)
            }
        }
        if (this.getCurrentActivity() != null) {
            this.mFragmentTransaction?.commitAllowingStateLoss()
        }
    }

    /*
    * Pop single fragment.
    * */
    fun pop(myFrag: Fragment) {
        this.mFragmentManager = getCurrentActivity()?.supportFragmentManager
        val trans = this.mFragmentManager?.beginTransaction()
        trans?.remove(myFrag)
        trans?.commitAllowingStateLoss()
        this.mFragmentManager?.popBackStack()
    }

    /**
     * get back stack count of fragments i.e how many fragments in back stack
     * */
    fun getBackStackCount(): Int {
        this.mFragmentManager = getCurrentActivity()?.supportFragmentManager
        return mFragmentManager?.backStackEntryCount ?: 0
    }

    fun getFragment(pPosition: Int): String? {
        this.mFragmentManager = getCurrentActivity()?.supportFragmentManager
        return mFragmentManager?.getBackStackEntryAt(pPosition)?.name
    }

    fun getFragmentByTag(pTag: String): Fragment? {
        this.mFragmentManager = getCurrentActivity()?.supportFragmentManager
        return mFragmentManager?.findFragmentByTag(pTag)
    }

    /**
     * Clear all fragment back stack from particular activity
     * */
    fun clearBackStack() {
        return this.getCurrentActivity()?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)!!
    }

    fun setProfile(data: SocialLoginSuccessModel?) {
        SharedPreferencesUtility.setData(this, Constants.SharePreferencesEnum.StringType.name, Constants.sProfileData, Gson().toJson(data, SocialLoginSuccessModel::class.java))
    }

    fun getProfile(): SocialLoginSuccessModel? {
        val data: String = SharedPreferencesUtility.getData(this, Constants.SharePreferencesEnum.StringType.name, Constants.sProfileData) as String
        return Gson().fromJson(data, SocialLoginSuccessModel::class.java)
    }

    fun setLoginStatus(status: Int?) {
        SharedPreferencesUtility.setData(this, Constants.SharePreferencesEnum.IntType.name, Constants.sLoginStatus, status
                ?: 0)
    }

    fun getLoginStatus(): Int {
        return SharedPreferencesUtility.getData(
                this,
                Constants.SharePreferencesEnum.IntType.name,
                Constants.sLoginStatus
        ) as Int
    }

    fun gotoLoginActivity(context: Activity) {
        setProfile(null)
        setLoginStatus(Constants.ScreenStatus.sLOGGEDIN)
        val intent = Intent(context, SplashActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        context.finish()
    }

    fun saveDeviceToken(token: String?) {
        SharedPreferencesUtility.setData(this, Constants.SharePreferencesEnum.StringType.name,
                Constants.sDeviceToken, token ?: Constants.sEmpty_String)
    }

    fun getDeviceToken(): String {
        return SharedPreferencesUtility.getData(this, Constants.SharePreferencesEnum.StringType.name,
                Constants.sDeviceToken) as String
    }

    fun setUserID(userID: String?) {
        this.userID = userID
    }

    fun getUserID(): String? {
        return userID
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    private fun getLogFileUri(): String {
        return SharedPreferencesUtility.getData(this, Constants.SharePreferencesEnum.StringType.name,
                Constants.sFileUriKey) as String
    }

    private fun saveLogFileUri(fileUri: String?) {
        SharedPreferencesUtility.setData(this, Constants.SharePreferencesEnum.StringType.name,
                Constants.sFileUriKey, fileUri ?: Constants.sEmpty_String)
    }

    private fun saveLogFilePath(filePath: String?) {
        SharedPreferencesUtility.setData(this, Constants.SharePreferencesEnum.StringType.name,
                Constants.sFilePathKey, filePath ?: Constants.sEmpty_String)
    }

    private fun getLogFilePath(): String? {
        return SharedPreferencesUtility.getData(this, Constants.SharePreferencesEnum.StringType.name,
                Constants.sFilePathKey) as String?
    }

    //--- create logs
    private fun saveApplicationLogs() {
        if (isExternalStorageWritable()) {

            val fileName = "logcat_some_app.txt"
            val newFileName = "logcat_some_app" + System.currentTimeMillis() + ".txt"
            val staticFileName = "/storage/emulated/0/Download/logcat_some_app.txt"
            val resolver = this.contentResolver
            var fileActualPath = "";

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

//            val downloadFolder = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)
//
//            val uri: Uri? = if (checkIfFileExists(fileName)) {
//                fileActualPath = getRealPathFromURI(this, Uri.parse(getLogFileUri()))!!
//                Uri.parse(getLogFileUri())
////                FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", File(getLogFileUri()))
//            } else {
//                resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//
//            }

            /*if (getLogFilePath().isNullOrEmpty()) {
                val file = File(staticFileName)
                if (file.exists()) {
                    fileActualPath = staticFileName
//                    try {
//                        resolver.openOutputStream(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file), "wa").use {
//                            it?.write(" ".toByteArray())
//                            it?.close()
//                        }
//                    } catch (e: FileNotFoundException) {
//                        e.printStackTrace()
//                    }
                }
            }*/
//            if (fileActualPath.isEmpty()) {
            if (checkIfFileExists(fileName)) {
                fileActualPath = getLogFilePath()!!
            } else {
                var uriNew = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                if (uriNew != null) {
                    try {
                        resolver.openOutputStream(uriNew!!, "wa").use {
                            it?.write(" ".toByteArray())
                            it?.close()
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    saveLogFilePath(getRealPathFromURI(this, uriNew))
                    fileActualPath = getRealPathFromURI(this, uriNew)!!
                }
            }
//            }
//            }
//            else{
//                var uriNew = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//                try {
//                    resolver.openOutputStream(uriNew!!, "wa").use {
//                        it?.write(" ".toByteArray())
//                        it?.close()
//                    }
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }
//                saveLogFilePath(getRealPathFromURI(this, uriNew))
//                getRealPathFromURI(this, uriNew)!!
//            }

//            if (uri != null) {
//                saveLogFileUri(uri.toString())
//                try {
//                    resolver.openOutputStream(uri, "wa").use {
//                        it?.write("Hello brother".toByteArray())
//                        it?.close()
//                    }
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }
//                resolver.openOutputStream(uri, "wa").use {
//                    it?.write("Hello brother".toByteArray())
//                    it?.close()
//                }

//                try {
//                    Runtime.getRuntime().exec("logcat -c")
//                    val process = Runtime.getRuntime().exec("logcat -s ${Constants.LOG_TAG}")
//                    val reader = BufferedReader(InputStreamReader(process.inputStream))
//
//                    for (nextLine in reader.readLine()) {
//                        saveLogToFile(nextLine.toString(), uri)
//                    }
//
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }


//            }

            if (fileActualPath.isNotEmpty()) {
//                saveLogFileUri(uri.toString())
//                val logFile = File(MediaStore.Files.getContentUri("external").toString(), fileName)
//                val logFile = File("$uri")
                val logFile = File(fileActualPath)
                try {
                    Runtime.getRuntime().exec("logcat -c")
                    Runtime.getRuntime().exec("logcat -f $logFile -s ${Constants.LOG_TAG}")
//                    Runtime.getRuntime().exec("logcat -s ${Constants.LOG_TAG}")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
//                if (checkIfFileExists(fileName)){
//                    var file= File(fileName)
//                    if(file.exists()){
//                        file.delete()
//                    }
//
//                }
            }


//            applicationContext.getExternalFilesDir(null)?.let { publicAppDirectory -> // getExternalFilesDir don't need storage permission
//                val logDirectory = File("${publicAppDirectory.absolutePath}/logs")
//                if (!logDirectory.exists()) {
//                    logDirectory.mkdir()
//                }
//
//                val logFile = File(logDirectory, "logcat_someapp.txt")
//
//                try {
//                    Runtime.getRuntime().exec("logcat -c")
//                    Runtime.getRuntime().exec("logcat -f $logFile -s ${Constants.LOG_TAG}")
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }

        } else {
            //--- not accessible
        }
    }

    private fun saveLogToFile(log: String, uri: Uri) {
        val resolver = this.contentResolver
        saveLogFileUri(uri.toString())
        resolver.openOutputStream(uri, "wa").use {
            it?.write(log.toByteArray())
            it?.close()
        }
    }

    private fun checkIfFileExists(fileName: String): Boolean {
        var isExist = false
        val fileUri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns._ID) //array of required columns
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} like ?"

        val selectionArgs = arrayOf(fileName)


        val mContentResolver = this.contentResolver
        val cursor: Cursor? = mContentResolver.query(
                fileUri,
                projection,
                selection,
                selectionArgs,
                null
        )
        if (cursor != null) {
            isExist = cursor.count > 0
        }
        return isExist
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            cursor?.getString(column_index!!)
        } finally {
            cursor?.close()
        }
    }

//    private fun checkIfFileExists(fileName: String): Boolean {
//        var isExist = false
//        val collection = MediaStore.Files.getContentUri("external")
//        val projection = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME,
//                MediaStore.Files.FileColumns.RELATIVE_PATH)
//        val query = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? and " +
//                MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?"
//
//        val mContentResolver = this.contentResolver
//        val cursor: Cursor? = mContentResolver.query(
//                collection,
//                projection,
//                query,
//                arrayOf("%" + Environment.DIRECTORY_DOWNLOADS + "%", "%$fileName%"),
//                null
//        )
//        if (cursor != null) {
//            isExist = cursor.count > 0
//        }
//        return isExist
//    }

//    private fun getUriFromPath(fileName: String): Uri? {
//        var fileId: Long = 0
//        val fileUri = MediaStore.Files.getContentUri("external")
//        val projection = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME,
//                MediaStore.Files.FileColumns.RELATIVE_PATH)
//        val query = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? and " +
//                MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?"
//
//        val mContentResolver = this.contentResolver
//        val cursor: Cursor? = mContentResolver.query(
//                fileUri,
//                projection,
//                query,
//                arrayOf("%" + Environment.DIRECTORY_DOWNLOADS + "%", "%$fileName%"),
//                null
//        )
//
//        if (cursor != null) {
//            cursor.moveToFirst()
//            val columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
//            fileId = cursor.getLong(columnIndex)
//            cursor.close()
//        }
//
//        return Uri.parse("$fileUri/$fileId")
//    }


    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

}