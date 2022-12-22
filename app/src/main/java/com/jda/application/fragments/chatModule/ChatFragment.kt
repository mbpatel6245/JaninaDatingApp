package com.jda.application.fragments.chatModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.jda.application.R
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentChatBinding
import com.jda.application.fragments.matchesModule.MatchesRequestFragment
import com.jda.application.fragments.messagesModule.ChatListFragment

class ChatFragment : BaseFragment() {

    private var mBinding: FragmentChatBinding? = null
    private lateinit var mChatCollectionAdapter: ChatCollectionAdapter

    companion object {
        val TAG: String = ChatFragment::class.java.simpleName
        fun newInstance() = ChatFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialise()
    }

    private fun initialise() {
        mBinding?.appBarChat?.tittleTv?.text = getString(R.string.chat)
        mChatCollectionAdapter = ChatCollectionAdapter(this)
        mBinding?.pager?.adapter = mChatCollectionAdapter
        TabLayoutMediator(mBinding?.tabLayout!!, mBinding?.pager!!) { tab, position ->
            tab.text = if (position == 0) getString(R.string.messages) else getString(R.string.matches)
        }.attach()
    }

    class ChatCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            return if (position == 0) ChatListFragment.newInstance() else MatchesRequestFragment.newInstance()
        }
    }
}