package com.jda.application.fragments.edit_profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.jda.application.R
import com.jda.application.adapter.MultiChoiceListAdapter
import com.jda.application.adapter.MutipleUploadAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentEditProfileBinding
import com.jda.application.fragments.editQuestionModule.EditQuestionsFragment
import com.jda.application.fragments.homeFragment.HomeFragment
import com.jda.application.fragments.profileModule.ProfileFetchResponse
import com.jda.application.fragments.signInModule.preferencesFragment.*
import com.jda.application.utils.*
import com.jda.application.utils.UserAlertUtility.Companion.showToast

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

class EditProfileFragment : BaseFragment(), OnItemClickListener,
        RangeSeekBar.OnRangeSeekBarRealTimeListener, MutipleUploadAdapter.UserClickListener,
        MultiChoiceListAdapter.UserClickListener {

    private var mBinding: FragmentEditProfileBinding? = null
    private var mList: MutableList<String>? = null
    private var mActiveSpinner: PowerSpinnerView? = null
    private var mGender: String? = null

    //    private var mGenderChoice: String? = null
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

    //    private var mEthnicityChoice: String? = null
    private var mEthnicityChoice = ArrayList<Int>()
    private var mBelief: String? = null
    private var mBeliefChoice = ArrayList<Int>()
    private var mImageBitmap: Bitmap? = null
    private var mImageUrl: String? = null
    private var mRequestModel: PreferencesRequestModelEdit? = null
    private var mPreferencesPresenter: PreferencesPresenter? = null
    private var mUploadedFileUrl: String? = null
    private var mAddress: String? = null

    private var latLngUpload: LatLng? = null

    private var myPResponse: ProfileFetchResponse? = null
    private var multiImageArrayList = ArrayList<UploadGalleryRequest>()
    private var multiImageAdapter: MutipleUploadAdapter? = null
    private var mDialog: AlertDialog? = null

    private var adapterMulti: MultiChoiceListAdapter? = null
    private var adapterGenderMulti: MultiChoiceListAdapter? = null
    private var adapterEthnicityChoiceMulti: MultiChoiceListAdapter? = null
    private var adapterEthnicityMulti: MultiChoiceListAdapter? = null
    private var arrayList = ArrayList<MutipleChoice>()
    private var genderChoiceList = ArrayList<MutipleChoice>()
    private var ethnicityChoiceList = ArrayList<MutipleChoice>()
    private var ethnicityList = ArrayList<MutipleChoice>()
    val selectedList: MutableList<String> = ArrayList()


    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myPResponse = it.getParcelable(Constants.BundleParams.DATA)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSubscription()
        initialise()
        if (myPResponse != null)
            setData(myPResponse!!)
    }

    private fun checkSubscription() {
        if (!Constants.sIsSubscribed) {
            mBinding?.imageTagTV?.visibility = View.GONE
            mBinding?.addImageIV?.visibility = View.GONE
            mBinding?.multiImageRV?.visibility = View.GONE
        }
    }

    private fun initialise() {
        mList = ArrayList()
        if (myPResponse != null)
            mImageUrl = myPResponse?.result!!.image
        mBinding?.clickHandler = this
        mRequestModel = PreferencesRequestModelEdit()
        mPreferencesPresenter = PreferencesPresenterImpl(this)
        mBinding?.appBar?.tittleTv?.text = getString(R.string.edit_profile)
        mBinding?.appBar?.backBt?.visibility = View.VISIBLE
        mActiveSpinner = mBinding?.genderSpinner
//        setDataListToAgeSpinner()
//        setDataToListForHeight()
        getDataFromDropDowns()
        handleDropDownWRTEditText()
        handleAgeAndHeightRangeSeekBar()
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
        if (myPResponse != null) {
            for (i in 0 until (myPResponse?.result?.gallery?.size?:0)) {
                multiImageArrayList.add(
                        UploadGalleryRequest(
                                myPResponse!!.result.gallery[i],
                                isServerUploaded = true
                        )
                )
            }
            if (multiImageAdapter == null) {
                multiImageAdapter = MutipleUploadAdapter(this, multiImageArrayList)
                mBinding?.multiImageRV?.adapter = multiImageAdapter
            } else {
                multiImageAdapter?.notifyDataSetChanged()
            }
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

    private fun gotGalleryImage() {

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (i in 0 until multiImageArrayList.size) {
            if (!multiImageArrayList[i].isServerUploaded) {
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
        }


        val requestBody = builder.build()

        mPreferencesPresenter?.apiGalleryFile(requestBody)
    }


    private fun getDataFromDropDowns() {
        mBinding?.genderSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            mGender = (newIndex + 1).toString()
        }
//        mBinding?.genderLookForSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mGenderChoice = (newIndex + 1).toString()
//        }
        mBinding?.ageSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            mAge = newItem
            mAgeIndex = newIndex
        }
        mBinding?.statusSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            mStatus = (newIndex + 1).toString()
        }
        mBinding?.heightSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            mHeight = convertFeetStrInInches(newItem)
            mHeightIndex = newIndex
        }
//        mBinding?.ethnicitySpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mEthnicity = (newIndex + 2).toString()
//        }
//        mBinding?.ethnicityLookForSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mEthnicityChoice = (newIndex + 1).toString()
//        }
        mBinding?.beliefSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            mBelief = (newIndex + 2).toString()
        }
        /*mBinding?.beliefLookForSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            mBeliefChoice = (newIndex + 1).toString()
        }*/
    }

    private fun handleDropDownWRTEditText() {
        mBinding?.firstNameET?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (mActiveSpinner?.isShowing!!) {
                    mActiveSpinner?.dismiss()
                }
            }
        }
        mBinding?.lastNameET?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (mActiveSpinner?.isShowing!!) {
                    mActiveSpinner?.dismiss()
                }
            }
        }
        mBinding?.locationET?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
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
            R.id.backBt -> {
                mActiveSpinner?.dismiss()
//                (mActivity as DashboardActivity).onBackPressed()
                mActivity?.onBackPressed()
            }
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
                CustomDialogue.createDialogue(mActivity!!, getString(R.string.i_am_gender), genderChoiceList, false, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        if (map.size != 0) {
                            mGender = (CustomDialogue.getChoiceSelectionsFromMap(map, false, 1).get(0)).toString()
                            mBinding?.genderSpinner!!.text = CustomDialogue.getTextFromList(map)
                        } else {
                            mGender = null
                            mBinding?.genderSpinner!!.text = ""
                        }
                    }
                }, false)
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
                CustomDialogue.createDialogue(mActivity!!, getString(R.string.looking_for_gender), genderChoiceList, true, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        mGenderChoice = CustomDialogue.getChoiceSelectionsFromMap(map, false, 1)
                        mBinding?.genderLookForSpinner!!.text = CustomDialogue.getTextFromList(map)
                    }
                }, false)


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
                setDataToListForHeight()

            }
            R.id.ethnicitySpinner -> {
                mActivity?.closeKeyBoard()


                val stringArray = resources.getStringArray(R.array.ethnicityList)
//                    ethnicityList.add(MutipleChoice(stringArray[0], false))
                ethnicityList.clear()
                for (i in 0 until stringArray.size) {
                    ethnicityList.add(MutipleChoice(stringArray[i], false))
                }
                ethnicityList.removeAt(0)
                for (item in mEthnicity) {
                    ethnicityList[item - 2].isSelected = true
                }
                CustomDialogue.createDialogue(mActivity!!, getString(R.string.my_ethnicity), ethnicityList, true, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        mEthnicity = CustomDialogue.getChoiceSelectionsFromMap(map, false)
                        mBinding?.ethnicitySpinner!!.text = CustomDialogue.getTextFromList(map)
                    }
                }, false)
                mBinding?.ethnicitySpinner!!.dismiss()


            }
            R.id.ethnicityLookForSpinner -> {
                mActivity?.closeKeyBoard()

                val stringArray = resources.getStringArray(R.array.ethnicityLookingForList)
//                        ethnicityChoiceList.add(MutipleChoice(stringArray[0], false))
                ethnicityChoiceList.clear()
                for (i in 0 until stringArray.size) {
                    ethnicityChoiceList.add(MutipleChoice(stringArray[i], false))
                }
                for (item in mEthnicityChoice) {
                    ethnicityChoiceList[item - 1].isSelected = true
                }
                CustomDialogue.createDialogue(mActivity!!, getString(R.string.looking_for_ethnicity), ethnicityChoiceList, true, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        mEthnicityChoice = CustomDialogue.getChoiceSelectionsFromMap(map, true)
                        mBinding?.ethnicityLookForSpinner!!.text = CustomDialogue.getTextFromList(map)
                    }
                }, true)
                mBinding?.ethnicityLookForSpinner!!.dismiss()


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
                CustomDialogue.createDialogue(mActivity!!, getString(R.string.my_beliefs), arrayList, false, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        if (map.size != 0) {
                            mBelief = CustomDialogue.getChoiceSelectionsFromMap(map, false, 2).get(0).toString()
                            mBinding!!.beliefSpinner.text = CustomDialogue.getTextFromList(map)
                        } else {
                            mBelief = null
                            mBinding!!.beliefSpinner.text = ""
                        }
                    }
                }, false)

                mBinding!!.beliefSpinner.dismiss()


            }
            R.id.beliefLookForSpinner -> {
                mActivity?.closeKeyBoard()

                val stringArray = resources.getStringArray(R.array.religionLookingForList)
                arrayList.clear()
                for (i in 0 until stringArray.size) {
                    arrayList.add(MutipleChoice(stringArray[i], false))
                }

                for (item in myPResponse!!.result!!.lookingFor!!.beliefId) {
                    arrayList[item - 1].isSelected = true
                }
                CustomDialogue.createDialogue(mActivity!!, getString(R.string.belief_looking_for), arrayList, true, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        mBeliefChoice = CustomDialogue.getChoiceSelectionsFromMap(map, true, 1)

                        myPResponse!!.result!!.lookingFor!!.beliefId = mBeliefChoice!!.toList()
                        mBinding!!.beliefLookForSpinner.text = CustomDialogue.getTextFromList(map)
                    }
                }, true)
//
            }
            R.id.nextBT -> {
                mActivity?.closeKeyBoard()

                mGenderChoice = ArrayList(HashSet(mGenderChoice))
                mEthnicityChoice = ArrayList(HashSet(mEthnicityChoice))
                mBeliefChoice = ArrayList(HashSet(mBeliefChoice))
                if (ValidationUtility.validatePreferences(
                                mBinding!!.firstNameET.text.toString(),
                                mBinding!!.lastNameET.text.toString(),
                                mGender,
                                mGenderChoice,
                                mAge,
                                mStatus,
                                mHeight,
                                mEthnicity,
                                mEthnicityChoice,
                                mBelief,
                                mBeliefChoice,
                                mActivity!!
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
                        mPreferencesPresenter!!.apiPreferencesPut(mRequestModel!!)
                    }
                }
            }
        }
    }

    private fun adapterItemListener(
            pId: Int,
            pPosition: Int,
            isChecked: Boolean,
            arrayList: ArrayList<MutipleChoice>,
            choiceList: MutableList<Int>?,
            spinner: PowerSpinnerView?,
            recyclerView: RecyclerView?,
            adapterMulti: MultiChoiceListAdapter?, isForAnyType: Boolean = false
    ) {
        if (pId == R.id.closeTV) {
            recyclerView?.visibility = View.GONE
        } else {
            var selectedList: MutableList<String>? = ArrayList()
            for (selected in arrayList) {
                if (selected.isSelected)
                    selectedList?.add(selected.title)
            }
            if (pPosition == 1 && isForAnyType) {
                selectedList?.clear()
                choiceList?.clear()
                if (isChecked) {
                    choiceList?.add(pPosition)
                    selectedList?.add(arrayList[pPosition].title)
                    for (i in 0 until arrayList.size) {
                        if (arrayList[i].isSelected) arrayList[i].isSelected = false
                    }
                }
                arrayList[pPosition].isSelected = isChecked
            } else {
                if (selectedList?.contains("Any") == true && isForAnyType) {
                    selectedList?.removeAt(0)
                    choiceList?.removeAt(0)
                    arrayList[1].isSelected = false
                    spinner?.text = ""
                }
                arrayList[pPosition].isSelected = isChecked
                var value = if (arrayList[0].title.equals("Any")) 1 else 0
                if (isChecked) {

                    choiceList?.add(pPosition + value)
                    selectedList?.add(arrayList[pPosition].title)
                } else {
                    choiceList?.remove(pPosition + value)
                    selectedList?.remove(arrayList[pPosition].title)
                }
            }
            adapterMulti?.notifyDataSetChanged()
            spinner?.text = (TextUtils.join(", ", selectedList!!))
        }
    }

    private var mBeliefList: Array<String>? = null
    private var mSelectedItem =
            booleanArrayOf(false, false, false, false, false, false, false, false)
    private var mIsSelected = ArrayList<String>()

    private fun openDialog() {
        mBeliefList = null
        mBeliefList = resources.getStringArray(R.array.religionLookingForList)

        val builder = AlertDialog.Builder(mActivity!!)

        builder.setTitle("Select Belief")
                .setMultiChoiceItems(
                        mBeliefList,
                        mSelectedItem
                ) { dialogInterface: DialogInterface, newIndex: Int, b: Boolean ->
                    if (mIsSelected.contains((newIndex + 2).toString())) {
                        mIsSelected.remove((newIndex + 2).toString())
                        mSelectedItem[newIndex] = false
                    } else {
                        mIsSelected.add((newIndex + 2).toString())
                        mSelectedItem[newIndex] = true
                        // mBelief = (newIndex + 2).toString()
                    }
                }
        builder.setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        builder.setCancelable(false)
        builder.show()
    }

    private fun handleSpinnerShowHide(pSpinnerView: PowerSpinnerView?) {
        if (mActiveSpinner?.isShowing!! && mActiveSpinner != pSpinnerView) {
            mActiveSpinner?.dismiss()
        }
        pSpinnerView?.showOrDismiss()
        mActiveSpinner = pSpinnerView
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

        CustomDialogue.createDialogue(mActivity!!, getString(R.string.i_am_text), list, false, object : CustomDialogue.DialoguMapCallback {
            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                if (map.size != 0) {
                    mStatus = CustomDialogue.getChoiceSelectionsFromMap(map, false, 1).get(0).toString()
                    mBinding!!.statusSpinner.text = CustomDialogue.getTextFromList(map)
                } else {
                    mStatus = null
                    mBinding!!.statusSpinner.text = ""
                }
            }
        }, false)
        mBinding!!.statusSpinner.dismiss()
    }

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
        CustomDialogue.createDialogue(mActivity!!, getString(R.string.height_text), list, false, object : CustomDialogue.DialoguMapCallback {
            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                if (map.size != 0) {
                    mHeight = convertFeetStrInInches(CustomDialogue.getChoiceSelectionsStringFromMap(map).get(0).toString())
                    mBinding!!.heightSpinner.text = CustomDialogue.getTextFromList(map)
                } else {
                    mBinding!!.heightSpinner.text = ""
                    mHeight = null
                }
            }
        }, false)

        mBinding!!.heightSpinner.dismiss()


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
                Activity.RESULT_OK -> {
                    val imageBitmap = CommonUtility.handleOnActivityResultCode(
                            mActivity!!,
                            pRequestCode = requestCode,
                            pData = data
                    )
                    mBinding?.profilePicCircularIv?.setImageBitmap(imageBitmap)
                    mImageBitmap = imageBitmap
                    mImageUrl = CommonUtility.getImagePath(mActivity!!, imageBitmap!!)
                    profilePicUpload()
                }
                Activity.RESULT_CANCELED -> {
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
        if (validateImage(mImageUrl)) {
            gotImage(mImageUrl)
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


    override fun onResume() {
        super.onResume()
        if (mImageUrl != null) {
            if (!mUploadedFileUrl.isNullOrEmpty()) {
                CommonUtility.setGlideImage(
                        mBinding?.profilePicCircularIv!!.context,
                        mImageUrl,
                        mBinding?.profilePicCircularIv!!
                )
            }
            //   mBinding?.profilePicCircularIv?.setImageURI(mImageUrl)
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        when (pResponse) {
            is PreferencesSuccessModel1 -> {
//                mActivity?.showToast(resources.getString(R.string.profile_updated_successfully))
//
//                (mActivity as DashboardActivity).onBackPressed()
                if (Constants.sIsSubscribed) {
                    openEditQuestionsScreen()
                } else {
                    mActiveSpinner?.dismiss()
                    mActivity?.onBackPressed()
                }
            }
            is ImageUploadSuccessModel -> {
                mUploadedFileUrl = pResponse.result.image
                mActivity?.showToast(resources.getString(R.string.image_uploaded_success))
            }
            is GallerySuccessModel -> {
                for (i in 0 until multiImageArrayList.size) {
                    multiImageArrayList[i].urlPath = pResponse.result!!.images[i]
                    multiImageArrayList[i].isServerUploaded = true
                }
                multiImageAdapter?.notifyDataSetChanged()
            }
            is FileDeleteResponse -> {

            }
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    private fun openEditQuestionsScreen() {
        val bundle = Bundle()
        bundle.putParcelable(Constants.BundleParams.DATA, myPResponse)
//        mActivity?.mainReplaceStackBundleTag(
//                EditQuestionsFragment.newInstance(),
//                bundle,
//                EditQuestionsFragment::javaClass.name
//        )

        mActivity?.mainAddStackBundleTag(
                EditQuestionsFragment.newInstance(),
                bundle,
                EditQuestionsFragment::javaClass.name
        )
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
        CustomDialogue.createDialogue(mActivity!!, getString(R.string.my_age), list, false, object : CustomDialogue.DialoguMapCallback {
            override fun onAdapterItemClick(map: HashMap<Int, String>) {
                if (map.size != 0) {
                    mAge = CustomDialogue.getChoiceSelectionsStringFromMap(map).get(0).toString()
                    mBinding!!.ageSpinner.text = CustomDialogue.getTextFromList(map)
                } else {
                    mBinding!!.ageSpinner.text = ""
                    mAge = null
                }
            }
        }, false)

        mBinding!!.ageSpinner.dismiss()

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


    private fun setData(data: ProfileFetchResponse) {
        mRequestModel?.firstName = data.result.firstName
        mRequestModel?.lastName = data.result.lastName
        mRequestModel?.height = data.result.height
        mRequestModel?.relationshipStatus = data.result.relationshipStatusId.toFloat().toInt()
        mRequestModel?.age = data.result.age.toInt()
        mRequestModel?.minAge = data.result.lookingFor.minAge.toInt()
        mRequestModel?.maxAge = data.result.lookingFor.maxAge.toInt()
        mRequestModel?.minHeight = data.result.lookingFor.minHeight
        mRequestModel?.maxHeight = data.result.lookingFor.maxHeight.toInt()
        mRequestModel?.gender = data.result.genderId.toFloat().toInt()
//        mRequestModel?.genderChoice = data.result.lookingFor.genderId
        mRequestModel?.genderChoice = data.result.lookingFor.genderId.toTypedArray()
//        mRequestModel?.ethinicity = data.result.etinicityId.toFloat().toInt()
        mRequestModel?.ethinicity = data.result.etinicityId.toTypedArray()
        mRequestModel?.ethinicityChoice = data.result.lookingFor.ethinicityId.toTypedArray()
        mRequestModel?.image = data.result.image
        mRequestModel?.belief = data.result.beliefId.toFloat().toInt()
        mRequestModel?.beliefChoice = data.result.lookingFor.beliefId.toTypedArray()

        mRequestModel?.latitude = data.result.location?.coordinates?.get(0)
        mRequestModel?.longitude = data.result.location?.coordinates?.get(1)
        mRequestModel?.address = data.result.location?.name
        mAddress = data.result.location?.name
        val latLng = LatLng(
                data.result.location?.coordinates?.get(0)
                        ?: 0.0, data.result.location?.coordinates?.get(1) ?: 0.0
        )
        latLngUpload = latLng
        mBinding!!.locationET.setText(data.result.location?.name)

        mHeight = data.result.height.toString()
        mStatus = data.result.relationshipStatusId
        mAge = data.result.age.toString()
        mGender = data.result.genderId
//        mGenderChoice = data.result.lookingFor.genderId.toString()
        mGenderChoice = ArrayList(data.result.lookingFor.genderId)
        mEthnicity = ArrayList(data.result.etinicityId)
//        mEthnicityChoice = data.result.lookingFor.ethinicityId.toString()
        mEthnicityChoice = ArrayList(data.result.lookingFor.ethinicityId)
        mUploadedFileUrl = data.result.image

        mBelief = data.result.beliefId
        mBeliefChoice = ArrayList(data.result.lookingFor.beliefId)

        mBinding?.firstNameET?.setText(data.result.firstName.capitalize())
        mBinding?.lastNameET?.setText(data.result.lastName.capitalize())
        if (!mUploadedFileUrl.isNullOrEmpty()) {
            CommonUtility.setGlideImage(
                    mBinding?.profilePicCircularIv!!.context,
                    mUploadedFileUrl,
                    mBinding?.profilePicCircularIv!!
            )
        }
        mBinding!!.genderSpinner.setText(data.result.gender)
//        mBinding!!.genderLookForSpinner.setText(data.result.lookingFor.gender)
        val stringArray = resources.getStringArray(R.array.genderList)
        var str = ""
        for (i in 0 until data.result.lookingFor.genderId.size) {
            for (j in 0 until stringArray.size) {
                if (data.result.lookingFor.genderId[i] - 1 == j) {
                    str = str + stringArray[j] + if (i == data.result.lookingFor.genderId.size - 1) "" else ", "
                }
            }


        }
        mBinding!!.genderLookForSpinner.setText(str)
        mBinding!!.ageSpinner.setText(data.result.age.toString())
        mBinding!!.minAgeTV.setText(data.result.lookingFor.minAge.toString())
        mBinding!!.maxAgeTV.setText(data.result.lookingFor.maxAge.toString())
        mBinding!!.ageRangeSeekBar.setMinStartValue(data.result.lookingFor.minAge.toFloat())
        mBinding!!.ageRangeSeekBar.setMaxStartValue(data.result.lookingFor.maxAge.toFloat())
        mBinding!!.ageRangeSeekBar.apply() // apply is compulsory to apply effects to crystal seekbar

        mBinding!!.statusSpinner.setText(data.result.relationshipStatus)
        mBinding!!.heightSpinner.setText(
                data.result.height.toInt().div(12).toString() + "\'" + data.result.height.toInt()
                        .rem(12).toString() + "\""
        )

        mBinding!!.heightRangeSeekBar.setMinStartValue(data.result.lookingFor.minHeight.toFloat())
        mBinding!!.heightRangeSeekBar.setMaxStartValue(data.result.lookingFor.maxHeight.toFloat())
        mBinding!!.heightRangeSeekBar.apply()

        mBinding?.minHeightTV?.text = (data.result.lookingFor.minHeight.toInt().div(12)
                .toString() + "\'" + data.result.lookingFor.minHeight.toInt().rem(12).toString() + "\"")
        mBinding?.maxHeightTV?.text = (data.result.lookingFor.maxHeight.toInt().div(12)
                .toString() + "\'" + data.result.lookingFor.maxHeight.toInt().rem(12).toString() + "\"")

//        mBinding!!.ethnicitySpinner.setText(data.result.ethinicity)
//        mBinding!!.ethnicityLookForSpinner.setText(data.result.lookingFor.ethinicity)
        var ethinticityValue = (TextUtils.join(", ", data.result.ethinicity!!))
//        for (item in data.result.ethinicity){
//            ethinticityValue=ethinticityValue+item+ ","
//        }
        mBinding!!.ethnicitySpinner.setText(ethinticityValue)
        ethinticityValue = (TextUtils.join(", ", data.result.lookingFor.ethinicity!!))
//        for (item in data.result.lookingFor.ethinicity){
//            ethinticityValue=ethinticityValue+item+ ","
//        }
        mBinding!!.ethnicityLookForSpinner.setText(ethinticityValue)
        mBinding!!.beliefSpinner.setText(data.result.belief)

        val mBeliefListChoice = resources.getStringArray(R.array.religionLookingForList)
        val string = StringBuilder()
        for (i in 0 until data.result.lookingFor.beliefId.size-1) {
            mSelectedItemChoice[data.result.lookingFor.beliefId[i] - 1] = true
            string.append(mBeliefListChoice!![data.result.lookingFor.beliefId[i] - 1])
            if (i != data.result.lookingFor.beliefId.size - 1)
                string.append(" , ")
        }
//        for (item in data.result.lookingFor.beliefId) {
//            selectedList.add(mBeliefListChoice[item])
//        }
        mBinding?.beliefLookForSpinner?.text = string.toString()

        //  mBinding!!.beliefLookForSpinner.setText(data.result.lookingFor.belief)
    }

    private fun addDataToRequestModel() {
        mRequestModel?.firstName = firstNameET.text.toString().trim()
        mRequestModel?.lastName = lastNameET.text.toString().trim()
        mRequestModel?.height = mHeight!!.toInt()
        mRequestModel?.relationshipStatus = mStatus!!.toFloat().toInt()
        mRequestModel?.age = mAge!!.toInt()
        mRequestModel?.minAge = mBinding?.minAgeTV?.text.toString().toInt()
        mRequestModel?.maxAge = mBinding?.maxAgeTV?.text.toString().toInt()
        mRequestModel?.minHeight =
                convertFeetStrInInches(mBinding?.minHeightTV?.text.toString()).toInt()
        mRequestModel?.maxHeight =
                convertFeetStrInInches(mBinding?.maxHeightTV?.text.toString()).toInt()
        mRequestModel?.gender = mGender!!.toFloat().toInt()
//        mRequestModel?.genderChoice = mGenderChoice!!.toInt()
        mRequestModel?.genderChoice = mGenderChoice?.toTypedArray()
        mRequestModel?.ethinicity = mEthnicity?.toTypedArray()
        mRequestModel?.ethinicityChoice = mEthnicityChoice?.toTypedArray()
        mRequestModel?.latitude = latLngUpload?.latitude
        mRequestModel?.longitude = latLngUpload?.longitude
        mRequestModel?.image = mUploadedFileUrl ?: "vfklg"
        mRequestModel?.belief = mBelief?.toFloat()?.toInt()
        mRequestModel?.beliefChoice = mBeliefChoice?.toTypedArray()
        mRequestModel?.address = mAddress
    }

    private var mBeliefListChoice: Array<String>? = null
    private var mSelectedItemChoice =
            booleanArrayOf(false, false, false, false, false, false, false, false)
    private var mIsSelectedChoice = ArrayList<Int>()

    /* private fun openDialogChoice() {
         mBeliefListChoice = null
         mBeliefListChoice = resources.getStringArray(R.array.religionLookingForList)

         val builder = AlertDialog.Builder(mActivity!!)
         val selectedList: MutableList<String>? = ArrayList()

         builder.setTitle("Select looking for Belief")
                 .setMultiChoiceItems(mBeliefListChoice, mSelectedItemChoice) { dialogInterface: DialogInterface, newIndex: Int, b: Boolean ->
                     if (mIsSelectedChoice.contains((newIndex + 2))) {
                         mIsSelectedChoice.remove((newIndex + 2))
                         mSelectedItemChoice[newIndex] = false
                     } else {
                         mIsSelectedChoice.add((newIndex + 2))
                         mSelectedItemChoice[newIndex] = true
                         // mBelief = (newIndex + 2).toString()
                     }
                 }
         builder.setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, i: Int ->
             dialogInterface.dismiss()
             val string = StringBuilder()
             for (i in 0 until mSelectedItemChoice.size) {
                 if (mSelectedItemChoice[i]) {
                     selectedList!!.add(mBeliefListChoice!![i])
 //                    string.append(mBeliefListChoice!![i])
 //                    if (i != mSelectedItemChoice.size - 1)
 //                        string.append(" , ")
                 }
             }

 //            mBinding!!.beliefLookForSpinner.setText(string.toString())
             mBinding!!.beliefLookForSpinner.text = TextUtils.join(", ", selectedList!!)
             mBeliefChoice = mIsSelectedChoice.toTypedArray()
         }

         builder.setCancelable(false)
         builder.show()
     }
 */

    private fun openDialogChoice() {
        mBeliefListChoice = null
        mBeliefListChoice = resources.getStringArray(R.array.religionLookingForList)
        val selectedList: MutableList<String>? = ArrayList()

        val builder = AlertDialog.Builder(mActivity!!, R.style.MyDialogTheme)
        val firstItemIndex = 0
        builder.setTitle("Select looking for Belief")
                .setMultiChoiceItems(
                        mBeliefListChoice,
                        mSelectedItemChoice
                ) { dialogInterface: DialogInterface, newIndex: Int, isChecked: Boolean ->
                    if (mSelectedItemChoice[firstItemIndex] && firstItemIndex == newIndex) {
                        for (index in 1 until mSelectedItemChoice.size) {
                            if (mSelectedItemChoice[index]) {
                                mSelectedItemChoice[index] = false
                                (dialogInterface as AlertDialog).listView.setItemChecked(index, false)
                            }
                        }
                    } else {
                        mSelectedItemChoice[newIndex] = isChecked
                        mSelectedItemChoice[firstItemIndex] = false
                        (dialogInterface as AlertDialog).listView.setItemChecked(firstItemIndex, false)
                    }
                }

        builder.setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, i: Int ->
            mDialog?.dismiss()
            mBeliefChoice?.clear()
            for (i in mBeliefListChoice!!.indices) {
                val checked = mSelectedItemChoice[i]
                if (checked) {
                    mBeliefChoice?.add(i + 1)
                    selectedList!!.add(mBeliefListChoice!![i])
                }
            }
            mBinding!!.beliefLookForSpinner.text = (TextUtils.join(", ", selectedList!!))
        }
        mDialog = builder.create()
        builder.setCancelable(false)
        builder.show()
    }

    override fun onAdapterItemClick(pId: Int, pPosition: Int, pData: UploadGalleryRequest) {
        if (pData.isServerUploaded) {
            apiDeleteImg(pData.urlPath)
        }
        multiImageArrayList.removeAt(pPosition)
        multiImageAdapter?.notifyDataSetChanged()
    }

    private fun apiDeleteImg(urlPath: String) {
        val param = HashMap<String, Any>()
        param.put("file", urlPath)
        mPreferencesPresenter!!.apiDeleteImage(param)
    }

    override fun onAdapterItemClick(pId: Int, pPosition: Int, isChecked: Boolean) {
        if (pId == R.id.closeTV) {
            mBinding!!.multiSelectionRV.visibility = View.GONE
        } else {
            if (pPosition == 1) {
                selectedList?.clear()
                mBeliefChoice?.clear()
                if (isChecked) {
                    mBeliefChoice?.add(pPosition)
                    selectedList?.add(arrayList[pPosition].title)
                    for (i in 0 until arrayList.size) {
                        if (arrayList[i].isSelected) arrayList[i].isSelected = false
                    }
                }
                arrayList[pPosition].isSelected = isChecked
            } else {
                if (selectedList?.contains("Any") == true) {
                    selectedList?.removeAt(0)
                    mBeliefChoice?.removeAt(0)
                    arrayList[1].isSelected = false
                    mBinding!!.beliefLookForSpinner.text = ""
                }
                arrayList[pPosition].isSelected = isChecked

                if (isChecked) {
                    mBeliefChoice?.add(pPosition)
                    selectedList?.add(arrayList[pPosition].title)
                } else {
                    mBeliefChoice?.remove(pPosition)
                    selectedList?.remove(arrayList[pPosition].title)
                }
            }
            adapterMulti?.notifyDataSetChanged()
            mBinding!!.beliefLookForSpinner.text = (TextUtils.join(", ", selectedList!!))
        }
    }
}