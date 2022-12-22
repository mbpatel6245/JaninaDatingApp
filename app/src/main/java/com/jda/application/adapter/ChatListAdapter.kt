package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.ChatListItemLayoutBinding
import com.jda.application.fragments.messagesModule.ChatListSuccessModel
import com.jda.application.utils.CommonUtility


class ChatListAdapter(var clickHandler: CallBack, var arrayListSuccess: ArrayList<ChatListSuccessModel.Result.Chat>) : RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = DataBindingUtil.inflate<ChatListItemLayoutBinding>(LayoutInflater.from(viewGroup.context), R.layout.chat_list_item_layout, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val binding = holder.binding
        binding.messageTagTV.text = arrayListSuccess[position].lastMessage?.text ?: ""
        binding.dateTagTV.text= CommonUtility.covertTimeToTextNew(context, arrayListSuccess[position].lastMessage?.createdAt)?:""
        if (arrayListSuccess[position].unReadCount > 0) {
            binding.countTagTV.visibility = View.VISIBLE
            binding.messageTagTV.setTextColor(ContextCompat.getColorStateList(context, R.color.white))
            binding.countTagTV.text = arrayListSuccess[position].unReadCount.toString()
        } else {
            binding.messageTagTV.setTextColor(ContextCompat.getColorStateList(context, R.color.white))
            binding.countTagTV.visibility = View.INVISIBLE
        }
        binding.headingTagTV.text = (CommonUtility.capitaliseOnlyFirstLetter(arrayListSuccess[position].user.firstName) + " " + CommonUtility.capitaliseOnlyFirstLetter(
            arrayListSuccess[position].user.lastName)
        )
        CommonUtility.setGlideImage(binding.profileCV.context, arrayListSuccess[position].user.image, binding.profileCV)

        if (position == arrayListSuccess.size - 1) {
            clickHandler.pagination()
        }
    }

    inner class MyViewHolder(var binding: ChatListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.foregroundView.setOnClickListener {
                clickHandler.onItemClicked(it.id, bindingAdapterPosition, arrayListSuccess[bindingAdapterPosition])
            }
            binding.removeIvSaved.setOnClickListener {
                clickHandler.onItemClicked(binding.removeIvSaved.id, bindingAdapterPosition, arrayListSuccess[bindingAdapterPosition])
            }
            binding.profileCV.setOnClickListener {
                clickHandler.onItemClicked(it.id, bindingAdapterPosition, arrayListSuccess[bindingAdapterPosition])
            }
        }
    }


    override fun getItemCount(): Int {
        return arrayListSuccess.size
    }

    interface CallBack {
        fun onItemClicked(@IdRes id: Int, position: Int, data: ChatListSuccessModel.Result.Chat?)
        fun pagination()
    }
}