package com.jda.application.fragments.subscriptions

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.jda.application.R
import com.jda.application.base.activity.BaseActivity
import com.jda.application.databinding.ActivitySubscriptionBinding
import com.jda.application.utils.OnItemClickListener

class SubscriptionActivity : BaseActivity(), OnItemClickListener {
    private lateinit var binding: ActivitySubscriptionBinding

    override fun onPermissionGranted(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription)
        binding.appBar.clickHandle = this
        initAppBAr()
        setViewPager()
    }

    private fun initAppBAr() {
        binding.appBar.backBt.visibility = View.VISIBLE
        binding.appBar.tittleTv.text = getString(R.string.subscription_plan)
    }

    private fun setViewPager() {
        val viewPager = binding.subsViewPager
        viewPager.apply {
            clipToPadding = false
            setPadding(100, 0, 100, 0)
            pageMargin = 30
        }
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }

    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.backBt -> {
                onBackPressed()
            }
        }
    }
}