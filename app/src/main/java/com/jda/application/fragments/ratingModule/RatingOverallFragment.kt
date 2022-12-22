package com.jda.application.fragments.ratingModule

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.jda.application.R
import com.jda.application.adapter.ReviewListAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentRatingOverallBinding
import com.jda.application.utils.Constants
import java.text.DecimalFormat


class RatingOverallFragment : BaseFragment() {

    private var adapter: ReviewListAdapter? = null
    private var presenter: RatingPresenter? = null
    private var mUserID = ""
    private var mBinding: FragmentRatingOverallBinding? = null
    private var pageCount = 1
    private var isSingleHit = false
    private var mIsChatReview = true
    private var mChatReviewList: ArrayList<RatingRequestNew.Result.Review> = ArrayList()
    private var mDateReviewList: ArrayList<RatingRequestNew.Result.Review> = ArrayList()
    private var mReviewList: ArrayList<RatingRequestNew.Result.Review> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUserID = it.getString(Constants.BundleParams.DATA) ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rating_overall, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        mBinding!!.reviewRV.isNestedScrollingEnabled = false
        presenter = RatingPresenterImp(this)
        isSingleHit = false
        pageCount = 1
        val param = HashMap<String, Any>()
        param["limit"] = 20
        param["page"] = 1
        param["userId"] = mUserID
        presenter!!.apiHitRating(param)


        mBinding!!.scrollable.viewTreeObserver.addOnScrollChangedListener(OnScrollChangedListener {
            val view = mBinding?.scrollable?.getChildAt(mBinding!!.scrollable.childCount - 1) as View
            val diff: Int = view.bottom - (mBinding!!.scrollable.height + mBinding!!.scrollable.scrollY)
            if (diff == 0) {
                // your pagination code
                if (!isSingleHit) {
                    isSingleHit = true
                    val param = HashMap<String, Any>()
                    param["limit"] = 20
                    param["page"] = pageCount
                    param["userId"] = mUserID
                    presenter!!.apiHitRating(param)
                }

            }
        })
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        when (pResponse) {
            is RatingRequestNew -> {
                pResponse.result?.let {
                    pageCount++
                    val pageTotal = pResponse.result.pageCount
                    isSingleHit = pageCount > pageTotal
                }
                pResponse.result?.ratings?.let { data ->
//                    mBinding?.overallRatingTV?.text = String.format("%.1f", data.overallRating?.value)
                    mBinding?.overallRatingTV?.text = DecimalFormat("0.0").format(data.overallRating?.value)
//                    mBinding!!.ratingTV.setText("Based on ${data.overallRating?.count} Review")
                    mBinding?.ratingTV?.text = resources.getQuantityString(
                            R.plurals.rating_review_plural,
                            data.overallRating?.count ?: 0,
                            data.overallRating?.count ?: 0 //var arg
                    )
                    mBinding?.behRB?.rating = data.behavior?.value ?: 0.0f
                    mBinding?.goodRV?.rating = data.goodListener?.value ?: 0.0f
                    mBinding?.responseRV?.rating = data.responseTime?.value ?: 0.0f
                    mBinding?.commRV?.rating = data.communication?.value ?: 0.0f
                    mBinding?.punRV?.rating = data.punctuality?.value ?: 0.0f
                    mBinding?.picsRV?.rating = data.punctuality?.value ?: 0.0f
                    mBinding?.expRV?.rating = data.punctuality?.value ?: 0.0f

                    data.overallRating?.value?.let {
                        val formattedValue = String.format("%.1f", it).toFloat()
                        val formatedAfterValue = formattedValue.toString().substring(formattedValue.toString().indexOf("."))
                        when (it) {
                            in 0.0..1.0 -> {
                                mBinding?.terribleRatingBar?.rating = it.toFloat()
                            }
                            in 1.1..2.0 -> {
                                mBinding?.terribleRatingBar?.rating = 1f
                                mBinding?.poorRatingBar?.rating = if (2f.minus(formattedValue) == 0f) 1f else formatedAfterValue.toFloat()
                            }
                            in 2.1..3.0 -> {
                                mBinding?.terribleRatingBar?.rating = 1f
                                mBinding?.poorRatingBar?.rating = 1f
                                mBinding?.averageRatingBar?.rating = if (3f.minus(formattedValue) == 0f) 1f else formatedAfterValue.toFloat()
                            }
                            in 3.1..4.0 -> {
                                mBinding?.terribleRatingBar?.rating = 1f
                                mBinding?.poorRatingBar?.rating = 1f
                                mBinding?.averageRatingBar?.rating = 1f
                                mBinding?.veryGoodRatingBar?.rating = if (4f.minus(formattedValue) == 0f) 1f else formatedAfterValue.toFloat()
                            }
                            in 4.1..5.0 -> {
                                mBinding?.terribleRatingBar?.rating = 1f
                                mBinding?.poorRatingBar?.rating = 1f
                                mBinding?.averageRatingBar?.rating = 1f
                                mBinding?.veryGoodRatingBar?.rating = 1f
                                mBinding?.excellentRatingBar?.rating = if (5f.minus(formattedValue) == 0f) 1f else formatedAfterValue.toFloat()
                            }
                        }
                    }

                    /*   when (data.overallRating?.value) {
                           1f -> {
                               setRedLight(mBinding?.first2TV)
                               setHeightLight(mBinding?.second2TV)
                               setHeightLight(mBinding?.third2TV)
                               setHeightLight(mBinding?.fourth2TV)
                               setHeightLight(mBinding?.fivth2TV)
                           }
                           2f -> {
                               setRedLight(mBinding?.first2TV)
                               setRedLight(mBinding?.second2TV)
                               setHeightLight(mBinding?.third2TV)
                               setHeightLight(mBinding?.fourth2TV)
                               setHeightLight(mBinding?.fivth2TV)
                           }
                           3f -> {
                               setRedLight(mBinding?.first2TV)
                               setRedLight(mBinding?.second2TV)
                               setRedLight(mBinding?.third2TV)
                               setHeightLight(mBinding?.fourth2TV)
                               setHeightLight(mBinding?.fivth2TV)
                           }
                           4f -> {
                               setRedLight(mBinding?.first2TV)
                               setRedLight(mBinding?.second2TV)
                               setRedLight(mBinding?.third2TV)
                               setRedLight(mBinding?.fourth2TV)
                               setHeightLight(mBinding?.fivth2TV)
                           }
                           5f -> {
                               setRedLight(mBinding?.first2TV)
                               setRedLight(mBinding?.second2TV)
                               setRedLight(mBinding?.third2TV)
                               setRedLight(mBinding?.fourth2TV)
                               setRedLight(mBinding?.fivth2TV)
                           }
                       }*/

                }

                pResponse.result?.reviews?.let {
                    mChatReviewList.clear()
                    mDateReviewList.clear()
                    mReviewList.clear()
                    it.forEachIndexed { _, review ->
                        when (review?.type) {
                            Constants.ReviewType.CHAT.ordinal -> {
                                mChatReviewList.add(review)
                            }
                            Constants.ReviewType.DATE.ordinal -> {
                                mDateReviewList.add(review)
                            }
                        }
                    }
                    mReviewList.addAll(mChatReviewList)
                    adapter = ReviewListAdapter(mReviewList)
                    mBinding?.reviewRV?.adapter = adapter
                    setRecyclerViewVisibility()
                }
            }
        }
    }

    private fun setHeightLight(view: TextView?) {
//        val top = resources.getDrawable(R.drawable.large_star_grey)
        val top = ContextCompat.getDrawable(requireActivity(), R.drawable.large_star_grey)
        view?.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
    }

    private fun setRedLight(view: TextView?) {
//        val top = resources.getDrawable(R.drawable.large_star_red)
        val top = ContextCompat.getDrawable(requireActivity(), R.drawable.large_star_red)
        view?.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
    }


    private fun setClickListener() {
        mBinding?.chatReviewTv?.background = ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_red_background)
        mBinding?.chatReviewTv?.setOnClickListener {
            if (!mIsChatReview) {
                mBinding?.chatReviewTv?.setTypeface(null, Typeface.BOLD)
                mBinding?.dateReviewTv?.setTypeface(null, Typeface.NORMAL)
                mBinding?.chatReviewTv?.background = ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_red_background)
                mBinding?.dateReviewTv?.background = null
                mIsChatReview = true
                mReviewList.clear()
                mReviewList.addAll(mChatReviewList)
                adapter?.notifyDataSetChanged()
                setRecyclerViewVisibility()
            }
        }

        mBinding?.dateReviewTv?.setOnClickListener {
            if (mIsChatReview) {
                mBinding?.chatReviewTv?.setTypeface(null, Typeface.NORMAL)
                mBinding?.dateReviewTv?.setTypeface(null, Typeface.BOLD)
                mBinding?.dateReviewTv?.background = ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_red_background)
                mBinding?.chatReviewTv?.background = null
                mIsChatReview = false
                mReviewList.clear()
                mReviewList.addAll(mDateReviewList)
                adapter?.notifyDataSetChanged()
                setRecyclerViewVisibility()
            }
        }
    }

    private fun setRecyclerViewVisibility() {
        if (mReviewList.isNullOrEmpty()) {
            mBinding?.reviewRV?.visibility = View.GONE
            mBinding?.emptyTV?.visibility = View.VISIBLE
        } else {
            mBinding?.reviewRV?.visibility = View.VISIBLE
            mBinding?.emptyTV?.visibility = View.GONE
        }
    }
}