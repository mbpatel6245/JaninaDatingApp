package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.UsersItemLayoutBinding
import com.jda.application.fragments.homeFragment.HomeSuccessResponse
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.CommonUtility.capitaliseOnlyFirstLetter

class UserListAdapter(var userClickListenerHandler: UserClickListener, var data: ArrayList<HomeSuccessResponse.User>) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = DataBindingUtil.inflate<UsersItemLayoutBinding>(LayoutInflater.from(viewGroup.context), R.layout.users_item_layout, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val binding = holder.binding
        CommonUtility.setGlideImage(binding.profileIV.context, data[position].image, binding.profileIV)
        binding.nameAndAgeTV.text = (capitaliseOnlyFirstLetter(data[position].firstName)+ " " + capitaliseOnlyFirstLetter(data[position].lastName) + "," + data[position].age.toString())
        binding.locationTV.text = data[position].location
    }

    inner class MyViewHolder(var binding: UsersItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.profileIV.setOnClickListener { userClickListenerHandler.onAdapterItemClick(binding.profileIV.id,bindingAdapterPosition ,data[bindingAdapterPosition]) }
            binding.likeIV.setOnClickListener { userClickListenerHandler.onAdapterItemClick(binding.likeIV.id,bindingAdapterPosition ,data[bindingAdapterPosition]) }
            binding.dislikeIV.setOnClickListener { userClickListenerHandler.onAdapterItemClick(binding.dislikeIV.id,bindingAdapterPosition ,data[bindingAdapterPosition]) }
            binding.blockIV.setOnClickListener { userClickListenerHandler.onAdapterItemClick(binding.blockIV.id,bindingAdapterPosition ,data[bindingAdapterPosition]) }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface UserClickListener {
        fun onAdapterItemClick(pId: Int, pPosition: Int, pData: HomeSuccessResponse.User?)
    }
}