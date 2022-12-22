package com.jda.application.fragments.settingsModule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.jda.application.R
import com.jda.application.acivities.WebViewActivity
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.base.service.URLs
import com.jda.application.databinding.FragmentSettingsBinding
import com.jda.application.fragments.subscriptions.SubscriptionActivity
import com.jda.application.utils.Constants

class SettingsFragment : BaseFragment() {
    private var mBinding: FragmentSettingsBinding? = null

    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() = SettingsFragment()
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*if (Constants.sIsFromChatDialog) {

        } else {
            mBinding?.settingsParentLayout?.visibility = View.VISIBLE
        }*/
        mBinding?.aboutTV?.setOnClickListener {
                startActivity(Intent(requireActivity(), WebViewActivity::class.java).apply {
                    putExtra(Constants.BundleParams.sURl, URLs.APIs.sAboutUsUrl)
                })
        }

        mBinding?.subscribeTV?.setOnClickListener {
//            mActivity!!.mainReplaceStack(SubscriptionFragment.newInstance())
            val intent = Intent(mActivity, SubscriptionActivity::class.java)
            startActivity(intent)
            //  mActivity?.showToast("coming soon")
        }

        mBinding?.helpTV?.setOnClickListener {
            mActivity?.showToast("coming soon")
        }
    }
}