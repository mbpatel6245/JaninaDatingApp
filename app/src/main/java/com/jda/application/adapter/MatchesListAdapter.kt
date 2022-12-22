package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.base.service.URLs
import com.jda.application.databinding.MatchesItemLayoutBinding
import com.jda.application.fragments.matchesModule.MatchesListSuccessModel
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants

class MatchesListAdapter(var mListener: MatchesRequestItemListener, var pUsersList: MutableList<MatchesListSuccessModel.Result.User>?) : RecyclerView.Adapter<MatchesListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = DataBindingUtil.inflate<MatchesItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.matches_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        if(pUsersList?.size?:0 > position && pUsersList!![position].user !=null) {
            CommonUtility.setGlideImage(
                binding.profileCV.context,
                pUsersList!![position].user.image,
                binding.profileCV
            )
            binding.nameTV.text =
                (CommonUtility.capitaliseOnlyFirstLetter(pUsersList!![position].user.firstName) + " " + CommonUtility.capitaliseOnlyFirstLetter(
                    pUsersList!![position].user.lastName)
                )
        }

        binding.acceptIV.visibility = if(pUsersList?.get(position)?.status == Constants.MatchListStatus.Match.values) View.GONE else View.VISIBLE

        if (position == pUsersList?.size?:0 - 1) {
            mListener.pagination()
        }
    }

    override fun getItemCount(): Int {
        return pUsersList!!.size
    }

    inner class MyViewHolder(var binding: MatchesItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.acceptIV.setOnClickListener {
                mListener.onItemClicked(binding.acceptIV.id, bindingAdapterPosition, pUsersList!![bindingAdapterPosition])
            }
            binding.rejectIV.setOnClickListener {
                mListener.onItemClicked(binding.rejectIV.id, bindingAdapterPosition, pUsersList!![bindingAdapterPosition])
            }

            binding.profileCV.setOnClickListener {
                mListener.onItemClicked(it.id, bindingAdapterPosition, pUsersList!![bindingAdapterPosition])
            }
            binding.foregroundView.setOnClickListener {
                mListener.onItemClicked(it.id, bindingAdapterPosition, pUsersList!![bindingAdapterPosition])
            }
        }
    }

    interface MatchesRequestItemListener {
        fun onItemClicked(id: Int, position: Int, pModel: MatchesListSuccessModel.Result.User)
        fun pagination()
    }
}