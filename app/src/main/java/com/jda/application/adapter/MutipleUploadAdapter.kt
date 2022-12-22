package com.jda.application.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jda.application.R
import com.jda.application.databinding.ChatListDeleteDialogBinding
import com.jda.application.databinding.LayoutImaageUploadBinding
import com.jda.application.fragments.matchesModule.MatchesListSuccessModel
import com.jda.application.fragments.signInModule.preferencesFragment.UploadGalleryRequest
import com.jda.application.utils.*

class MutipleUploadAdapter(
        var userClickListenerHandler: UserClickListener,
        var data: ArrayList<UploadGalleryRequest>
) : RecyclerView.Adapter<MutipleUploadAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = DataBindingUtil.inflate<LayoutImaageUploadBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.layout_imaage_upload,
                viewGroup,
                false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val binding = holder.binding
        CommonUtility.setGlideImage(
                binding.uploadImgIV.context,
                data[position].urlPath,
                binding.uploadImgIV
        )

        if (data[position].isUploaded == Constants.UPLOAD_END) {
            binding.closeIV.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.uploadedIV.visibility = View.VISIBLE
        }
        if (data[position].isUploaded == Constants.UPLOAD_START) {
            binding.closeIV.visibility = View.GONE
            binding.uploadedIV.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }

        if (data[position].isUploaded == Constants.UPLOAD_HIDE) {
            binding.closeIV.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.uploadedIV.visibility = View.GONE
        }
        if (data[position].isServerUploaded) {
//            binding.closeIV.visibility = View.VISIBLE
        } else {
            binding.closeIV.visibility = View.GONE

        }

        binding.uploadImgIV.setOnClickListener {
            CommonUtility.openFullImage(
                    context,
                    data[position].urlPath, true,
                    object : OnDialogClickListener {
                        override fun onDeleteClick(dialog: Dialog) {
                            UserAlertUtility.openCustomDialog(
                                    context,context.getString(R.string.delete_pic),
                                    context.getString(R.string.are_you_sure_delete),null,null,
                                    object: UserAlertUtility.CustomDialogClickListener{
                                        override fun onYesClick() {
                                            userClickListenerHandler.onAdapterItemClick(
                                                    binding.closeIV.id,
                                                    position,
                                                    data[position]
                                            )
                                            dialog.dismiss()
                                        }

                                        override fun onNoClick() {

                                        }

                                    }
                            ,true)

//                            var innerDialog: AlertDialog? = null
//                            val builder = AlertDialog.Builder(context)
//                            builder.setTitle(context.getString(R.string.delete))
//                            builder.setMessage(context.getString(R.string.are_you_sure_delete))
//                            builder.setPositiveButton(context.getString(R.string.yes)) { dialogInterface, which ->
//                                userClickListenerHandler.onAdapterItemClick(
//                                        binding.closeIV.id,
//                                        position,
//                                        data[position]
//                                )
//                                dialog.dismiss()
//                            }
//                            builder.setNegativeButton(context.getString(R.string.no)) { dialogInterface, which ->
//                                innerDialog?.dismiss()
//                            }
//                            innerDialog = builder.create()
//                            innerDialog.setCancelable(false)
//                            innerDialog.show()
                        }

                    })
        }
    }

    inner class MyViewHolder(var binding: LayoutImaageUploadBinding) :
            RecyclerView.ViewHolder(binding.root) {
        init {
            binding.closeIV.setOnClickListener {
                userClickListenerHandler.onAdapterItemClick(
                        binding.closeIV.id,
                        bindingAdapterPosition,
                        data[bindingAdapterPosition]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    interface UserClickListener {
        fun onAdapterItemClick(pId: Int, pPosition: Int, pData: UploadGalleryRequest)
    }

}