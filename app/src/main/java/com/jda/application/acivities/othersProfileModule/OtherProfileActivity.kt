package com.jda.application.acivities.othersProfileModule

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.adapter.ImageSliderAdapter
import com.jda.application.adapter.OtherProfileQsAdapter
import com.jda.application.base.activity.BaseActivity
import com.jda.application.fragments.homeFragment.HomeFragment
import com.jda.application.fragments.ratingModule.RatingOverallFragment
import com.jda.application.utils.CommonUtility.capitaliseOnlyFirstLetter
import com.jda.application.utils.Constants
import com.jda.application.utils.mainReplaceStackBundle
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import kotlinx.android.synthetic.main.fragment_other_user_profile.*
import org.json.JSONObject


class OtherProfileActivity : BaseActivity(), View.OnClickListener {
    private var mPresenter: OtherUserProfilePresenter? = null
    private var mUserId: String? = null
    private var mIsMatch: Boolean = false
    private var mAdapterMyProfileQs: OtherProfileQsAdapter? = null
    private var quesArrayList = ArrayList<OtherUserProfileResponse.Result.Answer>()

    override fun onPermissionGranted(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_other_user_profile)
        loadBannerAd(ad_view)
        mUserId = intent?.getStringExtra(Constants.BundleParams.sUserId)
        mIsMatch = intent?.getBooleanExtra(Constants.BundleParams.sIsMatch, false) ?: false
        initListeners()
        initialise()
        handleQuestionsRecyclerView()

        backIV.setOnClickListener {
            currentPage = viewPager.currentItem

            if (currentPage >= 0) {
                nextIV.visibility = View.VISIBLE
                viewPager.setCurrentItem(--currentPage, true)
            }
            if (currentPage == 0) {
                backIV.visibility = View.GONE
            }
        }
        nextIV.setOnClickListener {
            Log.e("currentPage", currentPage.toString())
            currentPage = viewPager.currentItem

            if (currentPage <= NUM_PAGES) {
                backIV.visibility = View.VISIBLE
                viewPager.setCurrentItem(++currentPage, true)
            }

            if (currentPage == NUM_PAGES - 1) {
                nextIV.visibility = View.GONE
            }

        }
    }

    private fun initialise() {
        likeOtherProfileIV.visibility = if (mIsMatch) View.GONE else View.VISIBLE
        dislikeOtherProfileIV.visibility = if (mIsMatch) View.GONE else View.VISIBLE
        appBarOtherProfile.tittleTv?.text = getString(R.string.profile)
        appBarOtherProfile?.backBt?.visibility = View.VISIBLE
        quesArrayList.clear()
        mPresenter = OtherUserProfilePresenterImp(this)
        hitGetProfileApi()
    }

    private fun initListeners() {
        reviewRatingTV?.setOnClickListener(this)
        appBarOtherProfile?.backBt?.setOnClickListener(this)
        dislikeOtherProfileIV?.setOnClickListener(this)
        likeOtherProfileIV?.setOnClickListener(this)
    }

    private fun handleQuestionsRecyclerView() {
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(myProfileQsRV)
        myProfileQsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val centerView = snapHelper.findSnapView(myProfileQsRV.layoutManager)
                val pos: Int = myProfileQsRV.layoutManager!!.getPosition(centerView!!)
                if (newState == RecyclerView.SCROLL_STATE_IDLE || (pos == 0 && newState == RecyclerView.SCROLL_STATE_DRAGGING)) {
                    mAdapterMyProfileQs?.notifyDataSetChanged()
                }
            }
        })
    }


    private fun hitGetProfileApi() {
        if (mUserId != null) {
            val param = HashMap<String, Any>()
            param[Constants.HashMapParamKeys.sUSER_ID] = mUserId!!
            mPresenter?.apiHitGetProfile(param)
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        when (pResponse) {
            is OtherUserProfileResponse -> {
                //CommonUtility.setGlideImage(otherPersonIV.context, pResponse.result.image, otherPersonIV)
//                nameAndAgeOthersTV?.text = (capitaliseOnlyFirstLetter(pResponse.result.firstName) + " " + capitaliseOnlyFirstLetter(pResponse.result.lastName) + ", " + pResponse.result.age.toString())
                nameAndAgeOthersTV?.text = (capitaliseOnlyFirstLetter(pResponse.result.firstName) + " " + capitaliseOnlyFirstLetter(pResponse.result.lastName))
                othersLocationTV?.text = pResponse.result.location?.name
                otherGenderTV?.text = pResponse.result.gender
                otherAgeTV?.text = pResponse.result.age.toString()
                otherStatusTV?.text = pResponse.result.relationshipStatus
                otherHeightTV?.text = ("${pResponse.result.height.div(12)}\' ${pResponse.result.height.rem(12)}\"")
                otherEthnicityTV?.text = (TextUtils.join(", ", pResponse.result.ethinicity))
                otherBeliefTV?.text = pResponse.result.belief
                ratingBarOtherProfile?.rating = pResponse.result.rating?.toFloat() ?: 0f
                reviewRatingTV.text = resources.getQuantityString(
                        R.plurals.review_plural,
                        pResponse.result.reviews ?: 0,
                        pResponse.result.reviews ?: 0 //var arg
                )
//                    "(${pResponse.result.reviews} Review)"

                val stringArray = resources.getStringArray(R.array.genderList)
                var str = ""
                for (i in 0 until pResponse.result.lookingFor.genderId.size) {
                    for (j in 0 until stringArray.size) {
                        if (pResponse.result.lookingFor.genderId[i] - 1 == j) {
                            str =
                                    str + stringArray[j] + if (i == pResponse.result.lookingFor.genderId.size - 1) "" else ", "
                        }
                    }


                }
                genderLookingForTV.text = str
                ageLookingForTV.text = (pResponse.result.lookingFor.minAge.toString() + "-" + pResponse.result.lookingFor.maxAge.toString())

                val mBeliefListChoice = resources.getStringArray(R.array.religionLookingForList)
                val string = StringBuilder()
                for (i in 0 until pResponse.result.lookingFor.beliefId.size) {
                    string.append(mBeliefListChoice!![pResponse.result.lookingFor.beliefId[i] - 1])
                    if (i != pResponse.result.lookingFor.beliefId.size - 1)
                        string.append(", ")
                }
                beliefLookingForTV.text = string.toString()
                heightLookingForTV.text = (("${pResponse.result.lookingFor.minHeight.div(12)}\' ${
                    pResponse.result.lookingFor.minHeight.rem(12)
                }\"") + "- " + ("${pResponse.result.lookingFor.maxHeight.div(12)}\' ${
                    pResponse.result.lookingFor.maxHeight.rem(
                            12
                    )
                }\""))

                ethnicityLookingForTV.text = (TextUtils.join(", ", pResponse.result.lookingFor.ethinicity))
                if (pResponse.result.answers != null)
                    quesArrayList.addAll(pResponse.result.answers)
                arrayList.add(pResponse.result.image)
                if (pResponse.result.gallery != null && pResponse.result.gallery.isNotEmpty()) {
                    nextIV.visibility = View.VISIBLE
                    for (i in 0 until pResponse.result.gallery.size) {
                        arrayList.add(pResponse.result.gallery[i])
                    }
                }

                setRecyclerView()
                initImageSlider()
            }
        }
    }

    private fun setRecyclerView() {
        mAdapterMyProfileQs = OtherProfileQsAdapter(quesArrayList)
        myProfileQsRV?.adapter = mAdapterMyProfileQs
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.backBt -> {
                onBackPressed()
            }
            R.id.likeOtherProfileIV -> {
                likeEvent()
                HomeFragment.mIsLikedFromProfile = true
                onBackPressed()
            }
            R.id.dislikeOtherProfileIV -> {
                dislikeEvent()
                HomeFragment.mIsLikedFromProfile = true
                onBackPressed()
            }
            R.id.reviewRatingTV -> {
                val bundle = Bundle()
                bundle.putString(Constants.BundleParams.DATA, mUserId)
                mainReplaceStackBundle(RatingOverallFragment(), bundle)
            }
        }
    }

    private fun likeEvent() {
        mUserId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sUSER_ID, mUserId)
            Log.e("likeEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.likeEvent(param)
        }
    }

    private fun dislikeEvent() {
        mUserId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sUSER_ID, mUserId)
            Log.e("likeEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.dislikeEvent(param)
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            OtherUserProfileResponse::class.java -> {
                shimmerFrameLayoutOtherProfile?.visibility = View.VISIBLE
                mainNSVOtherProfile?.visibility = View.GONE
                shimmerFrameLayoutOtherProfile?.startShimmerAnimation()
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            OtherUserProfileResponse::class.java -> {
                mainNSVOtherProfile.visibility = View.VISIBLE
                shimmerFrameLayoutOtherProfile?.visibility = View.GONE
                shimmerFrameLayoutOtherProfile?.stopShimmerAnimation()
            }
        }
    }

    override fun onBackPressed() {
        if (JDAApplication.mInstance.getRequestObject() != null) {
            JDAApplication.mInstance.getRequestObject()?.cancel()
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private var currentPage = 0
    private var NUM_PAGES = 0
    private var arrayList = ArrayList<String>()

    private fun initImageSlider() {

        //Set the pager with an adapter
        val adapter = ImageSliderAdapter(this, arrayList)
        viewPager.setAdapter(adapter)

        NUM_PAGES = arrayList.size

        /*// Auto start of viewpager
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)*/

    }
}