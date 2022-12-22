package com.jda.application.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.databinding.ChatListDeleteDialogBinding

class UserAlertUtility {
    companion object {
        private var mAlertDialog: AlertDialog? = null
        private var mProgressDialog: Dialog? = null
        fun showSnackBar(pMessage: String?, pView: View?, pContext: Context?) {
            if (pView != null) {
                val snackbar = Snackbar.make(pView, pMessage!!, Snackbar.LENGTH_SHORT)
                val sbView = snackbar.view
                val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
                textView.setTextColor(ContextCompat.getColor(pContext!!, R.color.black))
                textView.maxLines = Constants.sSNACK_BAR_MAX_LINES
                sbView.setBackgroundColor(ContextCompat.getColor(pContext, R.color.white))
                snackbar.show()
            }
        }

        // Extension function to show the toast
        fun Context.showToast(pMessage: String?) {
            Toast.makeText(this, pMessage, Toast.LENGTH_SHORT).show()
        }

        fun showToast(pMessage: String?, pContext: Activity?) {
            Toast.makeText(pContext, pMessage, Toast.LENGTH_LONG).show()
        }

        // Show a dialog alert for some actions
        fun showAlertDialog(
                pTitle: String? = null,
                pMessage: String?,
                pContext: Activity?,
                pPositiveButton: String? = null,
                pNegativeButton: String? = Constants.sEmpty_String,
                unAuth: Int = 0
        ) {
            if (pContext != null) {
                if (pContext.isFinishing) {
                    return
                }
                /*
             * If the alert dialog is already showing need not to create a new one
             */if (mAlertDialog == null || !mAlertDialog!!.isShowing) {
                    mAlertDialog = AlertDialog.Builder(pContext, R.style.AlertDialogCustom).create()
                    if (pTitle == null) {
                        with(mAlertDialog) { this?.setTitle(pContext.getString(R.string.app_name)) }
                    } else {
                        with(mAlertDialog) { this?.setTitle(pTitle) }
                    }
                    mAlertDialog!!.setMessage(pMessage)
                    mAlertDialog!!.setCancelable(false)
                    var positiveText: String? = pContext.getString(R.string.ok_message)
                    mAlertDialog.run {
                        if (pPositiveButton != null) {
                            positiveText = pPositiveButton
                        }
                        this!!.setButton(
                                DialogInterface.BUTTON_POSITIVE,
                                positiveText
                        ) { _: DialogInterface?, _: Int ->
                            if (unAuth == Constants.HTTP_UNAUTHORIZED_ACCESS) {
                                JDAApplication.mInstance.gotoLoginActivity(pContext)
                            }
                        }
                        if (pNegativeButton != null && pNegativeButton.isNotEmpty()) {
                            this.setButton(
                                    DialogInterface.BUTTON_NEGATIVE,
                                    pNegativeButton
                            ) { _: DialogInterface?, _: Int -> hideAlertDialog() }
                        }
                        show()
                    }
                }
            }
        }

        // hide alert dialog
        private fun hideAlertDialog() {
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
                mAlertDialog = null
            }
        }

        //show progress dialog
        fun showProgressDialog(pLayout: Int, pContext: Context?, pCancelable: Boolean): Dialog? {
            if (pContext != null && !(pContext as Activity).isFinishing) {
                if (mProgressDialog == null || !this.mProgressDialog!!.isShowing) {
                    mProgressDialog = Dialog(pContext)
                    mProgressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mProgressDialog?.window?.setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT
                    )
                    mProgressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    mProgressDialog?.setContentView(pLayout)
                    mProgressDialog?.setCancelable(pCancelable)
                    mProgressDialog?.show()
                }
            }
            return mProgressDialog
        }

        //hide progress dialog
        fun hideProgressDialog() {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
                mProgressDialog = null
            }
        }

        fun openCustomDialog(pContext: Context?, pTitle: String?,
                             pMessage: String?,
                             pPositiveButton: String?,
                             pNegativeButton: String?,
                             pListener: CustomDialogClickListener,
                             showTwoButtons: Boolean) {
            var dialog: AlertDialog? = null
            val binding = DataBindingUtil.inflate<ChatListDeleteDialogBinding>(
                    LayoutInflater.from(pContext),
                    R.layout.chat_list_delete_dialog,
                    null,
                    false
            )
            binding.messageTV.text = pMessage
            binding.headingTagTV.text = pTitle
            binding.noBT.text = pNegativeButton ?: JDAApplication.mInstance.getString(R.string.no)
            binding.yesBT.text = pPositiveButton ?: JDAApplication.mInstance.getString(R.string.yes)
            binding.noBT.visibility = if (showTwoButtons) View.VISIBLE else View.GONE
            binding.clickHandler = object : OnItemClickListener {
                override fun onItemClick(item: View) {
                    when (item.id) {
                        R.id.yesBT -> {
                            dialog?.dismiss()
                            pListener.onYesClick()
                        }
                        R.id.noBT -> {
                            dialog?.dismiss()
                            pListener.onNoClick()
                        }
                    }

                }
            }
            val builder = AlertDialog.Builder(pContext)
            builder.setView(binding?.root)
            dialog = builder.create()
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.show()
        }
    }


    interface CustomDialogClickListener {
        fun onYesClick()
        fun onNoClick()
    }

}