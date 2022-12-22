package com.jda.application.fragments.profileModule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.acivities.LoginActivity
import com.jda.application.adapter.ImageSliderAdapter
import com.jda.application.adapter.MyProfileQsAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentProfileBinding
import com.jda.application.fragments.editQuestionModule.EditQuestionsFragment
import com.jda.application.fragments.edit_profile.EditProfileActivity
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.UserAlertUtility
import com.jda.application.utils.mainReplaceStackBundleTag
import kotlinx.android.synthetic.main.fragment_other_user_profile.*

class ProfileFragment : BaseFragment(), OnItemClickListener {

    private var mBinding: FragmentProfileBinding? = null
    private var presenter: MyProfilePresenter? = null
    private var mAdapterMyProfileQs: MyProfileQsAdapter? = null
    private var quesArrayList = ArrayList<ProfileFetchResponse.Result.Answer>()
    private var mBundle: Bundle? = null
    private var mDialog: Dialog? = null
    private var arrayList = ArrayList<String>()
    private var currentPage = 0
    private var NUM_PAGES = 0
    private var imageAdapter: ImageSliderAdapter? = null

    companion object {
        val TAG: String = ProfileFragment::class.java.simpleName
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: ProfileFragment")
        
        initialise()
        handleQuestionsRecyclerView()
    }

    private fun initialise() {
        quesArrayList.clear()
        presenter = MyProfilePresenterImp(this)
        mBinding?.clickHandler = this
        mBundle = Bundle()
        initImageSlider()
    }

    private fun handleQuestionsRecyclerView() {
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mBinding!!.myProfileQsRV)
        mBinding!!.myProfileQsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val centerView = snapHelper.findSnapView(mBinding!!.myProfileQsRV.layoutManager)
                val pos: Int = mBinding!!.myProfileQsRV.layoutManager!!.getPosition(centerView!!)
                if (newState == RecyclerView.SCROLL_STATE_IDLE || (pos == 0 && newState == RecyclerView.SCROLL_STATE_DRAGGING)) {
                    mAdapterMyProfileQs!!.notifyDataSetChanged()
                }
            }
        })
    }

    @ExperimentalStdlibApi
    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.logoutIV -> {
//                openAlertDialog(getString(R.string.logout), getString(R.string.logout_alert_msg))

                UserAlertUtility.openCustomDialog(
                        context, context?.getString(R.string.logout),
                        context?.getString(R.string.logout_alert_msg), null, null,
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {
                                presenter?.apiLogout()
                            }

                            override fun onNoClick() {

                            }
                        }, true)
            }
            R.id.editProfileSaveBT -> {

                val intent = Intent(mActivity, EditProfileActivity::class.java)
                val profile = Gson().toJson(myPResponse)
                intent.putExtra(Constants.BundleParams.PROFILE_DATA, profile)
                intent.putExtra(Constants.BundleParams.sFromSubscription, false)
                startActivity(intent)

//                val bundle = Bundle()
//                bundle.putParcelable(Constants.BundleParams.DATA, myPResponse)
//                mActivity?.mainReplaceStackBundle(EditProfileFragment.newInstance(), bundle)

//                mActivity?.mainReplaceStackBundleTag(EditProfileFragment.newInstance(), bundle,
//                        EditProfileFragment::javaClass.name)

                //   mActivity?.showToast("Coming Soon")
            }
            R.id.editQuestionsTV -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.BundleParams.DATA, myPResponse)
                mActivity?.mainReplaceStackBundleTag(
                        EditQuestionsFragment.newInstance(),
                        bundle,
                        EditQuestionsFragment::javaClass.name
                )

                //  mActivity?.showToast("Coming Soon")
            }
            R.id.backIV -> {
                currentPage = viewPager.currentItem

                if (currentPage >= 0) {
                    nextIV.visibility = View.VISIBLE
                    viewPager.setCurrentItem(--currentPage, true)
                }
                if (currentPage == 0) {
                    backIV.visibility = View.GONE
                }
            }
            R.id.nextIV -> {
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
    }

    private var myPResponse: ProfileFetchResponse? = null

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        when (pResponse) {
            is ProfileFetchResponse -> {
                myPResponse = pResponse
//                sendDataToEditProfileFragment(pResponse)
                mBinding?.nameTV?.text =
                        (pResponse.result.firstName.capitalize() + " " + pResponse.result.lastName.capitalize())
//                CommonUtility.setGlideImage(mBinding!!.profileImageIV.context, pResponse.result.image, mBinding!!.profileImageIV)
                mBinding?.genderTV?.text = pResponse.result.gender
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
                mBinding?.genderLookingForTV?.text = str
                mBinding?.ageTV?.text = pResponse.result.age.toString()
                mBinding?.ageLookingForTV?.text =
                        (pResponse.result.lookingFor.minAge.toString() + "-" + pResponse.result.lookingFor.maxAge.toString())
                mBinding?.locationTV?.text = pResponse.result.location?.name
                mBinding?.statusTV?.text = pResponse.result.relationshipStatus
                mBinding?.heightTV?.text =
                        ("${pResponse.result.height.div(12)}\' ${pResponse.result.height.rem(12)}\"")
                mBinding?.heightLookingForTV?.text =
                        (("${pResponse.result.lookingFor.minHeight.div(12)}\' ${
                            pResponse.result.lookingFor.minHeight.rem(12)
                        }\"") + "- " + ("${pResponse.result.lookingFor.maxHeight.div(12)}\' ${
                            pResponse.result.lookingFor.maxHeight.rem(
                                    12
                            )
                        }\""))
//                mBinding?.ethnicityTV?.text = pResponse.result.ethinicity
//                mBinding?.ethnicityLookingForTV?.text = pResponse.result.lookingFor.ethinicity
                var ethinticityValue = (TextUtils.join(", ", pResponse.result.ethinicity))
//                for (item in pResponse.result.ethinicity){
//                    ethinticityValue=ethinticityValue+item+ ","
//                }
                mBinding?.ethnicityTV?.text = ethinticityValue
                ethinticityValue = (TextUtils.join(", ", pResponse.result.lookingFor.ethinicity))
//                for (item in pResponse.result.lookingFor.ethinicity){
//                    ethinticityValue=ethinticityValue+item+ ","
//                }
                mBinding?.ethnicityLookingForTV?.text = ethinticityValue
                mBinding?.beliefTV?.text = pResponse.result.belief

                val mBeliefListChoice = resources.getStringArray(R.array.religionLookingForList)
                val string = StringBuilder()
                for (i in 0 until pResponse.result.lookingFor.beliefId.size) {
                    string.append(mBeliefListChoice!![pResponse.result.lookingFor.beliefId[i] - 1])
                    if (i != pResponse.result.lookingFor.beliefId.size - 1)
                        string.append(", ")
                }
                mBinding?.beliefLookingForTV?.text = string.toString()

                mBinding?.questionCountTV?.text =
                        (getString(R.string.questions) + "-" + pResponse.result.answers.size)

                //--- clear list
                quesArrayList.clear()

                //--- add add questions to list
                quesArrayList.addAll(pResponse.result.answers)
                mAdapterMyProfileQs = MyProfileQsAdapter(quesArrayList)
                mBinding?.myProfileQsRV?.adapter = mAdapterMyProfileQs
//                mBinding?.quesIndicator?.attachToRecyclerView(mBinding!!.myProfileQsRV)
                arrayList.clear()
                arrayList.add(pResponse.result.image)
                if (pResponse.result.gallery != null && pResponse.result.gallery.isNotEmpty()) {
                    nextIV.visibility = View.VISIBLE
                    for (i in 0 until pResponse.result.gallery.size) {
                        arrayList.add(pResponse.result.gallery[i])
                    }
                }
                imageAdapter?.notifyDataSetChanged()
                NUM_PAGES = arrayList.size

            }

            is LogoutSuccessModel -> {
                JDAApplication.mInstance.setProfile(null)
                JDAApplication.mInstance.setLoginStatus(Constants.ScreenStatus.sLOGGEDIN)
                mActivity?.startActivity(Intent(mActivity, LoginActivity::class.java))
                mActivity?.showToast(resources.getString(R.string.logout_success))
                mActivity?.finish()
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ProfileFetchResponse::class.java -> {
                mBinding!!.shimmerFrameLayout.visibility = View.VISIBLE
                mBinding!!.actualDataCL.visibility = View.GONE
                mBinding!!.shimmerFrameLayout.startShimmerAnimation()
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ProfileFetchResponse::class.java -> {
                mBinding!!.actualDataCL.visibility = View.VISIBLE
                mBinding!!.shimmerFrameLayout.visibility = View.GONE
                mBinding!!.shimmerFrameLayout.stopShimmerAnimation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ProfileFragment")
        presenter!!.apiHitGetProfile()
    }

    private fun openAlertDialog(pTitle: String?, pMessage: String?) {
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle(pTitle)
        builder.setMessage(pMessage)
        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
            presenter?.apiLogout()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialogInterface, which ->
            mDialog?.dismiss()
        }
        mDialog = builder.create()
        mDialog?.setCancelable(false)
        mDialog?.show()
    }

    private fun initImageSlider() {
        arrayList.clear()
        imageAdapter?.notifyDataSetChanged()
        currentPage = 0
        NUM_PAGES = 0
        //Set the pager with an adapter
        imageAdapter = ImageSliderAdapter(requireActivity(), arrayList)
        viewPager.adapter = imageAdapter
    }
}