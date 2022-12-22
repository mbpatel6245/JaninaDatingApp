package com.jda.application.fragments.questionModule.signupQuestion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.jda.application.R
import com.jda.application.acivities.LoginActivity
import com.jda.application.adapter.QuestionsAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.base.service.URLs
import com.jda.application.databinding.FragmentQuestionsBinding
import com.jda.application.fragments.questionModule.*
import com.jda.application.fragments.questionModule.editQuestion.QuestionsSecondFragment
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.UserAlertUtility
import com.jda.application.utils.loginReplaceStack


@ExperimentalStdlibApi
class QuestionsFragment : BaseFragment(), OnItemClickListener,
        QuestionsAdapter.QuestionsItemListener {

    private var mBinding: FragmentQuestionsBinding? = null
    private var mPresenter: QuestionPresenter? = null
    private var mQuestionArrayList = ArrayList<Result>()
    private var mAnswerArrayList: MutableList<SaveAnswersRequestModel.Answer> = ArrayList()
    private var mQuestionsAdapter: QuestionsAdapter? = null
    private var mNestedQuestionCount: Int = 0

    companion object {
        val TAG: String = QuestionsFragment::class.java.simpleName
        @JvmStatic
        fun newInstance() = QuestionsFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (mBinding == null)
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_questions, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
    }

    private fun initialise() {
        if (mQuestionArrayList.isEmpty()) {
            mBinding?.clickHandler = this
            mBinding?.appBar?.clickHandle = this
            mQuestionArrayList = ArrayList()
            mAnswerArrayList = ArrayList()
            initAppBar()
            initTimelineView()
            mPresenter = QuestionPresenterImp(this)
            val param = HashMap<String, Any>()
            param[Constants.Params.sPAGE] = 1
            mPresenter?.apiGetQuestionList(param, isPaginationCall = false)
        }
    }

    private fun initAppBar() {
        mBinding?.appBar?.tittleTv?.text = getString(R.string.questions)
        mBinding?.appBar?.backBt?.visibility = View.GONE
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

    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.nextQuestionsBT -> {
                if (mAnswerArrayList.size == mQuestionsAdapter?.itemCount?.plus(mNestedQuestionCount)) {
                    mPresenter!!.apiSaveAnswer(SaveAnswersRequestModel(1, mAnswerArrayList))
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
                mQuestionArrayList.addAll(pResponse.result)
                initQuestionsAdapter()
//                initAdapter()
            }
            is SaveAnswerSuccessModel -> {
                if ((mActivity as LoginActivity).mQuestionSecondFragment == null)
                    (mActivity as LoginActivity).saveQuestionSecondFragment(QuestionsSecondFragment.newInstance())
                (mActivity as LoginActivity).mQuestionSecondFragment?.let {
                    mActivity?.loginReplaceStack(
                            it
                    )
                }
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
                                pAnswerModel?._id,
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