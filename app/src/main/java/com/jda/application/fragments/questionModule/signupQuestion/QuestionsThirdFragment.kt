package com.jda.application.fragments.questionModule.editQuestion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.adapter.QuestionsAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.base.service.URLs
import com.jda.application.databinding.FragmentQuestionsBinding
import com.jda.application.fragments.questionModule.*
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.UserAlertUtility

@ExperimentalStdlibApi
class QuestionsThirdFragment : BaseFragment(), OnItemClickListener,
        QuestionsAdapter.QuestionsItemListener {

    private var mBinding: FragmentQuestionsBinding? = null
    private var mPresenter: QuestionPresenter? = null
    private var mQuestionArrayList = ArrayList<Result>()
    private var mQuestionsAdapter: QuestionsAdapter? = null
    private var mNestedQuestionCount: Int = 0
    private var mAnswerArrayList: MutableList<SaveAnswersRequestModel.Answer> = ArrayList()

    companion object {
        val TAG: String = QuestionsThirdFragment::class.java.simpleName
        fun newInstance() = QuestionsThirdFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mBinding == null)
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_questions, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mQuestionArrayList.isEmpty())
            initialise()
    }

    private fun initialise() {
        mBinding?.clickHandler = this
        mBinding?.appBar?.clickHandle = this
        mQuestionArrayList = ArrayList()
        initAppBar()
        initTimelineView()
        setTimelineViewColor(3)
        mBinding?.nextQuestionsBT?.visibility = View.GONE
        mBinding?.nextQuestionsSecondBT?.visibility = View.GONE
        mBinding?.submitQuestionsBT?.visibility = View.VISIBLE
        mPresenter = QuestionPresenterImp(this)
        val param = HashMap<String, Any>()
        param[Constants.Params.sPAGE] = 3
        mPresenter?.apiGetQuestionList(param, isPaginationCall = false)
    }

    private fun initAppBar() {
        mBinding?.appBar?.tittleTv?.text = getString(R.string.questions)
        mBinding?.appBar?.backBt?.visibility = View.VISIBLE
    }

    private fun initTimelineView() {
        mBinding?.timeline1?.initLine(1)
        mBinding?.timeline2?.initLine(6)
        mBinding?.timeline3?.initLine(2)
    }

    private fun initQuestionsAdapter() {
        mQuestionsAdapter = QuestionsAdapter(mActivity!!, this, mQuestionArrayList)
        mBinding?.questionsRV?.adapter = mQuestionsAdapter
    }

    private fun initAdapter() {
        mBinding?.questionsRV?.setHasFixedSize(true)
        mBinding?.questionsRV?.layoutManager = LinearLayoutManager(mActivity)
//        mAdapter?.setExpandableParentItemList(mQuestionArrayList)
    }

    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.submitQuestionsBT -> {
                if (mAnswerArrayList.size == mQuestionsAdapter?.itemCount?.plus(mNestedQuestionCount)
                ) {
                    mPresenter!!.apiSaveAnswer(SaveAnswersRequestModel(3, mAnswerArrayList))
                } else {
                    mActivity?.showToast(getString(R.string.please_answer_all_questions))
                }
            }
            R.id.backBt -> {
                mActivity?.onBackPressed()
            }
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        when (pResponse) {

            is GetQuestionsSuccessModel -> {
                mQuestionArrayList.clear()


                for (j in 0 until pResponse.result.size) {
                    mQuestionArrayList.add(Result(pResponse.result[j]._id, pResponse.result[j].answers, pResponse.result[j].nestedQuestions, pResponse.result[j].page, pResponse.result[j].question, pResponse.result[j].type, ""))
                }


                initQuestionsAdapter()
//                initAdapter()
            }
            is SaveAnswerSuccessModel -> {
                JDAApplication.mInstance.setLoginStatus(Constants.ScreenStatus.sPROFILE_COMPLETED)
                val data = JDAApplication.mInstance.getProfile()
                data?.result?.arePreferencesSet = true
                data?.result?.areQuestionsSet = true
                JDAApplication.mInstance.setProfile(data)
                mActivity?.showToast(getString(R.string.questions_saved_successfully))
                startActivity(Intent(mActivity!!, DashboardActivity::class.java))
                mActivity!!.finish()
            }

        }
    }

    private fun setTimelineViewColor(type: Int) {
        when (type) {
            1 -> {
                mBinding!!.timeline2.setEndLineColor(ContextCompat.getColor(mActivity!!, R.color.lineColor), 5)
                mBinding!!.timeline2.setStartLineColor(ContextCompat.getColor(mActivity!!, R.color.lineColor), 5)
                mBinding!!.timeline2.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.two_grey)

                mBinding!!.timeline1.setEndLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.lineColor
                        ), 1
                )
                mBinding!!.timeline2.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.one_red)
                mBinding!!.timeline3.setStartLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.lineColor
                        ), 2
                )
                mBinding!!.timeline3.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.three_grey)
            }
            2 -> {
                mBinding!!.timeline2.setEndLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.lineColor
                        ), 5
                )
                mBinding!!.timeline2.setStartLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.colorRed
                        ), 5
                )
                mBinding!!.timeline2.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.two_red)
                mBinding!!.timeline1.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.tick)

                mBinding!!.timeline1.setEndLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.colorRed
                        ), 1
                )
                mBinding!!.timeline3.setStartLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.lineColor
                        ), 2
                )
                mBinding!!.timeline3.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.three_grey)
            }
            3 -> {
                mBinding!!.timeline2.setEndLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.colorRed
                        ), 5
                )
                mBinding!!.timeline2.setStartLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.colorRed
                        ), 5
                )
                mBinding!!.timeline1.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.tick)
                mBinding!!.timeline2.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.tick)

                mBinding!!.timeline1.setEndLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.colorRed
                        ), 1
                )

                mBinding!!.timeline3.setStartLineColor(
                        ContextCompat.getColor(
                                mActivity!!,
                                R.color.colorRed
                        ), 2
                )
                mBinding!!.timeline3.marker =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.three_red)
            }
        }
    }

    override fun onDataSelectedFromItem(
            pView: View?,
            pAnswerModel: Answer?,
            pText: String?,
            pResultModel: Result?,
            pNestedQuestionCount: Int
    ) {
        mNestedQuestionCount = pNestedQuestionCount
        when (pView?.id) {
            R.id.firstSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.secondSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.thirdSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.fourthSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.answerET -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sTEXT)
            }
        }
    }

    override fun onDataSelectedFromItemNested(pView: View?, pAnswerModel: Answer?, pText: String?, pResultModel: Question?) {
        when (pView?.id) {
            R.id.firstSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.secondSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.thirdSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.fourthSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.answerET -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sTEXT)
            }
        }
    }

    override fun updateArrayList(nestedArrayList: ArrayList<Answer>) {

    }

    override fun onDontKnowClick() {
        val uri: Uri = Uri.parse(URLs.APIs.sMBTI_TYPE_LINK)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun removeAnsFromList(pResultModel: Result?) {
        for (i in mAnswerArrayList.indices) {
            if (mAnswerArrayList[i].questionId == pResultModel?._id && mAnswerArrayList[i].text!!.isNotEmpty()) {
                mAnswerArrayList.removeAt(i)
                break
            }
        }
    }

    private fun handleData(pText: String?, pAnswerModel: Answer?, pModel: Result?, pChoice: Int) {
        when (pChoice) {
            Constants.QuestionType.sCHOICE -> {
                if (pAnswerModel != null && pModel != null) {
                    addDataToList(pText, pAnswerModel, pModel, pChoice)
                }
            }
            Constants.QuestionType.sTEXT -> {
                if (!pText.isNullOrEmpty() && pModel != null) {
                    addDataToList(pText, pAnswerModel, pModel, pChoice)
                }
            }
        }
    }

    private fun handleData(pText: String?, pAnswerModel: Answer?, pModel: Question?, pChoice: Int) {
        when (pChoice) {
            Constants.QuestionType.sCHOICE -> {
                if (pAnswerModel != null && pModel != null) {
                    addDataToList(pText, pAnswerModel, pModel, pChoice)
                }
            }
            Constants.QuestionType.sTEXT -> {
                if (!pText.isNullOrEmpty() && pModel != null) {
                    addDataToList(pText, pAnswerModel, pModel, pChoice)
                }
            }
        }
    }

    private fun addDataToList(pText: String?, pAnswerModel: Answer?, pModel: Result, pChoice: Int) {
        if (mAnswerArrayList.size == 0) {
            mAnswerArrayList.add(
                    SaveAnswersRequestModel.Answer(
                            pAnswerModel?.value,
                            pModel._id,
                            pText!!,
                            pChoice,
                    )
            )

        } else {
            var ansExist = false
            for (i in mAnswerArrayList.indices) {
                if (mAnswerArrayList[i].questionId == pModel._id) {
                    when (pChoice) {
                        Constants.QuestionType.sCHOICE -> {
                            if (mAnswerArrayList[i].choice == pAnswerModel?.value) {
                                ansExist = true
                                break
                            } else {
                                mAnswerArrayList[i].choice = pAnswerModel?.value
                                ansExist = true
                                break
                            }
                        }
                        Constants.QuestionType.sTEXT -> {
                            mAnswerArrayList[i].text = pText
                            ansExist = true
                            break
                        }
                    }
                }
            }
            if (!ansExist) {
                mAnswerArrayList.add(
                        SaveAnswersRequestModel.Answer(
                                pAnswerModel?.value,
                                pModel._id,
                                pText!!,
                                pChoice
                        )
                )
            }
        }
    }

    private fun addDataToList(pText: String?, pAnswerModel: Answer?, pModel: Question, pChoice: Int) {
        if (mAnswerArrayList.size == 0) {
            mAnswerArrayList.add(
                    SaveAnswersRequestModel.Answer(
                            pAnswerModel?.value,
                            pModel._id,
                            pText!!,
                            pChoice,
                    )
            )

        } else {
            var ansExist = false
            for (i in mAnswerArrayList.indices) {
                if (mAnswerArrayList[i].questionId == pModel._id) {
                    when (pChoice) {
                        Constants.QuestionType.sCHOICE -> {
                            if (mAnswerArrayList[i].choice == pAnswerModel?.value) {
                                ansExist = true
                                break
                            } else {
                                mAnswerArrayList[i].choice = pAnswerModel?.value
                                ansExist = true
                                break
                            }
                        }
                        Constants.QuestionType.sTEXT -> {
                            mAnswerArrayList[i].text = pText
                            ansExist = true
                            break
                        }
                    }
                }
            }
            if (!ansExist) {
                mAnswerArrayList.add(
                        SaveAnswersRequestModel.Answer(
                                pAnswerModel?.value,
                                pModel._id,
                                pText!!,
                                pChoice
                        )
                )
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            GetQuestionsSuccessModel::class.java, SaveAnswerSuccessModel::class.java -> {
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, mActivity, false)
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            GetQuestionsSuccessModel::class.java, SaveAnswerSuccessModel::class.java -> {
                UserAlertUtility.hideProgressDialog()
            }
        }
    }

    override fun onDataSelectedFromItem(
            pView: View?,
            pAnswerModel: Answer?,
            pText: String?,
            pResultModel: Result?) {
        Log.e("mNestedQuestionCount", mNestedQuestionCount.toString())
        when (pView?.id) {
            R.id.firstSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.secondSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.thirdSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.fourthSpinner -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sCHOICE)
            }
            R.id.answerET -> {
                handleData(pText, pAnswerModel, pResultModel, Constants.QuestionType.sTEXT)
            }
        }
    }

}