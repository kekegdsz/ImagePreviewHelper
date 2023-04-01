package com.ky.android.photo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.ky.android.photo.config.ImagePreviewConfig
import com.ky.android.photo.databinding.ActivityImagePreviewBinding

class ImagePreviewActivity : AppCompatActivity() {
    private var _binding: ActivityImagePreviewBinding? = null
    private var imageConfig: ImagePreviewConfig? = null
    private var fragmentList: MutableList<ImagePreviewFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        initIntent()
        initViews()
    }

    private fun initIntent() {
        imageConfig = intent.getParcelableExtra("config")
    }

    private fun initViews() {
        val currentPosition: Int? = imageConfig?.position
        val imageUrls: MutableList<String>? = imageConfig?.imgUrls
        fragmentList = mutableListOf()
        for (index in imageUrls?.indices!!) {
            val imageFragment: ImagePreviewFragment = ImagePreviewFragment.newInstance()
            imageFragment.arguments = Bundle().apply {
                putParcelable("config", imageConfig?.originModel)
                putString("url", imageUrls[index])
                putInt("position", index)
            }
            fragmentList?.add(imageFragment)
        }
        _binding?.viewPager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList!![position]
            }

            override fun getCount(): Int {
                return fragmentList!!.size
            }
        }
        if (currentPosition != null) {
            _binding?.viewPager?.currentItem = currentPosition
        }
    }

    companion object {
        @JvmStatic
        fun start(activity: Activity, config: ImagePreviewConfig) {
            val intent = Intent(activity, ImagePreviewActivity::class.java)
            intent.putExtra("config", config)
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}