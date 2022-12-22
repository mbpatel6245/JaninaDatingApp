package com.jda.application.fragments.subscriptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.databinding.FragmentAmazingSubscriptionBinding
import com.jda.application.utils.Constants
import org.greenrobot.eventbus.EventBus

class AmazingSubscriptionFragment : Fragment() {

    private lateinit var binding: FragmentAmazingSubscriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_amazing_subscription, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = JDAApplication.mInstance.getProfile()
//        if (data?.result?.subscriptionId == Constants.SubscriptionType.AMAZING_SUB) {
//            binding.buyBtn.visibility = View.GONE
//            binding.colorChangeLL.background = ContextCompat.getDrawable(requireActivity(),R.drawable.top_round_green_bg)
//            binding.titleTV.background = ContextCompat.getDrawable(requireActivity(),R.drawable.radius_4_back_light_green)
//        }

        clickListeners()
    }

    private fun clickListeners() {
        binding.buyBtn.setOnClickListener {
//            EventBus.getDefault().post(Constants.SubscriptionType.AMAZING_SUB)
        }
    }
}