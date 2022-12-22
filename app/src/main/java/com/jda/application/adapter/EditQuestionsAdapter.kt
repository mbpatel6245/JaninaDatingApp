package com.jda.application.adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.databinding.QuestionsItemLayoutBinding
import com.jda.application.fragments.editQuestionModule.EditQuestionsSecondFragment
import com.jda.application.fragments.edit_profile.AnswerResponse
import com.jda.application.fragments.edit_profile.EditProfileActivity
import com.jda.application.fragments.questionModule.*
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants
import com.jda.application.utils.CustomAnswerDialog
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.android.synthetic.main.questions_item_layout.view.*


@ExperimentalStdlibApi
class EditQuestionsAdapter(
        var pContext: Activity,
        var mListener: QuestionsItemListener,
        var pQuesList: ArrayList<Result>?,
        var secondFragment: EditQuestionsSecondFragment? = null,
        var answerList: AnswerResponse? = null
) :
        RecyclerView.Adapter<EditQuestionsAdapter.MyViewHolder>() {

    private var mActiveSpinner: PowerSpinnerView? = null
    private var mSpinnerAdapter: CustomSpinnerAdapter? = null
    private var mIsKeyboardShowing: Boolean = false
    private var mRootView: View? = null
    private var parentAnswerItemIndex: Int = 0
    var mQuestionDataList: MutableList<QuestionDataModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = DataBindingUtil.inflate<QuestionsItemLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.questions_item_layout,
                parent,
                false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        binding.questionTV.text = pQuesList!![position].question
        binding.firstSpinner.setItems(pQuesList!![position].answers)
        binding.firstNestedSpinner.setItems(pQuesList!![position].answers)
        mQuestionDataList.add(QuestionDataModel(pQuesList!![position]._id, position.toString()))
        when (pQuesList!![position].type) {
            Constants.QuestionType.sCHOICE -> {
                binding.firstSpinner.visibility = View.VISIBLE
                binding.firstNestedSpinner.visibility = View.GONE
                binding.answerET.visibility = View.GONE
                binding.firstSpinner.text = pQuesList!![position].choice
            }
            Constants.QuestionType.sTEXT -> {
                binding.firstSpinner.visibility = View.INVISIBLE
                binding.firstNestedSpinner.visibility = View.INVISIBLE
                binding.answerET.visibility = View.VISIBLE
                binding.answerET.setText(pQuesList!![position].choice)
            }
            Constants.QuestionType.sNESTED_QUESTIONS -> {
                binding.firstSpinner.visibility = View.INVISIBLE
                binding.firstNestedSpinner.visibility = View.VISIBLE
                binding.answerET.visibility = View.GONE
                binding.firstNestedSpinner.text = pQuesList!![position].choice
            }
        }
        if (pQuesList!![position].page == 3 && position == 1) {
            binding.dontKnowTV.visibility = View.VISIBLE
        } else
            binding.dontKnowTV.visibility = View.GONE

        if (pQuesList!![position].question.contains("Do you have") && pQuesList!![position].question.contains("children?")) {
            if (pQuesList!![position].choice == "No") {
                handleSelectionForChildren(
                        binding,
                        position,
                        binding.firstNestedSpinner,
                        pQuesList!![position].answers[1],
                        1
                )
            } else if (pQuesList!![position].choice == "Yes") {
                handleSelectionForChildren(
                        binding,
                        position,
                        binding.firstNestedSpinner,
                        pQuesList!![position].answers[0],
                        0
                )
            }

        }
    }

    override fun getItemCount(): Int {
        return pQuesList!!.size
    }

    @ExperimentalStdlibApi
    inner class MyViewHolder(var binding: QuestionsItemLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {
        init {
            initialise()

            binding.firstSpinner.setOnClickListener {
                CommonUtility.closeKeyBoard(pContext)
                handleSpinnerShowHide(binding.firstSpinner, binding, absoluteAdapterPosition)
            }

            binding.firstNestedSpinner.setOnClickListener {
                CommonUtility.closeKeyBoard(pContext)
                handleSpinnerShowHide(binding.firstNestedSpinner, binding, absoluteAdapterPosition)
            }

            binding.firstNestedSpinner.setOnSpinnerItemSelectedListener<Answer> { oldIndex, oldItem, newIndex, newItem ->
                var isNested = false
                pQuesList?.forEachIndexed { i: Int, result: Result ->
                    isNested = result._id != newItem._id
                }

                if (isNested) {
                    mListener.updateArrayList(nestedQuestionList)
                    handleNestedQuestionsWithCustomSpinner(
                            binding.root,
                            bindingAdapterPosition,
                            newIndex
                    )
                    mListener.onDataSelectedFromItem(
                            binding.firstNestedSpinner,
                            newItem,
                            "",
                            pQuesList!![bindingAdapterPosition],
                            if (pQuesList!![bindingAdapterPosition].nestedQuestions?.size ?: 0 > 0) {
                                pQuesList!![bindingAdapterPosition].nestedQuestions?.let {
                                    it[if (newIndex == 0) 1 else 0].questions?.size
                                }
                                        ?: 0
                            } else {
                                0
                            }
                    )
                } else {

                    pQuesList?.forEachIndexed { index, result ->
                        result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                            Log.e("nestedQuestions", "${nestedQuestion._id} ${newItem._id}")
                            nestedQuestion.questions?.forEachIndexed { ii: Int, question: Question ->
                                question.answers.forEachIndexed { index8, answerX ->
                                    if (answerX._id == newItem._id) {
                                        mListener.onDataSelectedFromItemNested(
                                                binding.firstNestedSpinner,
                                                newItem,
                                                "",
                                                pQuesList!![index].nestedQuestions?.get(i)?.questions?.get(ii)
                                        )
                                    }
                                }

                            }
                        }
                    }

                }
            }

            binding.firstSpinner.setOnSpinnerItemSelectedListener<Answer> { oldIndex, oldItem, newIndex, newItem ->
                /* var isNested = false
                 pQuesList?.forEachIndexed { i: Int, result: Result ->
                     isNested = result._id != newItem._id
                 }

                 if (isNested) {

                     handleNestedQuestionsWithCustomSpinner(
                             binding.root,
                             bindingAdapterPosition,
                             newIndex
                     )*/
                mListener.onDataSelectedFromItem(
                        binding.firstSpinner,
                        newItem,
                        "",
                        pQuesList!![bindingAdapterPosition]
                )
                /* } else {

                     pQuesList?.forEachIndexed { index, result ->
                         result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                             Log.e("nestedQuestions", "${nestedQuestion._id} ${newItem._id}")
                             nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                                 question?.answers.forEachIndexed { index8, answerX ->
                                     if (answerX._id == newItem._id) {
                                         mListener.onDataSelectedFromItemNested(
                                                 binding.firstSpinner,
                                                 newItem,
                                                 "",
                                                 pQuesList!![index].nestedQuestions?.get(i)?.questions?.get(ii)
                                         )
                                     }
                                 }

                             }
                         }
                     }

                 }*/
            }

            binding.secondSpinner.setOnSpinnerItemSelectedListener<Answer> { oldIndex, oldItem, newIndex, newItem ->

                pQuesList?.forEachIndexed { index, result ->
                    result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                        Log.e("nestedQuestions", "${nestedQuestion._id} ${newItem._id}")
                        nestedQuestion.questions?.forEachIndexed { ii: Int, question: Question ->
                            question.answers.forEachIndexed { index8, answerX ->
                                if (answerX._id == newItem._id) {
                                    mListener.onDataSelectedFromItemNested(
                                            binding.secondSpinner,
                                            newItem,
                                            "",
                                            pQuesList!![index].nestedQuestions?.get(i)?.questions?.get(ii)
                                    )
                                }
                            }

                        }
                    }
                }
            }

            binding.thirdSpinner.setOnSpinnerItemSelectedListener<Answer> { oldIndex, oldItem, newIndex, newItem ->

                pQuesList?.forEachIndexed { index, result ->
                    result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                        Log.e("nestedQuestions", "${nestedQuestion._id} ${newItem._id}")
                        nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                            question.answers.forEachIndexed { index8, answerX ->
                                if (answerX._id == newItem._id) {
                                    mListener.onDataSelectedFromItemNested(
                                            binding.thirdSpinner,
                                            newItem,
                                            "",
                                            pQuesList!![index].nestedQuestions?.get(i)?.questions?.get(ii)
                                    )
                                }
                            }

                        }
                    }
                }

            }

            binding.fourthSpinner.setOnSpinnerItemSelectedListener<Answer> { oldIndex, oldItem, newIndex, newItem ->

                pQuesList?.forEachIndexed { index, result ->
                    result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                        Log.e("nestedQuestions", "${nestedQuestion._id} ${newItem._id}")
                        nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                            question.answers.forEachIndexed { index8, answerX ->
                                if (answerX._id == newItem._id) {
                                    mListener.onDataSelectedFromItemNested(
                                            binding.fourthSpinner,
                                            newItem,
                                            "",
                                            pQuesList!![index].nestedQuestions?.get(i)?.questions?.get(ii)
                                    )
                                }
                            }

                        }
                    }
                }

            }

            binding.secondSpinner.setOnClickListener {
                CommonUtility.closeKeyBoard(pContext)
                showNestedSpinnerDialog(binding.secondSpinner, absoluteAdapterPosition)
            }

            binding.thirdSpinner.setOnClickListener {
                CommonUtility.closeKeyBoard(pContext)
                showNestedSpinnerDialog(binding.thirdSpinner, absoluteAdapterPosition)
            }

            binding.fourthSpinner.setOnClickListener {
                showNestedSpinnerDialog(binding.fourthSpinner, absoluteAdapterPosition)
                CommonUtility.closeKeyBoard(pContext)
            }

            binding.firstSpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                binding.firstSpinner.dismiss()
            }

            binding.firstNestedSpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                binding.firstNestedSpinner.dismiss()
            }

            binding.secondSpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                binding.secondSpinner.dismiss()
            }

            binding.thirdSpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                binding.thirdSpinner.dismiss()
            }

            binding.fourthSpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                binding.fourthSpinner.dismiss()
            }

            binding.answerET.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (binding.answerET.text.toString().trim().isNotEmpty()) {
                        mListener.onDataSelectedFromItem(
                                binding.answerET,
                                null,
                                binding.answerET.text.toString().trim(),
                                pQuesList!![bindingAdapterPosition]
                        )
                    } else {
                        mListener.removeAnsFromList(pQuesList!![bindingAdapterPosition])
                    }
                }

            })

            binding.dontKnowTV.setOnClickListener {
                mListener.onDontKnowClick()
            }
        }

        private fun initialise() {
            mActiveSpinner = binding.firstSpinner
            mSpinnerAdapter = CustomSpinnerAdapter(binding.firstSpinner)
            binding.firstSpinner.setSpinnerAdapter(mSpinnerAdapter!!)
            mSpinnerAdapter = CustomSpinnerAdapter(binding.firstNestedSpinner)
            binding.firstNestedSpinner.setSpinnerAdapter(mSpinnerAdapter!!)
            binding.secondSpinner.lifecycleOwner = if (pContext is DashboardActivity) pContext as DashboardActivity else pContext as EditProfileActivity
            val nestedSecondSpinnerAdapter = CustomSpinnerAdapter(binding.secondSpinner)
            binding.secondSpinner.setSpinnerAdapter(nestedSecondSpinnerAdapter)
            binding.secondSpinner.lifecycleOwner = if (pContext is DashboardActivity) pContext as DashboardActivity else pContext as EditProfileActivity
            val nestedThirdSpinnerAdapter = CustomSpinnerAdapter(binding.thirdSpinner)
            binding.thirdSpinner.setSpinnerAdapter(nestedThirdSpinnerAdapter)
            binding.thirdSpinner.lifecycleOwner = if (pContext is DashboardActivity) pContext as DashboardActivity else pContext as EditProfileActivity
            val nestedFourthSpinnerAdapter = CustomSpinnerAdapter(binding.fourthSpinner)
            binding.fourthSpinner.setSpinnerAdapter(nestedFourthSpinnerAdapter)
            binding.fourthSpinner.lifecycleOwner = if (pContext is DashboardActivity) pContext as DashboardActivity else pContext as EditProfileActivity
        }
    }

//    private fun handleSpinnerShowHide(pSpinnerView: PowerSpinnerView?) {
//        if (mActiveSpinner?.isShowing!! && mActiveSpinner != pSpinnerView) {
//            mActiveSpinner?.dismiss()
//        }
//        pSpinnerView?.showOrDismiss()
//        mActiveSpinner = pSpinnerView
//    }


    private fun handleSpinnerShowHide(
            pSpinnerView: PowerSpinnerView?,
            binding: QuestionsItemLayoutBinding,
            position: Int
    ) {
        mActiveSpinner = pSpinnerView
        val stringArray = pQuesList?.get(position)?.answers ?: ArrayList()
        val choiceList = ArrayList<Answer>()
        for (element in stringArray) {
            choiceList.add(element)
        }

        val dialog = CustomAnswerDialog()
        dialog.createDialog(
                pContext,
                pQuesList?.get(position)?.question,
                choiceList,
                object : CustomAnswerDialog.DialogCallback {
                    override fun onItemClick(answer: Answer?, pItemPosition: Int) {
                        if (answer != null) {
                            pSpinnerView?.text = answer.value
                            pQuesList?.get(position)?.choice = answer.value!!
                            when (mActiveSpinner?.id) {
                                R.id.firstSpinner -> {
                                    mListener.onDataSelectedFromItem(
                                            pSpinnerView,
                                            answer,
                                            "",
                                            pQuesList?.get(position)
                                    )
                                }

                                R.id.firstNestedSpinner -> {
                                    var isNested = false
                                    pQuesList?.forEachIndexed { i: Int, result: Result ->
                                        isNested = result._id != answer._id
                                    }

                                    if (isNested) {
                                        mListener.updateArrayList(nestedQuestionList)
                                        handleNestedQuestionsWithCustomSpinner(
                                                binding.root,
                                                position,
                                                pItemPosition
                                        )
                                        mListener.onDataSelectedFromItem(
                                                pSpinnerView,
                                                answer,
                                                "",
                                                pQuesList!![position],
                                                if (pQuesList!![position].nestedQuestions?.size ?: 0 > 0) {
                                                    pQuesList!![position].nestedQuestions?.let {
                                                        it[if (pItemPosition == 0) 1 else 0].questions?.size
                                                    }
                                                            ?: 0
                                                } else {
                                                    0
                                                }
                                        )
                                    } else {

                                        pQuesList?.forEachIndexed { index, result ->
                                            result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                                                Log.e(
                                                        "nestedQuestions",
                                                        "${nestedQuestion._id} ${answer._id}"
                                                )
                                                nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                                                    question.answers.forEachIndexed { index8, answerX ->
                                                        if (answerX._id == answer._id) {
                                                            mListener.onDataSelectedFromItemNested(
                                                                    pSpinnerView,
                                                                    answer,
                                                                    "",
                                                                    pQuesList!![index].nestedQuestions?.get(
                                                                            i
                                                                    )?.questions?.get(
                                                                            ii
                                                                    )
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }, pQuesList?.get(position)?.choice
        )
    }

    fun handleSelectionForChildren(
            binding: QuestionsItemLayoutBinding,
            position: Int,
            pSpinnerView: PowerSpinnerView,
            answer: Answer?,
            pItemPosition: Int) {
        if (answer != null) {
            when (pSpinnerView.id) {
                R.id.firstSpinner -> {
                    mListener.onDataSelectedFromItem(
                            pSpinnerView,
                            answer,
                            "",
                            pQuesList?.get(position)
                    )
                }

                R.id.firstNestedSpinner -> {
                    var isNested = false
                    pQuesList?.forEachIndexed { i: Int, result: Result ->
                        isNested = result._id != answer._id
                    }

                    if (isNested) {
                        mListener.updateArrayList(nestedQuestionList)
                        handleNestedQuestionsWithCustomSpinner(
                                binding.root,
                                position,
                                pItemPosition
                        )
                        mListener.onDataSelectedFromItem(
                                pSpinnerView,
                                answer,
                                "",
                                pQuesList!![position],
                                if (pQuesList!![position].nestedQuestions?.size ?: 0 > 0) {
                                    pQuesList!![position].nestedQuestions?.let {
                                        it[if (pItemPosition == 0) 1 else 0].questions?.size
                                    }
                                            ?: 0
                                } else {
                                    0
                                }
                        )
                    } else {

                        pQuesList?.forEachIndexed { index, result ->
                            result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                                Log.e(
                                        "nestedQuestions",
                                        "${nestedQuestion._id} ${answer._id}"
                                )
                                nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                                    question.answers.forEachIndexed { index8, answerX ->
                                        if (answerX._id == answer._id) {
                                            mListener.onDataSelectedFromItemNested(
                                                    pSpinnerView,
                                                    answer,
                                                    "",
                                                    pQuesList!![index].nestedQuestions?.get(
                                                            i
                                                    )?.questions?.get(
                                                            ii
                                                    )
                                            )
                                        }
                                    }

                                }
                            }
                        }

                    }
                }
            }
        }
    }

    var nestedQuestionList: java.util.ArrayList<Answer> = ArrayList()
    private fun handleNestedQuestionsWithCustomSpinner(
            pRootView: View,
            pAdapterPosition: Int,
            pIndex: Int
    ) {

        parentAnswerItemIndex = pIndex
        nestedQuestionList.clear()
        if (pQuesList!![pAdapterPosition].type == Constants.QuestionType.sNESTED_QUESTIONS) {
            if (pIndex == 0) {
                pRootView.secondQuestionLL.visibility = View.VISIBLE
                pRootView.thirdQuestionLL.visibility = View.VISIBLE
                pRootView.secondSpinner.clearSelectedItem()
                pRootView.thirdSpinner.clearSelectedItem()
                pRootView.fourthSpinner.clearSelectedItem()

                pQuesList!![pAdapterPosition].nestedQuestions?.forEachIndexed { index, nestedQuestion ->
                    if (nestedQuestion.choice == Constants.NestedQuestionChoice.sCHOICE_YES) {
                        nestedQuestion.questions?.forEachIndexed { indexQs, question ->


                            nestedQuestionList.add(Answer(question._id, question.question))
                            pRootView.secondSpinner.setItems(nestedQuestion.questions[0].answers.map {
                                Answer(
                                        it._id,
                                        it.value
                                )
                            }.toMutableList())

                            pRootView.thirdSpinner.setItems(nestedQuestion.questions[1].answers.map {
                                Answer(
                                        it._id,
                                        it.value
                                )
                            }.toMutableList())

                            pRootView.fourthSpinner.setItems(nestedQuestion.questions[2].answers.map {
                                Answer(
                                        it._id,
                                        it.value
                                )
                            }.toMutableList())
                        }
                    }
                }
                pRootView.secondQuestionTV.text = nestedQuestionList[0].value
                pRootView.thirdQuestionTV.text = nestedQuestionList[1].value
                pRootView.fourthQuestionTV.text = nestedQuestionList[2].value

                if (answerList != null){
                    for (item in answerList!!.result!!){
                        if (item?.question == nestedQuestionList[0]._id){
                            pRootView.secondSpinner.text = item?.choice
                        }
                        if (item?.question == nestedQuestionList[1]._id){
                            pRootView.thirdSpinner.text = item?.choice
                        }
                        if (item?.question == nestedQuestionList[2]._id){
                            pRootView.fourthSpinner.text = item?.choice
                        }
                    }
                }

            } else if (pIndex == 1) {
                pRootView.secondQuestionLL.visibility = View.VISIBLE
                pRootView.thirdQuestionLL.visibility = View.GONE
                pRootView.secondSpinner.clearSelectedItem()
                pQuesList!![pAdapterPosition].nestedQuestions?.forEachIndexed { index, nestedQuestion ->
                    if (nestedQuestion.choice == Constants.NestedQuestionChoice.sCHOICE_NO) {
                        nestedQuestionList.add(Answer(nestedQuestion.questions!![index]._id, nestedQuestion.questions!![index].question))
                        pRootView.secondQuestionTV.text = nestedQuestion.questions!![index].question
                        pRootView.secondSpinner.setItems(nestedQuestion.questions[index].answers.map {
                            Answer(
                                    it._id,
                                    it.value
                            )
                        }.toMutableList())

                        if (answerList != null){
                            for (item in answerList!!.result!!){
                                if (item?.question == nestedQuestionList[0]._id){
                                    pRootView.secondSpinner.text = item?.choice
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    private fun showNestedSpinnerDialog(
            pSpinnerView: PowerSpinnerView?,
            position: Int
    ) {
        var choiceList = ArrayList<Answer>()
        var choice: String = pSpinnerView?.text.toString()
        var question: String? = Constants.sEmpty_String

        if (pQuesList!![position].type == Constants.QuestionType.sNESTED_QUESTIONS) {
            if (parentAnswerItemIndex == 0) {
                pQuesList!![position].nestedQuestions?.forEachIndexed { _, nestedQuestion ->
                    if (nestedQuestion.choice == Constants.NestedQuestionChoice.sCHOICE_YES) {
                        when (pSpinnerView?.id) {
                            R.id.secondSpinner -> {
                                choiceList = nestedQuestion.questions?.get(0)?.answers?.map {
                                    Answer(
                                            it._id,
                                            it.value
                                    )
                                } as ArrayList<Answer>
                                question = nestedQuestion.questions?.get(0)?.question
                            }
                            R.id.thirdSpinner -> {
                                choiceList = nestedQuestion.questions?.get(1)?.answers?.map {
                                    Answer(
                                            it._id,
                                            it.value
                                    )
                                } as ArrayList<Answer>
                                question = nestedQuestion.questions?.get(1)?.question
                            }
                            R.id.fourthSpinner -> {
                                choiceList = nestedQuestion.questions?.get(2)?.answers?.map {
                                    Answer(
                                            it._id,
                                            it.value
                                    )
                                } as ArrayList<Answer>
                                question = nestedQuestion.questions?.get(2)?.question
                            }
                        }
                    }
                }
            } else if (parentAnswerItemIndex == 1) {
                pQuesList!![position].nestedQuestions?.forEachIndexed { index, nestedQuestion ->
                    if (nestedQuestion.choice == Constants.NestedQuestionChoice.sCHOICE_NO) {
                        when (pSpinnerView?.id) {
                            R.id.secondSpinner -> {
                                choiceList = nestedQuestion.questions?.get(index)?.answers?.map {
                                    Answer(
                                            it._id,
                                            it.value
                                    )
                                } as ArrayList<Answer>
                                question = nestedQuestion.questions?.get(index)?.question
                            }
                        }
                    }
                }
            }
        }

        val dialog = CustomAnswerDialog()
        dialog.createDialog(
                pContext, question, choiceList,
                object : CustomAnswerDialog.DialogCallback {
                    override fun onItemClick(answer: Answer?, pItemPosition: Int) {
                        if (answer != null) {
                            pSpinnerView?.text = answer.value
                            choice = pSpinnerView?.text.toString()
                            when (pSpinnerView?.id) {
                                R.id.secondSpinner -> {
                                    pQuesList?.forEachIndexed { index, result ->
                                        result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                                            Log.e(
                                                    "nestedQuestions",
                                                    "${nestedQuestion._id} ${answer._id}"
                                            )
                                            if (pQuesList!![position].choice.uppercase() == nestedQuestion.choice) {
                                                nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                                                    question.answers.forEachIndexed { index8, answerX ->
                                                        if (answerX._id == answer._id) {
                                                            mListener.onDataSelectedFromItemNested(
                                                                    pSpinnerView,
                                                                    answer,
                                                                    "",
                                                                    pQuesList!![index].nestedQuestions?.get(
                                                                            i
                                                                    )?.questions?.get(
                                                                            ii
                                                                    )
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                                R.id.thirdSpinner -> {
                                    pQuesList?.forEachIndexed { index, result ->
                                        result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                                            Log.e(
                                                    "nestedQuestions",
                                                    "${nestedQuestion._id} ${answer._id}"
                                            )
                                            nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                                                question.answers.forEachIndexed { index8, answerX ->
                                                    if (answerX._id == answer._id) {
                                                        mListener.onDataSelectedFromItemNested(
                                                                pSpinnerView,
                                                                answer,
                                                                "",
                                                                pQuesList!![index].nestedQuestions?.get(i)?.questions?.get(
                                                                        ii
                                                                )
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                                R.id.fourthSpinner -> {
                                    pQuesList?.forEachIndexed { index, result ->
                                        result.nestedQuestions?.forEachIndexed { i: Int, nestedQuestion: NestedQuestion ->
                                            Log.e(
                                                    "nestedQuestions",
                                                    "${nestedQuestion._id} ${answer._id}"
                                            )
                                            if (pQuesList!![position].choice.uppercase() == nestedQuestion.choice) {
                                                nestedQuestion?.questions?.forEachIndexed { ii: Int, question: Question ->
                                                    question.answers.forEachIndexed { index8, answerX ->
                                                        if (answerX._id == answer._id) {
                                                            mListener.onDataSelectedFromItemNested(
                                                                    pSpinnerView,
                                                                    answer,
                                                                    "",
                                                                    pQuesList!![index].nestedQuestions?.get(
                                                                            i
                                                                    )?.questions?.get(
                                                                            ii
                                                                    )
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, choice)
    }

    interface QuestionsItemListener {
        fun onDataSelectedFromItem(
                pView: View?,
                pAnswerModel: Answer?,
                pText: String?,
                pResultModel: Result?,
                pNestedQuestionCount: Int = 0,
        )

        fun onDataSelectedFromItem(
                pView: View?,
                pAnswerModel: Answer?,
                pText: String?,
                pResultModel: Result?
        )

        fun onDataSelectedFromItemNested(
                pView: View?,
                pAnswerModel: Answer?,
                pText: String?,
                pResultModel: Question?
        )

        fun updateArrayList(nestedArrayList: ArrayList<Answer>)
        fun onDontKnowClick()
        fun removeAnsFromList(pResultModel: Result?)
    }
}