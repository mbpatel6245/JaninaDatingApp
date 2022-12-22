package com.jda.application.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.jda.application.R
import com.jda.application.utils.CommonUtility.setGlideImage

class ImageSliderAdapter(private val mContext: Context, private val imageList: ArrayList<String>) : PagerAdapter() {
//    private val inflater: LayoutInflater
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val imageLayout = LayoutInflater.from(mContext).inflate(R.layout.slider_home, collection, false) as ViewGroup
        setGlideImage(collection.context, imageList[position], (imageLayout.findViewById<View>(R.id.imageView) as ImageView))
        collection.addView(imageLayout)
        return imageLayout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return imageList[position].toString()
    }

//    init {
//        inflater = LayoutInflater.from(mContext)
//    }
}