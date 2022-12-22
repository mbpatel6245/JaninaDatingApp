package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.AdapterReviewListDesignBinding
import com.jda.application.fragments.ratingModule.RatingRequestNew
import com.jda.application.utils.CommonUtility

class ReviewListAdapter(var data: ArrayList<RatingRequestNew.Result.Review>) : RecyclerView.Adapter<ReviewListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = DataBindingUtil.inflate<AdapterReviewListDesignBinding>(LayoutInflater.from(viewGroup.context), R.layout.adapter_review_list_design, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val binding = holder.binding
        CommonUtility.setGlideBlurImage(binding.profileImageIV.context, data[position].user?.image
                ?: "", binding.profileImageIV)
//        binding.nameTV.text = (data[position].user?.firstName + " " + data[position].user?.lastName)
        binding.nameTV.text = "${data[position].user?.firstName?.substring(0, 1)?.toUpperCase()}*******"
        binding.timeStampTV.text = CommonUtility.covertTimeToTextNew(context, data[position].createdAt)
        binding.ratingBarRB.rating = data[position].user?.rating ?: 0.0f
        binding.dataTV.text = data[position].comment
    }

    inner class MyViewHolder(var binding: AdapterReviewListDesignBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun getItemCount(): Int {
        return data.size
    }
}