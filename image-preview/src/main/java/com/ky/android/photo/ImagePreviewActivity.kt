package com.ky.android.photo

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ky.android.photo.config.ImagePreviewConfig
import com.ky.android.photo.widget.DragDiootoView
import me.panpf.sketch.Sketch
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.decode.ImageAttrs
import me.panpf.sketch.request.*
import java.security.AccessController.getContext

class ImagePreviewActivity : AppCompatActivity() {
    private var imageConfig: ImagePreviewConfig? = null
    private var dragDiootoView: DragDiootoView? = null
    private var sketchImageView: SketchImageView? = null
    private var visible = true;
    private var hasCache = false
    private var loadRequest: LoadRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        initIntent()
        initViews()
    }

    private fun initIntent() {
        imageConfig = intent.getParcelableExtra("config")
    }

    private fun initViews() {
        dragDiootoView = findViewById(R.id.dragDiootoView)
        sketchImageView = SketchImageView(this)
        sketchImageView?.options?.isDecodeGifImage = true
        sketchImageView?.isZoomEnabled = true
        dragDiootoView?.addContentChildView(sketchImageView)
        sketchImageView?.zoomer?.blockDisplayer?.setPause(visible)


        dragDiootoView?.setOnShowFinishListener { dragDiootoView, showImmediately -> loadImage() }
        dragDiootoView?.setOnDragListener { view, moveX, moveY -> }

        if (hasCache) {
            loadImage()
        } else {
            dragDiootoView?.putData(
                imageConfig!!.originModel!!.getLeft(),
                imageConfig!!.originModel!!.getTop(),
                imageConfig!!.originModel!!.getWidth(),
                imageConfig!!.originModel!!.getHeight()
            )
            //如果显示的点击的position  则进行动画处理
            dragDiootoView?.show(false)
        }

        dragDiootoView?.setOnFinishListener(object : DragDiootoView.OnFinishListener {
            override fun callFinish() {
                finish()
            }

        })
        dragDiootoView?.setOnReleaseListener(object : DragDiootoView.OnReleaseListener {
            override fun onRelease(isToMax: Boolean, isToMin: Boolean) {

            }

        })

        dragDiootoView?.setOnClickListener(object : DragDiootoView.OnClickListener {
            override fun onClick(dragDiootoView: DragDiootoView?) {
                dragDiootoView?.backToMin()
            }

        })

    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        this.visible = visible
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

    /**
     * 由于图片框架原因  这里需要使用两种不同的加载方式
     * 如果有缓存  直接可显示
     * 如果没缓存 则需要等待加载完毕  才能够将图片显示在view上
     */
    private fun loadImage() {
        if (getContext() == null || sketchImageView == null) {
            return
        }
        if (hasCache) {
            loadWithCache()
        } else {
            loadWithoutCache()
        }
    }

    private fun loadWithCache() {
        sketchImageView!!.displayListener = object : DisplayListener {
            override fun onStarted() {

            }

            override fun onCompleted(
                drawable: Drawable,
                imageFrom: ImageFrom,
                imageAttrs: ImageAttrs
            ) {
                val w = drawable.intrinsicWidth
                val h = drawable.intrinsicHeight
                //如果有缓存  直接将大小变为最终大小而不是去调用notifySize来更新 并且是直接显示不进行动画
                dragDiootoView!!.putData(
                    imageConfig!!.originModel!!.getLeft(), imageConfig!!.originModel!!.getTop(),
                    imageConfig!!.originModel!!.getWidth(), imageConfig!!.originModel!!.getHeight(),
                    w, h
                )
                dragDiootoView!!.show(false)
            }

            override fun onError(cause: ErrorCause) {

            }

            override fun onCanceled(cause: CancelCause) {}
        }
        sketchImageView!!.downloadProgressListener =
            DownloadProgressListener { totalLength, completedLength ->

            }
        imageConfig?.imgUrl?.let { sketchImageView?.displayImage(it) }
    }

    private fun loadWithoutCache() {
        loadRequest = imageConfig?.imgUrl?.let {
            Sketch.with(this).load(it, object : LoadListener {
                override fun onStarted() {

                }

                override fun onCompleted(result: LoadResult) {
                    if (result.gifDrawable != null) {
                        result.gifDrawable.followPageVisible(true, true)
                    }
                    val w = result.bitmap.width
                    val h = result.bitmap.height
                    dragDiootoView!!.notifySize(w, h)
                    sketchImageView!!.displayImage(imageConfig?.imgUrl!!)
                    hasCache = true
                }

                override fun onError(cause: ErrorCause) {

                }

                override fun onCanceled(cause: CancelCause) {}
            }).downloadProgressListener { totalLength, completedLength ->

            }.commit()
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}