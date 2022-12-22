package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.ProfileQuestionsItemLayoutBinding
import com.jda.application.fragments.profileModule.ProfileFetchResponse
import com.jda.application.utils.Constants

class MyProfileQsAdapter(var answers: List<ProfileFetchResponse.Result.Answer>) : RecyclerView.Adapter<MyProfileQsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfileQsAdapter.MyViewHolder {
        val view = DataBindingUtil.inflate<ProfileQuestionsItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.profile_questions_item_layout, parent, false)
        return MyProfileQsAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        val context = holder.itemView.context
        when (answers[position].type) {
            Constants.QuestionType.sCHOICE -> {
                binding.ansTv.text = answers[position].choice
            }
            Constants.QuestionType.sTEXT -> {
                binding.ansTv.text = answers[position].text
            }
        }
        binding.quesTv.text = answers[position].question
        binding.textA.text = context.getString(R.string.a)
    }

    override fun getItemCount(): Int {
        return answers.size
    }

    class MyViewHolder(var binding: ProfileQuestionsItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}