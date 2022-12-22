package com.jda.application.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.CustomDialogueLayoutBinding
import com.jda.application.databinding.DialogueItemBinding
import com.jda.application.fragments.questionModule.Answer


class CustomAnswerDialog {
    var mDialog: AlertDialog? = null
    lateinit var mCallBacks: DialogCallback
    var answer: Answer? = null
    lateinit var mAdapter: ListAdapter
    var dialogBinding: CustomDialogueLayoutBinding? = null
    var position = 0;

    fun createDialog(
        context: Activity,
        dialogTitle: String?,
        list: ArrayList<Answer>,
        callback: DialogCallback,
        choice: String?
    ) {
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_dialogue_layout,
            null,
            false
        )
        mDialog = AlertDialog.Builder(context, R.style.MyDialogStyle).apply {
            setView(dialogBinding?.root)
        }.create()
        initViews()
        dialogBinding?.dialogueTitle?.text = dialogTitle ?: Constants.sEmpty_String
        mCallBacks = callback
        mAdapter = ListAdapter(object : ListAdapter.UserClickListener {
            override fun onAdapterItemClick(pPosition: Int, isChecked: Boolean) {
                if (isChecked) {
                    list[pPosition].let { answer = it }
                }
                list[pPosition].isSelected = isChecked
                position = pPosition
                mAdapter.notifyDataSetChanged()
            }
        }, list, choice)
        dialogBinding?.recyclerView?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        dialogBinding?.recyclerView?.adapter = mAdapter
        mDialog?.setCancelable(false)
        mDialog?.show()
        val window: Window? = mDialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun initViews() {
        dialogBinding?.cancelButton?.setOnClickListener {
            mDialog?.dismiss()
            mDialog = null
        }
        dialogBinding?.doneButton?.setOnClickListener {
            mDialog?.dismiss()
            mCallBacks.onItemClick(answer,position)
            mDialog = null
        }
    }

    class ListAdapter(
        var userClickListenerHandler: UserClickListener,
        var data: ArrayList<Answer>,
        var choice: String?
    ) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
            val view = DataBindingUtil.inflate<DialogueItemBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.dialogue_item,
                viewGroup,
                false
            )
            return MyViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val binding = holder.binding as DialogueItemBinding

            binding.itemTitle.text = data[position].value
            data[position].isSelected = isCheckValue(data[position].value)
            binding.itemCheckBox.isChecked = data[position].isSelected
            binding.itemCheckBox.setOnClickListener {
                choice = data[position].value
                userClickListenerHandler.onAdapterItemClick(
                    position, !data[position].isSelected
                )
            }
            binding.root.setOnClickListener {
                choice = data[position].value
                userClickListenerHandler.onAdapterItemClick(
                    position, !data[position].isSelected
                )

            }

        }

        private fun isCheckValue(value: String?): Boolean {
            return value == choice
        }

        inner class MyViewHolder(var binding: ViewDataBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun getItemCount(): Int {
            return data.size
        }

        interface UserClickListener {
            fun onAdapterItemClick(pPosition: Int, isChecked: Boolean)
        }
    }

    interface DialogCallback {
        fun onItemClick(answer: Answer?,pItemPosition: Int)
    }
}