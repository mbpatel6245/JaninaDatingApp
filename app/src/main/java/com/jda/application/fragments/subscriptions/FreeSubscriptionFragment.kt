package com.jda.application.fragments.subscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jda.application.R

class FreeSubscriptionFragment : Fragment() {

    companion object {
        val TAG: String = FreeSubscriptionFragment::class.java.simpleName
        @JvmStatic
        fun newInstance() = FreeSubscriptionFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_free_subscription, container, false)
    }

}