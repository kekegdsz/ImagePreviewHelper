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
import androidx.core.content.ContextCompat
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

        _binding.dragLayout.setOnAlphaChangeListener {
            val colorId = convertPercentToBlackAlphaColor(it)
            _binding.dragLayout.setBackgroundColor(colorId)
        }
        _binding.dragLayout.setOnPageFinishListener {
            activity?.finish()
        }
        _binding.photoView.setOnClickListener {
            exitAnimation{
                activity?.finish()
            }
        }
        activity?.let {
            Glide.with(it).load(url).into(_binding.photoView)
        }

        //设置背景色，后面需要为其设置渐变动画
        //设置背景色，后面需要为其设置渐变动画
        colorDrawable = activity?.let { ContextCompat.getColor(it, android.R.color.black) }?.let { ColorDrawable(it) }
        _binding.dragLayout.background = colorDrawable;
        val viewTreeObserver = _binding.photoView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                _binding.photoView.viewTreeObserver.removeOnPreDrawListener(this);
                val screenLocation = IntArray(2)
                _binding.photoView.getLocationOnScreen(screenLocation)
                //动画需要移动的距离
                //动画需要移动的距离
                xDelta = originModel?.getLeft()?.minus(screenLocation[0])?.toFloat()
                yDelta = originModel?.getTop()?.minus(screenLocation[1])?.toFloat()
                //计算缩放比例
                //计算缩放比例
                mWidthScale = originModel?.getWidth()?.toFloat()?.div(_binding.photoView?.width)
                mHeightScale = originModel?.getHeight()?.toFloat()?.div(_binding.photoView?.height)
                enterAnimation {
                    //开始动画之后要做的操作
                }
                //返回 true 继续绘制，返回false取消。
                return true
            }
        });
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun enterAnimation(enterAction: Runnable) {
        //放大动画
        _binding.photoView.pivotX = 0F
        _binding.photoView.pivotY = 0F
        mWidthScale?.let { _binding.photoView.scaleX = it }
        mHeightScale?.let { _binding.photoView.scaleY = it }
        xDelta?.let { _binding.photoView.translationX = it }
        yDelta?.let { _binding.photoView.translationY = it }
        val sDecelerator: TimeInterpolator = DecelerateInterpolator()
        _binding.photoView.animate().setDuration(DURATION.toLong()).scaleX(1F).scaleY(1F)
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
        _binding.photoView.pivotX = 0F
        _binding.photoView.pivotY = 0F
        _binding.photoView.scaleX = 1F
        _binding.photoView.scaleY = 1F
        _binding.photoView.translationX = 0F
        _binding.photoView.translationY = 0F
        val sInterpolator: TimeInterpolator = AccelerateInterpolator()
        _binding.photoView.animate().setDuration(DURATION.toLong()).scaleX(mWidthScale!!)
            .scaleY(mHeightScale!!).translationX(xDelta!!).translationY(yDelta!!)
            .setInterpolator(sInterpolator).withEndAction(endAction)
        //设置背景渐透明
        val bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0)
        bgAnim.duration = DURATION.toLong()
        bgAnim.start()
    }

    private fun convertPercentToBlackAlphaColor(alpha: Float): Int {
        var percent = Math.min(1f, Math.max(0f, alpha))
        val intAlpha = (percent * 255).toInt()
        val stringAlpha = Integer.toHexString(intAlpha).toLowerCase()
        val color = "#" + (if (stringAlpha.length < 2) "0" else "") + stringAlpha + "000000"
        return Color.parseColor(color)
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