package com.ky.android.photo

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.danikula.videocache.HttpProxyCacheServer
import com.ky.android.photo.config.ContentViewOriginModel
import com.ky.android.photo.databinding.FragmentVideoBinding
import com.ky.android.photo.util.cache.ProxyVideoCacheManager
import xyz.doikki.videocontroller.R
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.PrepareView

open class VideoFragment : Fragment() {

    private lateinit var _binding: FragmentVideoBinding

    private var url: String? = null
    private var thumb: String? = null
    private var position: Int? = null
    private var originModel: ContentViewOriginModel? = null
    private var isLoad = false;

    //X、Y的移动距离
    private var xDelta: Float? = 0f
    private var yDelta: Float? = 0f

    //X、Y的缩放比例
    private var mWidthScale: Float? = 0f
    private var mHeightScale: Float? = 0f
    private val DURATION = 250
    //背景色
    private var colorDrawable: ColorDrawable? = null
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
        _binding.dragLayout.setOnAlphaChangeListener {
            val colorId = convertPercentToBlackAlphaColor(it)
            _binding.dragLayout.setBackgroundColor(colorId)
        }
        _binding.dragLayout.setOnPageFinishListener {
            activity?.finish()
        }
        val controller = context?.let { StandardVideoController(it) }
        _binding.player.setVideoController(controller) //设置控制器
        val prepareView = activity?.let { PrepareView(it) } //准备播放界面
        prepareView?.setClickStart()
        val thumbView = prepareView?.findViewById<ImageView>(R.id.thumb) //封面图
        thumbView?.let { Glide.with(this).load(thumb).into(it) }
        controller?.addControlComponent(prepareView)
        _binding.player.setVideoController(controller)
        val cacheServer: HttpProxyCacheServer = ProxyVideoCacheManager.getProxy(activity)
        val proxyUrl = cacheServer.getProxyUrl(url)
        _binding.player.setUrl(proxyUrl) //设置视频地址

        //设置背景色，后面需要为其设置渐变动画
//        colorDrawable = activity?.let { ContextCompat.getColor(it, android.R.color.black) }?.let { ColorDrawable(it) }
        _binding.dragLayout.background = colorDrawable;
        val viewTreeObserver = _binding.player.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                _binding.player.viewTreeObserver.removeOnPreDrawListener(this);
                val screenLocation = IntArray(2)
                _binding.player.getLocationOnScreen(screenLocation)
                //动画需要移动的距离
                //动画需要移动的距离
                xDelta = originModel?.getLeft()?.minus(screenLocation[0])?.toFloat()
                yDelta = originModel?.getTop()?.minus(screenLocation[1])?.toFloat()
                //计算缩放比例
                //计算缩放比例
                mWidthScale = originModel?.getWidth()?.toFloat()?.div(_binding.player?.width)
                mHeightScale = originModel?.getHeight()?.toFloat()?.div(_binding.player?.height)
                enterAnimation {
                    //开始动画之后要做的操作
                }
                //返回 true 继续绘制，返回false取消。
                return true
            }
        });
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
        thumb = arguments?.getString("thumb")
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

    @SuppressLint("ObjectAnimatorBinding")
    private fun enterAnimation(enterAction: Runnable) {
        //放大动画
        _binding.player.pivotX = 0F
        _binding.player.pivotY = 0F
        mWidthScale?.let { _binding.player.scaleX = it }
        mHeightScale?.let { _binding.player.scaleY = it }
        xDelta?.let { _binding.player.translationX = it }
        yDelta?.let { _binding.player.translationY = it }
        val sDecelerator: TimeInterpolator = DecelerateInterpolator()
        _binding.player.animate().setDuration(DURATION.toLong()).scaleX(1F).scaleY(1F)
            .translationX(0F).translationY(0F).setInterpolator(sDecelerator)
            .withEndAction(enterAction)
        //设置背景渐变成你设置的颜色
        val bgAnim: ObjectAnimator = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255)
        bgAnim.duration = DURATION.toLong()
        bgAnim.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun exitAnimation(endAction: Runnable) {
        //缩小动画
        _binding.player.pivotX = 0F
        _binding.player.pivotY = 0F
        _binding.player.scaleX = 1F
        _binding.player.scaleY = 1F
        _binding.player.translationX = 0F
        _binding.player.translationY = 0F
        val sInterpolator: TimeInterpolator = AccelerateInterpolator()
        _binding.player.animate().setDuration(DURATION.toLong()).scaleX(mWidthScale!!)
            .scaleY(mHeightScale!!).translationX(xDelta!!).translationY(yDelta!!)
            .setInterpolator(sInterpolator).withEndAction(endAction)
        //设置背景渐透明
        val bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0)
        bgAnim.duration = DURATION.toLong()
        bgAnim.start()
    }
}