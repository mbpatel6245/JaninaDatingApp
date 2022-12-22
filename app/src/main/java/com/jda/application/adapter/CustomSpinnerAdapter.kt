package com.jda.application.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.databinding.SpinnerItemLayoutBinding
import com.jda.application.fragments.questionModule.Answer
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerInterface
import com.skydoves.powerspinner.PowerSpinnerView

class CustomSpinnerAdapter(
    powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<CustomSpinnerAdapter.MyViewHolder>(),
    PowerSpinnerInterface<Answer> {

    override var index: Int = powerSpinnerView.selectedIndex
    override val spinnerView: PowerSpinnerView = powerSpinnerView
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<Answer>? =
        null

    private val spinnerItems: MutableList<Answer> = arrayListOf()

    companion object {
        const val NO_SELECTED_INDEX = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            SpinnerItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        return MyViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener
                notifyItemSelected(position)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        binding.itemTV.text = spinnerItems[position].value
        holder.bind(spinnerItems[position], spinnerView)
    }

    override fun setItems(itemList: List<Answer>) {
        this.spinnerItems.clear()
        this.spinnerItems.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun notifyItemSelected(index: Int) {
        if (index == NO_SELECTED_INDEX) return
        val oldIndex = this.index
        this.index = index
        this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].value!!)
        this.onSpinnerItemSelectedListener?.onItemSelected(
            oldIndex = oldIndex,
            oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { spinnerItems[oldIndex] },
            newIndex = index,
            newItem = spinnerItems[index]
        )
    }

    override fun getItemCount() = this.spinnerItems.size

    class MyViewHolder(val binding: SpinnerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Answer, spinnerView: PowerSpinnerView) {
            // do something using a custom item.
        }
    }
}