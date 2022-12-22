package com.jda.application.fragments.editQuestionModule

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
import com.jda.application.adapter.EditQuestionsAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.base.service.URLs
import com.jda.application.databinding.FragmentQuestionsBinding
import com.jda.application.fragments.edit_profile.AnswerResponse
import com.jda.application.fragments.questionModule.*
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.UserAlertUtility

@ExperimentalStdlibApi
class EditQuestionsThirdFragment : BaseFragment(), OnItemClickListener,
        EditQuestionsAdapter.QuestionsItemListener {

    private var mBinding: FragmentQuestionsBinding? = null
    private var mPresenter: QuestionPresenter? = null
    private var mQuestionArrayList = ArrayList<Result>()
    private var mQuestionsAdapter: EditQuestionsAdapter? = null
    private var mNestedQuestionCount: Int = 0
    private var mAnswerArrayList: MutableList<SaveAnswersRequestModel.Answer> = ArrayList()
    private var myPResponse: AnswerResponse? = null

    companion object {
        val TAG: String = EditQuestionsThirdFragment::class.java.simpleName
        fun newInstance() = EditQuestionsThirdFragment()
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
        mPresenter?.apiGetAnswer()
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
        mQuestionsAdapter = EditQuestionsAdapter(mActivity!!, this, mQuestionArrayList)
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

            is AnswerResponse -> {
                myPResponse = pResponse
                val param = HashMap<String, Any>()
                param[Constants.Params.sPAGE] = 3
                mPresenter?.apiGetQuestionList(param, isPaginationCall = false)
            }
            is GetQuestionsSuccessModel -> {
                mQuestionArrayList.clear()

                for (i in 0 until myPResponse?.result!!.size) {
                    for (j in 0 until pResponse.result.size) {
                        if (myPResponse?.result!![i]?.question == pResponse.result[j]._id) {
                            mAnswerArrayList?.add(SaveAnswersRequestModel.Answer(myPResponse?.result!![i]?.choice, myPResponse?.result!![i]?.question!!, myPResponse?.result!![i]?.text, myPResponse?.result!![i]?.type
                                    ?: 0))
                            mQuestionArrayList.add(Result(pResponse.result[j]._id, pResponse.result[j].answers, pResponse.result[j].nestedQuestions, pResponse.result[j].page, pResponse.result[j].question, pResponse.result[j].type, myPResponse?.result!![i]?.choice
                                    ?: myPResponse?.result!![i]?.text ?: ""))
                            break
                        }
                    }
                }

                if (myPResponse?.result?.size ?: 0 <= 17) {
                    mQuestionArrayList.clear()
                    mQuestionArrayList.addAll(pResponse.result)
                }

                initQuestionsAdapter()
//                initAdapter()
            }
            is SaveAnswerSuccessModel -> {
                mActivity?.showToast(getString(R.string.questions_updated_successfully))
//                mActivity?.supportFragmentManager?.popBackStack(EditQuestionsFragment::javaClass.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                mActivity?.finish()
                // mActivity?.mainReplaceStack(QuestionsThirdFragment.newInstance())
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
                            mAnswerArrayList[i].text = ""
                            mAnswerArrayList[i].type = pChoice
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