package com.ky.android.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ky.android.photo.config.ContentViewOriginModel
import com.ky.android.photo.databinding.FragmentImagePreviewBinding
import me.panpf.sketch.request.DisplayRequest

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