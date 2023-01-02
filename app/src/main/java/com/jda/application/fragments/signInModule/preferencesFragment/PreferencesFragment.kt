package com.jda.application.fragments.signInModule.preferencesFragment

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.acivities.LoginActivity
import com.jda.application.acivities.WebViewActivity
import com.jda.application.adapter.MultiChoiceListAdapter
import com.jda.application.adapter.MutipleUploadAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.base.service.URLs
import com.jda.application.databinding.FragmentPreferencesBinding
import com.jda.application.fragments.homeFragment.HomeFragment
import com.jda.application.fragments.questionModule.signupQuestion.QuestionsFragment
import com.jda.application.utils.*
import com.jda.application.utils.UserAlertUtility.Companion.showToast
import com.jda.application.utils.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.jda.application.utils.rangeseekbar.RangeSeekBar

import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.android.synthetic.main.fragment_preferences.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PreferencesFragment : BaseFragment(), OnItemClickListener,
        RangeSeekBar.OnRangeSeekBarRealTimeListener, MutipleUploadAdapter.UserClickListener,
        MultiChoiceListAdapter.UserClickListener {

    private var mBinding: FragmentPreferencesBinding? = null
    private var mList: MutableList<String>? = null
    private var mActiveSpinner: PowerSpinnerView? = null
    private var mGender: String? = null
    private var mGenderChoice = ArrayList<Int>()
    private var mAge: String? = null
    private var mAgeIndex: Int? = null
    private var mMinAge: String? = null
    private var mMaxAge: String? = null
    private var mStatus: String? = null
    private var mHeight: String? = null
    private var mMinHeight: Int? = null
    private var mMaxHeight: Int? = null
    private var mHeightIndex: Int? = null
    private var mEthnicity = ArrayList<Int>()
    private var mEthnicityChoice = ArrayList<Int>()

    private var mBelief: String? = null
    private var mBeliefChoice: String? = null
    private var mImageBitmap: Bitmap? = null
    private var mImageUpload: String? = null
    private var mRequestModel: PreferencesRequestModel? = null
    private var mPreferencesPresenter: PreferencesPresenter? = null
    private var mUploadedFileUrl: String? = null
    private var mAddress: String? = null

    private var latLngUpload: LatLng? = null
    private var multiImageAdapter: MutipleUploadAdapter? = null
    private var multiImageArrayList = ArrayList<UploadGalleryRequest>()
    private var isImageUploaded = false
    private var mDialog: AlertDialog? = null

    private var adapterMulti: MultiChoiceListAdapter? = null
    private var adapterGenderMulti: MultiChoiceListAdapter? = null
    private var adapterEthnicityChoiceMulti: MultiChoiceListAdapter? = null
    private var adapterEthnicityMulti: MultiChoiceListAdapter? = null
    private var arrayList = ArrayList<MutipleChoice>()
    private var genderChoiceList = ArrayList<MutipleChoice>()
    private var ethnicityChoiceList = ArrayList<MutipleChoice>()
    private var ethnicityList = ArrayList<MutipleChoice>()


    companion object {
        val TAG: String = PreferencesFragment::class.java.simpleName
        fun newInstance() = PreferencesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mBinding == null) {
            mBinding =
                    DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)
            mBinding?.nextBT?.isEnabled = false
        }
        return mBinding!!.root
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        latLngUpload = LatLng(30.6983052,76.6273401)
//        mAddress = "Mohali"
//        mBinding!!.locationET.setText("Mohali")
        checkSubscription()
        initialise()
    }

    private fun checkSubscription() {
        if (!Constants.sIsSubscribed) {
            mBinding?.imageTagTV?.visibility = View.GONE
            mBinding?.addImageIV?.visibility = View.GONE
            mBinding?.multiImageRV?.visibility = View.GONE
        }
    }

    @ExperimentalStdlibApi
    private fun initialise() {
        mList = ArrayList()
        mBinding?.clickHandler = this
        mRequestModel = PreferencesRequestModel()
        mPreferencesPresenter = PreferencesPresenterImpl(this)
        deleteAlreadyUploadedProfilePics()
        mBinding?.appBar?.tittleTv?.text = getString(R.string.complete_your_profile)
        mActiveSpinner = mBinding?.genderSpinner

//        setDataListToAgeSpinner()
//        setDataToListForHeight()
        getDataFromDropDowns()
        handleTermServiceCheckBox()
        handleDropDownWRTEditText()
        handleAgeAndHeightRangeSeekBar()
        val spannableString = SpannableString(getString(R.string.i_agree_term_condition))
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 8, 26, 0)
        mBinding?.agreeTermTV?.text = spannableString
        mBinding?.agreeTermTV?.setOnClickListener {
            startActivity(Intent(requireActivity(), WebViewActivity::class.java).apply {
                putExtra(Constants.BundleParams.sURl, URLs.APIs.sTermsUrl)
            })

        }
        mBinding?.locationET?.setOnClickListener {
            //clear focus from other views
            activity?.window?.decorView?.clearFocus()
            openLocationPicker()
        }
        mBinding?.addImageIV?.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (CommonUtility.checkCameraAndGalleryPermission(
                                requireActivity(),
                                Constants.RequestCodes.MULTI_PERMISSIONS_RESULT
                        )
                ) {
                    uploadMutiImage()
                }
            }
        }
        if ((mActivity as LoginActivity).mPreferencesApiHit) {
            setDataOnBackPressed()
        } else {
            setDataInPreferencesFromSocialLogin()
        }
    }

    private fun uploadMutiImage() {
        if (mBinding?.multiImageRV?.adapter == null || mBinding?.multiImageRV?.adapter?.itemCount!! < 5) {
            UwMediaPicker
                    .with(this)                        // Activity or Fragment
                    .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
                    .setGridColumnCount(4)                                  // Grid column count, default is 3
                    .setMaxSelectableMediaCount(5 - if (mBinding?.multiImageRV?.adapter != null) mBinding?.multiImageRV?.adapter?.itemCount!! else 0)                         // Maximum selectable media count, default is null which means infinite
                    .setLightStatusBar(true)                                // Is llight status bar enable, default is true
                    .enableImageCompression(false)                // Is image compression enable, default is false
                    .launch { selectedMediaList ->
                        val count = multiImageAdapter?.itemCount ?: 0
                        for (i in 0 until selectedMediaList!!.size) {
                            multiImageArrayList.add(UploadGalleryRequest(selectedMediaList[i].mediaPath))
                        }
                        if (multiImageAdapter == null) {
                            multiImageAdapter = MutipleUploadAdapter(this, multiImageArrayList)
                            mBinding?.multiImageRV?.adapter = multiImageAdapter
                        } else {
                            multiImageAdapter?.notifyDataSetChanged()
                        }
                        gotGalleryImage()
                    }
        } else {
            context?.showToast(getString(R.string.max_five_pics))
        }
    }

    @ExperimentalStdlibApi
    private fun setDataInPreferencesFromSocialLogin() {
        JDAApplication.mInstance.getProfile()!!.result.let {
            mBinding?.firstNameET?.setText(capitaliseOnlyFirstLetter(it?.firstName))
            mBinding?.lastNameET?.setText(capitaliseOnlyFirstLetter(it?.lastName))
            mUploadedFileUrl = it?.image
            if (!mUploadedFileUrl.isNullOrEmpty()) {
                CommonUtility.setGlideImage(
                        mBinding?.profilePicCircularIv!!.context,
                        mUploadedFileUrl,
                        mBinding?.profilePicCircularIv!!
                )
            }

            if (it?.image.equals("/user/profile/image/default.jpg"))
                mUploadedFileUrl = null
        }
    }

    private fun setDataOnBackPressed() {
        if (mRequestModel != null) {
//            mBinding?.genderSpinner?.selectItemByIndex(mGender?.toInt()!!.minus(2))
////            mBinding?.genderLookForSpinner?.selectItemByIndex(mGenderChoice?.toInt()!!.minus(1))
//            mBinding?.ageSpinner?.selectItemByIndex(mAgeIndex!!)
//            mBinding?.heightSpinner?.selectItemByIndex(mHeightIndex!!)
//            mBinding?.statusSpinner?.selectItemByIndex(mStatus!!.toInt().minus(1))
////            mBinding?.ethnicitySpinner?.selectItemByIndex(mEthnicity?.toInt()!!.minus(2))
////            mBinding?.ethnicityLookForSpinner?.selectItemByIndex(
////                mEthnicityChoice?.toInt()!!.minus(1)
////            )
//            mBinding?.beliefSpinner?.selectItemByIndex(mBelief?.toInt()!!.minus(2))
            //mBinding?.beliefLookForSpinner?.selectItemByIndex(mBeliefChoice?.toInt()!!.minus(1))
            (mActivity as LoginActivity).mPreferencesApiHit = false
        }
    }

    private fun getDataFromDropDowns() {
//        mBinding?.genderSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mGender = (newIndex + 1).toString()
//        }
////        mBinding?.genderLookForSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
////            mGenderChoice = (newIndex + 1).toString()
////        }
//        mBinding?.ageSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mAge = newItem
//            mAgeIndex = newIndex
//        }
//        mBinding?.statusSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mStatus = (newIndex + 1).toString()
//        }
//        mBinding?.heightSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mHeight = convertFeetStrInInches(newItem)
//            mHeightIndex = newIndex
//        }
////        mBinding?.ethnicitySpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
////            mEthnicity = (newIndex + 2).toString()
////        }
////        mBinding?.ethnicityLookForSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
////            mEthnicityChoice = (newIndex + 1).toString()
////        }
//        mBinding?.beliefSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mBelief = (newIndex + 2).toString()
//        }
//        /* mBinding?.beliefLookForSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//             mBeliefChoice = (newIndex + 1).toString()
//         }*/
    }

    private fun handleTermServiceCheckBox() {
        mBinding?.termAndServicesCB?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                mBinding?.nextBT?.isEnabled = isChecked
                mBinding?.nextBT?.alpha = 1f
            } else {
                mBinding?.nextBT?.isEnabled = isChecked
                mBinding?.nextBT?.alpha = 0.4f
            }
        }
    }

    private fun handleDropDownWRTEditText() {
        mBinding?.firstNameET?.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (mActiveSpinner?.isShowing!!) {
                    mActiveSpinner?.dismiss()
                }
            }
        }
        mBinding?.lastNameET?.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (mActiveSpinner?.isShowing!!) {
                    mActiveSpinner?.dismiss()
                }
            }
        }
        mBinding?.locationET?.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (mActiveSpinner?.isShowing!!) {
                    mActiveSpinner?.dismiss()
                }
            }
        }
    }

    private fun handleAgeAndHeightRangeSeekBar() {
        mBinding?.heightRangeSeekBar?.setMinValue(48f)
        mBinding?.heightRangeSeekBar?.setMaxValue(77f)
        mBinding?.ageRangeSeekBar?.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue, maxValue ->
            mMinAge = minValue.toString()
            mMaxAge = maxValue.toString()
            mBinding?.minAgeTV?.text = minValue.toString()
            mBinding?.maxAgeTV?.text = maxValue.toString()
        })
        mBinding?.heightRangeSeekBar?.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue, maxValue ->
            mMinHeight = minValue.toInt()
            mMaxHeight = maxValue.toInt()
            mBinding?.minHeightTV?.text =
                    (minValue.toInt().div(12).toString() + "\'" + minValue.toInt().rem(12)
                            .toString() + "\"")
            if (maxValue.toInt().div(12).toString().equals("6") && maxValue.toInt().rem(12)
                            .toString().equals("5")
            ) {
                mBinding?.maxHeightTV?.text =
                        (maxValue.toInt().div(12).toString() + "\'" + maxValue.toInt().rem(12)
                                .toString() + "\"+")
            } else {
                mBinding?.maxHeightTV?.text =
                        (maxValue.toInt().div(12).toString() + "\'" + maxValue.toInt().rem(12)
                                .toString() + "\"")
            }
        })
    }


    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.addProfilePicRl -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CommonUtility.checkCameraAndGalleryPermission(
                                    requireActivity(),
                                    Constants.RequestCodes.ALL_PERMISSIONS_RESULT
                            )
                    ) {
                        CommonUtility.choosePicture(requireActivity())
                    }
                }
            }
            R.id.genderSpinner -> {
                mActivity?.closeKeyBoard()
                val stringArray = resources.getStringArray(R.array.genderList)
//                genderChoiceList.add(MutipleChoice(stringArray[0], false))
                genderChoiceList.clear()
                for (i in 0 until stringArray.size) {
                    genderChoiceList.add(MutipleChoice(stringArray[i], false))
                }
                if (mGender != null) {
                    genderChoiceList[mGender!!.toDouble().toInt() - 1].isSelected = true
                }
                CustomDialogue.createDialogue(
                        mActivity!!,
                        getString(R.string.i_am_gender),
                        genderChoiceList,
                        false,
                        object : CustomDialogue.DialoguMapCallback {
                            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                                if (map.size != 0) {
                                    mGender = (CustomDialogue.getChoiceSelectionsFromMap(map, false, 1)
                                            .get(0)).toString()
                                    mBinding?.genderSpinner!!.text = CustomDialogue.getTextFromList(map)
                                } else {
                                    mGender = null
                                    mBinding?.genderSpinner!!.text = ""
                                }
                            }
                        },
                        false
                )
                mBinding?.genderSpinner!!.dismiss()
            }
            R.id.genderLookForSpinner -> {
                mActivity?.closeKeyBoard()
                val stringArray = resources.getStringArray(R.array.genderList)
                genderChoiceList.clear()
                for (i in 0 until stringArray.size) {
                    genderChoiceList.add(MutipleChoice(stringArray[i], false))
                }
                for (item in mGenderChoice) {
                    genderChoiceList[item - 1].isSelected = true
                }
                CustomDialogue.createDialogue(
                        mActivity!!,
                        getString(R.string.looking_for_gender),
                        genderChoiceList,
                        true,
                        object : CustomDialogue.DialoguMapCallback {
                            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                                mGenderChoice = CustomDialogue.getChoiceSelectionsFromMap(map, false, 1)
                                mBinding?.genderLookForSpinner!!.text =
                                        CustomDialogue.getTextFromList(map)
                            }
                        },
                        false
                )


            }
            R.id.ageSpinner -> {
                mActivity?.closeKeyBoard()
                setDataListToAgeSpinner()
            }
            R.id.statusSpinner -> {
                mActivity?.closeKeyBoard()
                setStatusSpinnerData()
            }
            R.id.heightSpinner -> {
                mActivity?.closeKeyBoard()
                setDataToListForHeight()
            }
            R.id.ethnicitySpinner -> {
                mActivity?.closeKeyBoard()


                if (adapterEthnicityMulti == null) {
                    val stringArray = resources.getStringArray(R.array.ethnicityList)

                    ethnicityList.clear()
                    for (i in 0 until stringArray.size) {
                        ethnicityList.add(MutipleChoice(stringArray[i], false))
                    }
                    ethnicityList.removeAt(0)
                    for (item in mEthnicity) {
                        ethnicityList[item - 2].isSelected = true
                    }
                    CustomDialogue.createDialogue(
                            mActivity!!,
                            getString(R.string.my_ethnicity),
                            ethnicityList,
                            true,
                            object : CustomDialogue.DialoguMapCallback {
                                override fun onAdapterItemClick(map: HashMap<Int, String>) {
                                    mEthnicity = CustomDialogue.getChoiceSelectionsFromMap(map, false)
                                    mBinding?.ethnicitySpinner!!.text =
                                            CustomDialogue.getTextFromList(map)
                                }
                            },
                            false
                    )
                    mBinding?.ethnicitySpinner!!.dismiss()

                }
            }
            R.id.ethnicityLookForSpinner -> {
                mActivity?.closeKeyBoard()

                if (adapterEthnicityChoiceMulti == null) {
                    val stringArray = resources.getStringArray(R.array.ethnicityLookingForList)
//                        ethnicityChoiceList.add(MutipleChoice(stringArray[0], false))
                    ethnicityChoiceList.clear()
                    for (i in 0 until stringArray.size) {
                        ethnicityChoiceList.add(MutipleChoice(stringArray[i], false))
                    }
                    for (item in mEthnicityChoice) {
                        ethnicityChoiceList[item - 1].isSelected = true
                    }
                    CustomDialogue.createDialogue(
                            mActivity!!,
                            getString(R.string.looking_for_ethnicity),
                            ethnicityChoiceList,
                            true,
                            object : CustomDialogue.DialoguMapCallback {
                                override fun onAdapterItemClick(map: HashMap<Int, String>) {
                                    mEthnicityChoice =
                                            CustomDialogue.getChoiceSelectionsFromMap(map, true)
                                    mBinding?.ethnicityLookForSpinner!!.text =
                                            CustomDialogue.getTextFromList(map)
                                }
                            },
                            true
                    )
                    mBinding?.ethnicityLookForSpinner!!.dismiss()
                }
            }
            R.id.beliefSpinner -> {
                mActivity?.closeKeyBoard()
                val stringArray = resources.getStringArray(R.array.religionList)
                arrayList.clear()
                for (i in 0 until stringArray.size) {
                    arrayList.add(MutipleChoice(stringArray[i], false))
                }
//                arrayList.removeAt(0)
                if (mBelief != null) {
                    arrayList[mBelief!!.toFloat().toInt() - 2].isSelected = true
                }
                CustomDialogue.createDialogue(
                        mActivity!!,
                        getString(R.string.my_beliefs),
                        arrayList,
                        false,
                        object : CustomDialogue.DialoguMapCallback {
                            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                                if (map.size != 0) {
                                    mBelief =
                                            CustomDialogue.getChoiceSelectionsFromMap(map, false, 2).get(0)
                                                    .toString()
                                    mBinding!!.beliefSpinner.text = CustomDialogue.getTextFromList(map)
                                } else {
                                    mBelief = null
                                    mBinding!!.beliefSpinner.text = ""
                                }
                            }
                        },
                        false
                )

                mBinding!!.beliefSpinner.dismiss()

            }
            R.id.beliefLookForSpinner -> {
                mActivity?.closeKeyBoard()

                val stringArray = resources.getStringArray(R.array.religionLookingForList)
                arrayList.clear()
                for (i in 0 until stringArray.size) {
                    arrayList.add(MutipleChoice(stringArray[i], false))
                }

                for (item in mIsSelectedChoice) {
                    arrayList[item - 1].isSelected = true
                }
                CustomDialogue.createDialogue(
                        mActivity!!,
                        getString(R.string.belief_looking_for),
                        arrayList,
                        true,
                        object : CustomDialogue.DialoguMapCallback {
                            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                                mIsSelectedChoice =
                                        CustomDialogue.getChoiceSelectionsFromMap(map, true, 1)
                                mBinding!!.beliefLookForSpinner.text =
                                        CustomDialogue.getTextFromList(map)
                            }
                        },
                        true
                )
            }
            R.id.nextBT -> {
                Log.d("TAG", "onItemClick: $mUploadedFileUrl")
                mActivity?.closeKeyBoard()
                if (ValidationUtility.validatePreferences(
                                mBinding!!.firstNameET.text.toString(),
                                mBinding!!.lastNameET.text.toString(),
                                mGender,
                                mGenderChoice,
                                mAge,
                                mBinding?.locationET?.text.toString(),
                                mStatus,
                                mHeight,
                                mEthnicity,
                                mEthnicityChoice,
                                mBelief,
                                mIsSelectedChoice,
                                mActivity!!,
                                isImageUploaded
                        ) && validateImage(mUploadedFileUrl) && validateLocation(
                                mBinding?.locationET?.text.toString().trim()
                        ) && ValidationUtility.validateMinMax(
                                mMinAge.toString(),
                                mMaxAge.toString(),
                                mActivity!!
                        )
                ) {
                    if (mRequestModel != null) {
                        addDataToRequestModel()
                        mActivity?.closeKeyBoard()
                        mPreferencesPresenter!!.apiPreferences(mRequestModel!!)
                    }
                }
            }
        }
    }


    private var mBeliefListChoice: Array<String>? = null
    private var mSelectedItemChoice =
            booleanArrayOf(false, false, false, false, false, false, false, false)
    private var mIsSelectedChoice = ArrayList<Int>()


    private fun setDataToListForHeight() {
        mList?.clear()
        var list = ArrayList<MutipleChoice>()
        var inches = -1
        var feet = -1
        if (mHeight != null) {
            inches = mHeight!!.toDouble().toInt().rem(12)
            feet = mHeight!!.toDouble().toInt().div(12)
        }
        var index = 0;
        var mSelectedIndex = -1;
        for (i in 4..6) {
            for (j in 0..11) {
                if (i == 6 && j == 6) {
                    break
                }
                if (i == 6 && j == 5) {
                    list.add(MutipleChoice(i.toString() + "\'" + j.toString() + "\"+", false))
//                    mList!!.add(i.toString() + "\'" + j.toString() + "\"+")
                } else {
                    list.add(MutipleChoice(i.toString() + "\'" + j.toString() + "\"", false))
//                    mList!!.add(i.toString() + "\'" + j.toString() + "\"")
                }
                if (i == feet.toInt() && j == inches.toInt()) {
                    mSelectedIndex = index
                }
                index++

            }
        }
        if (mSelectedIndex != -1) {
            list.get(mSelectedIndex).isSelected = true
        }
        CustomDialogue.createDialogue(
                mActivity!!,
                getString(R.string.height_text),
                list,
                false,
                object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        if (map.size != 0) {
                            mHeight = convertFeetStrInInches(
                                    CustomDialogue.getChoiceSelectionsStringFromMap(map).get(0).toString()
                            )
                            mBinding!!.heightSpinner.text = CustomDialogue.getTextFromList(map)
                        } else {
                            mBinding!!.heightSpinner.text = ""
                        }
                    }
                },
                false
        )

        mBinding!!.heightSpinner.dismiss()


    }

    private fun setStatusSpinnerData() {
        var list = ArrayList<MutipleChoice>()
        val stringArray = resources.getStringArray(R.array.relationshipList)
//                    ethnicityList.add(MutipleChoice(stringArray[0], false))
        for (i in 0 until stringArray.size) {
            list.add(MutipleChoice(stringArray[i], false))
        }
        if (mStatus != null) {
            list[mStatus!!.toFloat().toInt() - 1].isSelected = true
        }
        CustomDialogue.createDialogue(
                mActivity!!,
                getString(R.string.i_am_text),
                list,
                false,
                object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        if (map.size != 0) {
                            mStatus = CustomDialogue.getChoiceSelectionsFromMap(map, false, 1).get(0)
                                    .toString()
                            mBinding!!.statusSpinner.text = CustomDialogue.getTextFromList(map)
                        } else {
                            mStatus = null
                            mBinding!!.statusSpinner.text = ""
                        }
                    }
                },
                false
        )
        mBinding!!.statusSpinner.dismiss()
    }

    private fun setDataListToAgeSpinner() {
        mList?.clear()
        var list = ArrayList<MutipleChoice>()

        for (i in 18..99) {
            list.add(MutipleChoice(i.toString(), false))
//            mList?.add(i.toString())
        }
        if (mAge != null) {
            list.get(mAge!!.toDouble().toInt() - 18).isSelected = true
        }
        CustomDialogue.createDialogue(
                mActivity!!,
                getString(R.string.my_age),
                list,
                false,
                object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        if (map.size != 0) {
                            mAge =
                                    CustomDialogue.getChoiceSelectionsStringFromMap(map).get(0).toString()
                            mBinding!!.ageSpinner.text = CustomDialogue.getTextFromList(map)
                        } else {
                            mBinding!!.ageSpinner.text = ""
                            mAge = null
                        }
                    }
                },
                false
        )

        mBinding!!.ageSpinner.dismiss()

    }

    private fun convertFeetStrInInches(pChooseString: String?): String {
        if (!pChooseString!!.isDigitsOnly()) {

            if (pChooseString.substring(pChooseString.lastIndex - 1).equals("\"+")) {
                return ((pChooseString.substring(0, 1)).trim()
                        .toInt() * 12 + pChooseString.substring(
                        2,
                        pChooseString.length - 2
                ).trim().toInt()).toString()
            } else {

                return ((pChooseString.substring(0, 1)).trim()
                        .toInt() * 12 + pChooseString.substring(
                        2,
                        pChooseString.length - 1
                ).trim().toInt()).toString()
            }
        } else {
            return pChooseString
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ChoosePhotoAction.GALLERY.values || requestCode == Constants.ChoosePhotoAction.CAMERA.values) {
            when (resultCode) {
                RESULT_OK -> {
                    val imageBitmap = CommonUtility.handleOnActivityResultCode(
                            mActivity!!,
                            pRequestCode = requestCode,
                            pData = data
                    )
                    mImageBitmap = imageBitmap
                    mImageUpload = CommonUtility.getImagePath(mActivity!!, imageBitmap!!)
                    profilePicUpload()
                }
                RESULT_CANCELED -> {
                    return
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.RequestCodes.MULTI_PERMISSIONS_RESULT) {
            uploadMutiImage()
        } else {
            CommonUtility.handlePermissionResult(requireActivity(), requestCode, permissions)
        }
    }

    private fun profilePicUpload() {
        if (validateImage(mImageUpload)) {
            gotImage(mImageUpload)
        }
    }

    private fun validateImage(pImagePath: String?): Boolean {
        if (pImagePath.isNullOrEmpty()) {
            mActivity?.showToast(resources.getString(R.string.image_error_validation))
            return false
        }
        return true
    }

    private fun validateLocation(pImagePath: String?): Boolean {
        if (pImagePath.isNullOrEmpty()) {
            mActivity?.showToast(getString(R.string.please_enter_the_location))
            return false
        }
        return true
    }

    private fun gotImage(mImageUrl: String?) {
        val file = File(mImageUrl!!)
        val requestFile: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        mPreferencesPresenter?.apiUploadFile(body)
    }

    private fun deleteAlreadyUploadedProfilePics() {
        mPreferencesPresenter?.apiDeleteGallery()
    }

    private fun gotGalleryImage() {

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (i in 0 until multiImageArrayList.size) {
            if (multiImageArrayList[i].isServerUploaded) {
                continue
            }
            val file = File(multiImageArrayList[i].urlPath)
            builder.addFormDataPart(
                    "files",
                    file.name,
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            )
        }

        val requestBody = builder.build()

        mPreferencesPresenter?.apiGalleryFile(requestBody)
    }

    private fun addDataToRequestModel() {
        mRequestModel?.firstName = firstNameET.text.toString().trim()
        mRequestModel?.lastName = lastNameET.text.toString().trim()
        mRequestModel?.height = mHeight!!.toInt()
        mRequestModel?.relationshipStatus = mStatus!!.toInt()
        mRequestModel?.age = mAge!!.toInt()
        mRequestModel?.minAge = mBinding?.minAgeTV?.text.toString().toInt()
        mRequestModel?.maxAge = mBinding?.maxAgeTV?.text.toString().toInt()
        mRequestModel?.minHeight =
                convertFeetStrInInches(mBinding?.minHeightTV?.text.toString()).toInt()
        mRequestModel?.maxHeight =
                convertFeetStrInInches(mBinding?.maxHeightTV?.text.toString()).toInt()
        mRequestModel?.gender = mGender!!.toInt()
        mRequestModel?.genderChoice = mGenderChoice.toTypedArray()
//        mRequestModel?.genderChoice = arrayOf(1, 2)
        mRequestModel?.ethinicity = mEthnicity.toTypedArray()
//        mRequestModel?.ethinicity = arrayOf(3, 2)
        mRequestModel?.ethinicityChoice = mEthnicityChoice.toTypedArray()
//        mRequestModel?.ethinicityChoice = arrayOf(1, 2)
        mRequestModel?.latitude = latLngUpload?.latitude
        mRequestModel?.longitude = latLngUpload?.longitude
        mRequestModel?.image = mUploadedFileUrl ?: "vfklg"
        mRequestModel?.belief = mBelief
        mRequestModel?.beliefChoice = mIsSelectedChoice.toTypedArray()
        mRequestModel?.agreement = mBinding?.termAndServicesCB?.isChecked!!
        mRequestModel?.address = mAddress
    }


    override fun onResume() {
        super.onResume()
        if (mImageUpload != null) {
            if (!mUploadedFileUrl.isNullOrEmpty()) {
                CommonUtility.setGlideImage(
                        mBinding?.profilePicCircularIv!!.context,
                        mImageUpload,
                        mBinding?.profilePicCircularIv!!
                )
            }
            //   mBinding?.profilePicCircularIv?.setImageURI(mImageUrl)
        }
    }

    @ExperimentalStdlibApi
    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        when (pResponse) {
            is PreferencesSuccessModel1 -> {
                val data = JDAApplication.mInstance.getProfile()
                data?.result?.arePreferencesSet = true
                data?.result?.myGender = pResponse.result?.myGender
                JDAApplication.mInstance.setProfile(data)
                JDAApplication.mInstance.setLoginStatus(Constants.ScreenStatus.sPREFERENCES_SET)
                (mActivity as LoginActivity).mPreferencesApiHit = true

                if (Constants.sIsSubscribed) {
                    mActivity?.loginReplace(QuestionsFragment.newInstance())
                } else {
                    startActivity(Intent(mActivity, DashboardActivity::class.java))
                    mActivity?.showToast(getString(R.string.login_success))
                    mActivity?.finish()
                }
            }
            is ImageUploadSuccessModel -> {
                mUploadedFileUrl = pResponse.result.image
                if (!mUploadedFileUrl.isNullOrEmpty()) {
                    CommonUtility.setGlideImage(
                            mBinding?.profilePicCircularIv!!.context,
                            mUploadedFileUrl,
                            mBinding?.profilePicCircularIv!!
                    )
                }
                mActivity?.showToast(resources.getString(R.string.image_uploaded_success))
            }
            is GallerySuccessModel -> {
//                multiImageArrayList.clear()
                for (i in 0 until multiImageArrayList.size) {
//                    multiImageArrayList.add(UploadGalleryRequest(pResponse.result!!.images[i],Constants.UPLOAD_END,true))
                    multiImageArrayList[i].urlPath = pResponse.result!!.images[i]
                    multiImageArrayList[i].isServerUploaded = true
                }
                isImageUploaded = true
                multiImageAdapter?.notifyDataSetChanged()

            }
            is FileDeleteResponse -> {

            }
        }
    }


    override fun onValuesChanging(minValue: Float, maxValue: Float) {

    }

    override fun onValuesChanging(minValue: Int, maxValue: Int) {
        mBinding?.minHeightTV?.text =
                (minValue.div(12).toString() + "\'" + minValue.rem(12).toString() + "\"")
        if (maxValue.div(12).toString().equals("6") && maxValue.rem(12).toString().equals("5")) {
            mBinding?.maxHeightTV?.text =
                    (maxValue.div(12).toString() + "\'" + maxValue.rem(12).toString() + "\"+")
        } else {
            mBinding?.maxHeightTV?.text =
                    (maxValue.div(12).toString() + "\'" + maxValue.rem(12).toString() + "\"")
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ImageUploadSuccessModel::class.java -> {
                mBinding?.progressImageLL?.visibility = View.VISIBLE
            }
            PreferencesSuccessModel1::class.java -> {
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, mActivity, false)
            }
            GallerySuccessModel::class.java -> {
                for (i in 0 until multiImageArrayList.size) {
                    if (!multiImageArrayList[i].isServerUploaded)
                        multiImageArrayList[i].isUploaded = Constants.UPLOAD_START
                }
                multiImageAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ImageUploadSuccessModel::class.java -> {
                mBinding?.progressImageLL?.visibility = View.GONE
            }
            PreferencesSuccessModel1::class.java -> {
                UserAlertUtility.hideProgressDialog()
            }
            GallerySuccessModel::class.java -> {
                if (isResult) {
                    for (i in 0 until multiImageArrayList.size) {
                        if (!multiImageArrayList[i].isServerUploaded)
                            multiImageArrayList[i].isUploaded = Constants.UPLOAD_END
                    }
                    multiImageAdapter?.notifyDataSetChanged()
                    Handler().postDelayed({
                        for (i in 0 until multiImageArrayList.size) {
                            multiImageArrayList[i].isUploaded = Constants.UPLOAD_HIDE
                        }
                        multiImageAdapter?.notifyDataSetChanged()
                    }, 2000)
                }
            }
        }
    }

    private fun openLocationPicker() {
        val fields = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(mActivity!!)
        getResultLauncher.launch(intent)
    }

    private val getResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        result?.let {
                            val place = Autocomplete.getPlaceFromIntent(result.data!!)
                            Log.i(HomeFragment.TAG, "Place: ${place.address}, ${place.id}")
                            latLngUpload = place.latLng
                            mAddress = place.address
                            mBinding!!.locationET.setText(place.address)
                        }
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        // TODO: Handle the error.
                        result?.let {
                            val status = Autocomplete.getStatusFromIntent(result.data!!)
                            Log.i(HomeFragment.TAG, status.statusMessage ?: "")
                            mActivity!!.showToast(status.statusMessage!!)
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        // The user canceled the operation.
                    }
                }
            }

    override fun onAdapterItemClick(pId: Int, pPosition: Int, pData: UploadGalleryRequest) {
        if (pData.isServerUploaded) {
            apiDeleteImg(pData.urlPath)
        }
        multiImageArrayList.removeAt(pPosition)
        multiImageAdapter?.notifyDataSetChanged()
        if (multiImageAdapter?.itemCount == 0)
            isImageUploaded = false
    }

    private fun apiDeleteImg(urlPath: String) {
        val param = HashMap<String, Any>()
        param.put("file", urlPath)
        mPreferencesPresenter!!.apiDeleteImage(param)
    }

    @ExperimentalStdlibApi
    private fun capitaliseOnlyFirstLetter(data: String?): String? {
        var capitalString = ""
        if (data?.length != 0) {
            capitalString = data?.substring(0, 1)?.uppercase(Locale.ROOT) + data?.substring(1)
        }
        return capitalString
    }

    val selectedList: MutableList<String> = ArrayList()

    override fun onAdapterItemClick(pId: Int, pPosition: Int, isChecked: Boolean) {
        if (pId == R.id.closeTV) {
            mBinding!!.multiSelectionRV.visibility = View.GONE
        } else {
            if (pPosition == 1) {
                selectedList?.clear()
                mIsSelectedChoice.clear()
                if (isChecked) {
                    mIsSelectedChoice.add(pPosition)
                    selectedList?.add(arrayList[pPosition].title)
                    for (i in 0 until arrayList.size) {
                        if (arrayList[i].isSelected) arrayList[i].isSelected = false
                    }
                }
                arrayList[pPosition].isSelected = isChecked
            } else {
                if (selectedList?.contains("ANY") == true) {
                    selectedList?.removeAt(0)
                    mIsSelectedChoice?.removeAt(0)
                    arrayList[1].isSelected = false
                    mBinding!!.beliefLookForSpinner.text = ""
                }
                arrayList[pPosition].isSelected = isChecked

                if (isChecked) {
                    mIsSelectedChoice.add(pPosition)
                    selectedList?.add(arrayList[pPosition].title)
                } else {
                    mIsSelectedChoice.remove(pPosition)
                    selectedList?.remove(arrayList[pPosition].title)
                }
            }
            adapterMulti?.notifyDataSetChanged()
            mBinding!!.beliefLookForSpinner.text = (TextUtils.join(", ", selectedList!!))
        }
    }
}