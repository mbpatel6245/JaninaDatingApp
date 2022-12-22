package com.jda.application.utils

import android.app.Activity
import android.app.AlertDialog
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.DialogueItemBinding
import com.jda.application.fragments.signInModule.preferencesFragment.MutipleChoice


object CustomDialogue {
    var mDialogue: AlertDialog? = null
    lateinit var mCancelButton: TextView
    lateinit var mDoneButton: TextView
    lateinit var mTitle: TextView
    lateinit var mRecyclerView: RecyclerView
    lateinit var mCallBacks: DialoguMapCallback
    var mMap = HashMap<Int, String>()
    var mList = ArrayList<MutipleChoice>()

    //    var mChoiceList = ArrayList<MutipleChoice>()
    lateinit var mAdapter: ListAdapter

    fun createDialogue(context: Activity, dialogueTitle: String, list: ArrayList<MutipleChoice>, isMultiSelect: Boolean, mapCallback: DialoguMapCallback, isAnySelctable: Boolean) {
        var v = LayoutInflater.from(context).inflate(R.layout.custom_dialogue_layout, null)
        mDialogue = AlertDialog.Builder(context,R.style.MyDialogStyle).apply {
            setView(v)
        }.create()
        mMap.clear()
        initViews(v)
        mTitle.text = dialogueTitle
        mCallBacks = mapCallback
        mList = list
        for (i in 0 until list.size) {
            if (list[i].isSelected) {
                mMap.put(i, list[i].title)
            }
        }
        mAdapter = ListAdapter(object : ListAdapter.UserClickListener {
            override fun onAdapterItemClick(pPosition: Int, isChecked: Boolean) {
                if (isMultiSelect) {
                    if (isAnySelctable && pPosition == 0) {
                        if (isChecked) {
                            for (item in mMap) {
                                list.get(item.key).isSelected = false
                            }
                            mMap.clear()
                            mMap.put(pPosition, list.get(pPosition).title)
                        } else {
                            checkAny(isAnySelctable)
                        }
                    } else {
                        checkAny(isAnySelctable)

                        if (isChecked) {
                            mMap.put(pPosition, list.get(pPosition).title)
                        } else {
                            mMap.remove(pPosition)
                        }
                    }
                } else {
                    for (item in mMap) {
                        list.get(item.key).isSelected = false
                    }
                    mMap.clear()

                    if (isChecked) {
                        mMap.put(pPosition, list.get(pPosition).title)
                    }


                }
                list.get(pPosition).isSelected = isChecked

                notifyAdapter(activity = context)
            }
        }, list)
        mRecyclerView.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        mRecyclerView.adapter = mAdapter
        mDialogue!!.setCancelable(false)
        mDialogue!!.show()
        val window: Window? = mDialogue!!.getWindow()
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    fun checkAny(isAnySelectable: Boolean) {
        if (!isAnySelectable) {
            return
        }
        if (mMap.containsKey(0)) {
            mMap.remove(0)
            mList.get(0).isSelected = false
        }
    }

    fun initViews(view: View) {
        mCancelButton = view.findViewById<TextView>(R.id.cancel_button)
        mDoneButton = view.findViewById<TextView>(R.id.done_button)
        mTitle = view.findViewById<TextView>(R.id.dialogue_title)
        mRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        mCancelButton.setOnClickListener {
            mDialogue!!.dismiss()
            mDialogue = null
        }
        mDoneButton.setOnClickListener {
            mDialogue!!.dismiss()
            if(mMap!=null)
            mCallBacks.onAdapterItemClick(mMap)
            mDialogue = null
        }
    }

    fun notifyAdapter(activity: Activity) {
//        activity.runOnUiThread {
////
//            mAdapter.notifyDataSetChanged()
//        }
        mRecyclerView.post(Runnable {

            mAdapter.notifyDataSetChanged()
        })
    }

    fun getChoiceSelectionsFromMap(map: HashMap<Int, String>, isAnySelectable: Boolean,offset:Int?=null): ArrayList<Int> {
        var offsetval = offset?:getOffset(isAnySelectable)
        var choiceList = ArrayList<Int>()
        for (item in map) {
            choiceList.add(item.key + offsetval)
        }
        return choiceList;
    }
fun getOffset(isAnySelectable: Boolean):Int{
    return  if (isAnySelectable) 1 else 2
}
    fun getChoiceSelectionsStringFromMap(map: HashMap<Int, String>): ArrayList<String> {
        return ArrayList(map.values);
    }

    fun getTextFromList(selectedList: ArrayList<String>): String {
        try {

            return (TextUtils.join(", ", selectedList!!))
        } catch (e: Exception) {
            return ""
        }
    }

    fun getTextFromList(map: HashMap<Int, String>): String {
        try {

            return (TextUtils.join(", ", ArrayList(map.values)))
        } catch (e: Exception) {
            return ""
        }
    }

    class ListAdapter(var userClickListenerHandler: UserClickListener, var data: ArrayList<MutipleChoice>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
//            if (viewType == 0) {
//                val viewClose = DataBindingUtil.inflate<CloseLayoutDesignBinding>(LayoutInflater.from(viewGroup.context), R.layout.close_layout_design, viewGroup, false)
//                return MyViewHolder(viewClose)
//
//            } else {
            val view = DataBindingUtil.inflate<DialogueItemBinding>(LayoutInflater.from(viewGroup.context), R.layout.dialogue_item, viewGroup, false)
            return MyViewHolder(view)
//            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val context = holder.itemView.context

            val binding = holder.binding as DialogueItemBinding
            binding.itemTitle.text = data[position].title
            binding.itemCheckBox.isChecked = data[position].isSelected
            binding.itemCheckBox.setOnClickListener { buttonView ->
                userClickListenerHandler.onAdapterItemClick(position, !data[position].isSelected)
            }
            binding.root.setOnClickListener {

                userClickListenerHandler.onAdapterItemClick(position, !data[position].isSelected)

            }

        }

        inner class MyViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        }

        override fun getItemCount(): Int {
            return data.size
        }

        interface UserClickListener {
            fun onAdapterItemClick(pPosition: Int, isChecked: Boolean)
        }
    }

    interface DialoguMapCallback {
        fun onAdapterItemClick(map: HashMap<Int, String>)
    }
}