package com.ky.android.photo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ky.android.photo.config.ContentViewOriginModel
import com.ky.android.photo.databinding.FragmentImagePreviewBinding

open class ImagePreviewFragment : Fragment() {
    private lateinit var _binding: FragmentImagePreviewBinding
    private var url: String? = null
    private var position: Int? = null
    private var originModel: ContentViewOriginModel? = null

    companion object {
        fun newInstance() =
            ImagePreviewFragment()
    }

    private lateinit var viewModel: ImagePreviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        initIntent()
        initViews()
        return _binding.root
    }

    private fun initViews() {
        _binding.imageDetailFinger.setOnAlphaChangeListener {
            val colorId = convertPercentToBlackAlphaColor(it)
            _binding.imageDetailFinger.setBackgroundColor(colorId)
        }
        _binding.imageDetailFinger.setOnPageFinishListener {
            activity?.finish()
        }
        _binding.photoView.setOnClickListener {
            activity?.finish()
        }
        activity?.let {
            Glide.with(it).load(url).into(_binding.photoView)
        }
    }

    private fun convertPercentToBlackAlphaColor(alpha: Float): Int {
        var percent = Math.min(1f, Math.max(0f, alpha))
        val intAlpha = (percent * 255).toInt()
        val stringAlpha = Integer.toHexString(intAlpha).toLowerCase()
        val color = "#" + (if (stringAlpha.length < 2) "0" else "") + stringAlpha + "000000"
        return Color.parseColor(color)
    }

    private fun isVisibleToUser(): Boolean {
        return isResumed && userVisibleHint
    }

    private fun initIntent() {
        originModel = arguments?.getParcelable("config")
        position = arguments?.getInt("position")
        url = arguments?.getString("url")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ImagePreviewViewModel::class.java]


    }
}