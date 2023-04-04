package com.ky.android.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.danikula.videocache.HttpProxyCacheServer
import com.ky.android.photo.config.ContentViewOriginModel
import com.ky.android.photo.databinding.FragmentVideoBinding
import xyz.doikki.videocontroller.StandardVideoController

class VideoFragment : Fragment() {

    private lateinit var _binding: FragmentVideoBinding

    private var url: String? = null
    private var position: Int? = null
    private var originModel: ContentViewOriginModel? = null

    companion object {
        fun newInstance() = VideoFragment()
    }

    private lateinit var viewModel: VideoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        initIntent()
        initViews()
        return _binding.root
    }

    private fun initViews() {
        _binding.player.setUrl(url) //设置视频地址
        val controller = context?.let { StandardVideoController(it) }
        controller?.addDefaultControlComponent("标题", false)
        _binding.player.setVideoController(controller) //设置控制器
        _binding.player.start() //开始播放，不调用则不自动播放

    }

    private fun initIntent() {
        originModel = arguments?.getParcelable("config")
        position = arguments?.getInt("position")
        url = arguments?.getString("url")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]

    }

    override fun onPause() {
        super.onPause()
        _binding.player.pause()
    }

    override fun onResume() {
        super.onResume()
        _binding.player.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding.player.release()
    }

}