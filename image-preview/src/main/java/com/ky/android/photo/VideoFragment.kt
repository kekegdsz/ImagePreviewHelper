package com.ky.android.photo

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ky.android.photo.config.ContentViewOriginModel
import com.ky.android.photo.databinding.FragmentVideoBinding
import xyz.doikki.videocontroller.StandardVideoController

open class VideoFragment : Fragment() {

    private lateinit var _binding: FragmentVideoBinding

    private var url: String? = null
    private var position: Int? = null
    private var originModel: ContentViewOriginModel? = null
    private var isLoad = false;

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
        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            onUserVisibleChanged(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            onUserVisibleChanged(true)
        }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            onUserVisibleChanged(isVisibleToUser)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding.player.release()
    }


    private fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        // 不可见的时候暂停分块显示器，节省内存，可见的时候恢复
        if (isVisibleToUser) {
            if (!isLoad && isVisibleToUser()) {
                isLoad = true
                _binding.player.start()
            } else {
                _binding.player.resume()
            }
        } else {
            _binding.player.pause()
        }
    }
}