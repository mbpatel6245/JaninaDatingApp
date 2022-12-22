package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.CloseLayoutDesignBinding
import com.jda.application.databinding.MutiselectionLayoutDesignBinding
import com.jda.application.fragments.signInModule.preferencesFragment.MutipleChoice

class MultiChoiceListAdapter(var userClickListenerHandler: UserClickListener, var data: ArrayList<MutipleChoice>) : RecyclerView.Adapter<MultiChoiceListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        if (viewType == 0) {
            val viewClose = DataBindingUtil.inflate<CloseLayoutDesignBinding>(LayoutInflater.from(viewGroup.context), R.layout.close_layout_design, viewGroup, false)
            return MyViewHolder(viewClose)

        } else {
            val view = DataBindingUtil.inflate<MutiselectionLayoutDesignBinding>(LayoutInflater.from(viewGroup.context), R.layout.mutiselection_layout_design, viewGroup, false)
            return MyViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        if (holder.itemViewType == 0) {
            val binding = holder.binding as CloseLayoutDesignBinding
            binding.closeTV.setOnClickListener { userClickListenerHandler.onAdapterItemClick(binding.closeTV.id, position, false) }

        } else {
            val binding = holder.binding as MutiselectionLayoutDesignBinding
            binding.titleTV.text = data[position].title
            binding.titleTV.isChecked = data[position].isSelected
            binding.titleTV.setOnClickListener { userClickListenerHandler.onAdapterItemClick(binding.mainCL.id, position, binding!!.titleTV.isChecked) }
        }
    }

    inner class MyViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface UserClickListener {
        fun onAdapterItemClick(pId: Int, pPosition: Int, isChecked: Boolean)
    }
}