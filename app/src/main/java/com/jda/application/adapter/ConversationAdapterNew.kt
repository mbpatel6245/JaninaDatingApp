package com.jda.application.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.databinding.AdapterChatDesignBinding
import com.jda.application.fragments.chatModule.ConversationSuccessModel
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants
import java.util.*

class ConversationAdapterNew(
    var callBack: CallBack,
    var arrayList: MutableList<ConversationSuccessModel.Result.Message.MessageX>
) : RecyclerView.Adapter<ConversationAdapterNew.MyViewHolder>() {

    var isLongPressed = false

    class MyViewHolder(var binding: AdapterChatDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<AdapterChatDesignBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.adapter_chat_design,
            viewGroup,
            false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        val context = holder.itemView.context
        val fromID = if(arrayList[position].reciever!=null) arrayList[position].reciever._id else ""
        val myID = JDAApplication.mInstance.getProfile()?.result?._id

        /*
           * Read/Un-read handled here
           * */

        if (arrayList[position].isReaded) {
            readUnreadFun(position, R.drawable.double_tick, binding)
        } else {
            readUnreadFun(position, R.drawable.single_tick, binding)
        }


        /*
        * Message :- Multiple message display
        * with sender and getter identify
        * */
        if (fromID == myID) {
            binding.getterLL.visibility = View.VISIBLE
            binding.senderLL.visibility = View.GONE
            binding.senderTV.visibility = View.GONE
            binding.senderTextTimeTV.visibility = View.GONE
            binding.senderTimeTV.visibility = View.GONE

            binding.getterTV.visibility = View.GONE
            binding.getterTextTimeTV.visibility = View.GONE
            binding.senderTimeTV.visibility = View.GONE
            binding.getterTimeTV.visibility = View.GONE


            when (arrayList[position].type) {
                Constants.Socket_id.TEXT_ID -> {
                    binding.chatRateTV.visibility= View.GONE
                    binding.getterTV.visibility = View.VISIBLE
                    binding.getterTextTimeTV.visibility = View.VISIBLE
                    binding.getterTV.text = arrayList[position].text
                    binding.getterTextTimeTV.text =
                        CommonUtility.convertDateFormat("hh:mm a", arrayList[position].createdAt)
                }
                Constants.Socket_id.CHAT_RATE_TYPE -> {
                    binding.senderLL.visibility = View.GONE
                    binding.getterLL.visibility = View.GONE
                    binding.chatRateTV.visibility= View.VISIBLE
                    val time = CommonUtility.convertDateFormat("hh:mm a", arrayList[position].createdAt)
                    Log.d("CheckLastMessage", "onBindViewHolder: $time position: $position")
                    callBack.setChatRating(binding.chatRateTV)
                }
            }

            /*
            * Read/Un-read handled here
            * */

            /* if (firstUser.userId != myID && firstUser.lastReadAt != null) {
                 if (CommonUtility.convertStringToDate(firstUser.lastReadAt)
                                 .after(CommonUtility.convertStringToDate(arrayList[position].message.createdAt))) {
                     readUnreadFun(position, R.drawable.double_tick, binding)
                 } else {
                     readUnreadFun(position, R.drawable.single_tick, binding)
                 }
             }

             if (secondUser.userId != myID && secondUser.lastReadAt != null) {
                 if (CommonUtility.convertStringToDate(secondUser.lastReadAt)
                                 .after(CommonUtility.convertStringToDate(arrayList[position].message.createdAt))) {
                     readUnreadFun(position, R.drawable.double_tick, binding)
                 } else {
                     readUnreadFun(position, R.drawable.single_tick, binding)
                 }
             }*/

        } else {
            binding.getterLL.visibility = View.GONE
            binding.senderLL.visibility = View.VISIBLE
            binding.getterTV.visibility = View.GONE
            binding.getterTextTimeTV.visibility = View.GONE
            binding.getterTimeTV.visibility = View.GONE


            binding.senderTV.visibility = View.GONE
            binding.senderTextTimeTV.visibility = View.GONE

            binding.senderTimeTV.visibility = View.GONE
            binding.getterTimeTV.visibility = View.GONE


            when (arrayList[position].type) {
                Constants.Socket_id.TEXT_ID -> {
                    binding.chatRateTV.visibility= View.GONE
                    binding.senderTV.visibility = View.VISIBLE
                    binding.senderTextTimeTV.visibility = View.VISIBLE
                    binding.senderTV.text = arrayList[position].text
                    binding.senderTextTimeTV.text =
                        CommonUtility.convertDateFormat("hh:mm a", arrayList[position].createdAt)
                }
                Constants.Socket_id.CHAT_RATE_TYPE -> {
                    binding.senderLL.visibility = View.GONE
                    binding.getterLL.visibility = View.GONE
                    binding.chatRateTV.visibility= View.VISIBLE
                    val time = CommonUtility.convertDateFormat("hh:mm a", arrayList[position].createdAt)
                    Log.d("CheckLastMessage", "onBindViewHolder: $time position: $position")
                    callBack.setChatRating(binding.chatRateTV)
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (isLongPressed && fromID == myID)
                callBack.longPressed(position, false)
        }

        /*
        * Time stamp handled
        * */
        var previousDate = ""
        if (position == arrayList.size - 1) {
            binding.timeStampTV.visibility = View.VISIBLE
//            binding.timeStampTV.setText(CommonUtility.convertDateFormat("EEE, MMM dd yyyy", arrayList[position].createdAt))
            setTimeTextVisibility(context, arrayList[position].createdAt, previousDate, binding)
        } else if (position != arrayList.size - 1) {
            previousDate = arrayList[position + 1].createdAt
            setTimeTextVisibility(context, arrayList[position].createdAt, previousDate, binding)
        }

//        if (position == 0) {
//            callBack.setChatRating(binding.chatRateTV)
//        } else {
//            binding.chatRateTV.visibility = View.GONE
//        }


        /*
        * Pagination handled
        * */
        if (position == arrayList.size - 1) {
            callBack.apiHitForPagination()
        }
    }

    private fun setTimeTextVisibility(
        context: Context,
        createdAt: String,
        previousDate: String,
        binding: AdapterChatDesignBinding
    ) {
        val currentDate: Calendar = Calendar.getInstance()
        val cal1: Calendar = Calendar.getInstance()
        val cal2: Calendar = Calendar.getInstance()
        cal1.timeInMillis = CommonUtility.convertStringToLong(createdAt)
        if (previousDate.isNotEmpty())
            cal2.timeInMillis = CommonUtility.convertStringToLong(previousDate)

        val sameMonth =
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        if (sameMonth && previousDate.isNotEmpty()) {
            binding.timeStampTV.visibility = View.GONE
            binding.timeStampTV.text = ""
        } else {
            binding.timeStampTV.visibility = View.VISIBLE

            if (cal1.get(Calendar.DATE) == currentDate.get(Calendar.DATE)) {
                binding.timeStampTV.setText(context.getString(R.string.today))
            } else if (currentDate.get(Calendar.DATE) - cal1.get(Calendar.DATE) == 1) {
                binding.timeStampTV.setText(context.getString(R.string.yesterday))
            } else {
                binding.timeStampTV.setText(
                    CommonUtility.convertDateFormat(
                        "EEE, MMM dd yyyy",
                        createdAt
                    )
                )
            }
        }


    }

    fun readUnreadFun(
        position: Int,
        @DrawableRes drawableRes: Int,
        binding: AdapterChatDesignBinding
    ) {
        binding.senderTextTimeTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface CallBack {
        fun longPressed(pos: Int, isFirstTime: Boolean)
        fun apiHitForPagination()
        fun setChatRating(pView: TextView)
    }
}
