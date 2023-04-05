package com.ky.android.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ky.android.photo.bean.ImageModel
import com.ky.android.photo.config.ImagePreviewConfig
import com.ky.android.photo.config.ImageType
import com.ky.android.photo.databinding.ActivityImagePreviewBinding
import com.ky.android.photo.util.FileUtils
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.kotlin.enqueue4
import com.liulishuo.okdownload.kotlin.enqueue4WithSpeed
import com.liulishuo.okdownload.kotlin.listener.onConnectStart
import com.tbruyelle.rxpermissions3.RxPermissions
import java.lang.Exception
import java.util.*


class ImagePreviewActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    private val TAG = "ImagePreviewActivity"

    private var _binding: ActivityImagePreviewBinding? = null
    private var imageConfig: ImagePreviewConfig? = null
    private var fragmentList: MutableList<Fragment>? = null
    private var currentPosition: Int? = 0
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
        val position: Int? = imageConfig?.position
        val models: MutableList<ImageModel>? = imageConfig?.models
        currentPosition = position
        fragmentList = mutableListOf()
        for (index in models?.indices!!) {
            val type = models[index].type
            if (type == ImageType.IMAGE) {
                val imageFragment: ImagePreviewFragment = ImagePreviewFragment.newInstance()
                imageFragment.arguments = Bundle().apply {
                    putParcelable("config", imageConfig?.originModel)
                    putString("url", models[index].url)
                    putBoolean("showAnim", index == position)
                    putString("thumb", models[index].thumb)
                }
                fragmentList?.add(imageFragment)
            } else if (type == ImageType.VIDEO) {
                val videoFragment: VideoFragment = VideoFragment.newInstance()
                videoFragment.arguments = Bundle().apply {
                    putParcelable("config", imageConfig?.originModel)
                    putString("url", models[index].url)
                    putBoolean("showAnim", index == position)
                    putString("thumb", models[index].thumb)
                }
                fragmentList?.add(videoFragment)
            }
        }
        _binding?.viewPager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList!![position]
            }

            override fun getCount(): Int {
                return fragmentList!!.size
            }
        }
        if (position != null) {
            _binding?.viewPager?.currentItem = position
        }
        _binding?.viewPager?.addOnPageChangeListener(this)
        _binding?.ivLeftClose?.setOnClickListener { finish() }
        _binding?.downloadImage?.setOnClickListener { downloadFile() }
    }

    private fun downloadFile() {
        val models = imageConfig?.models
        val model = currentPosition?.let { models?.get(it) }
        if (model?.type == ImageType.IMAGE) {
            downloadImage(model)
        } else if (model?.type == ImageType.VIDEO) {
            downloadVideo(model)
        }
    }

    private fun downloadVideo(model: ImageModel) {
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { granted ->
                if (granted) {
                    downloadOrignVideo(model)
                }
            }
    }

    private fun downloadImage(model: ImageModel) {
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { granted ->
                if (granted) {
                    downloadOrignImage(model)
                }
            }
    }

    private fun downloadOrignVideo(model: ImageModel) {
        val url = model.url
        val fileName = "${UUID.randomUUID()}.mp4"
        val file = FileUtils.getParentFile(this);
        val task = DownloadTask.Builder(url, file)
            .setFilename(fileName)
            .setMinIntervalMillisCallbackProcess(30)
            .setPassIfAlreadyCompleted(false)
            .build()
        task.enqueue(object : DownloadListener {
            override fun taskStart(task: DownloadTask) {
                Log.e(TAG, "taskStart")
            }

            override fun connectTrialStart(
                task: DownloadTask,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
                Log.e(TAG, "connectTrialStart")
            }

            override fun connectTrialEnd(
                task: DownloadTask,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
                Log.e(TAG, "connectTrialEnd")
            }

            override fun downloadFromBeginning(
                task: DownloadTask,
                info: BreakpointInfo,
                cause: ResumeFailedCause
            ) {
                Log.e(TAG, "downloadFromBeginning")
            }

            override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
                Log.e(TAG, "downloadFromBreakpoint")
            }

            override fun connectStart(
                task: DownloadTask,
                blockIndex: Int,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
                Log.e(TAG, "connectStart")
            }

            override fun connectEnd(
                task: DownloadTask,
                blockIndex: Int,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
                Log.e(TAG, "connectEnd")
            }

            override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
                Log.e(TAG, "fetchStart")
            }

            override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
                Log.e(TAG, "fetchProgress")
            }

            override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
                Log.e(TAG, "fetchEnd")
                val file = task.file
                task.tag = null
                Log.e(TAG, "download completed video")
                FileUtils.saveVideoToAlbum(file?.absolutePath, "", ".mp4")
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                Log.e(TAG, "taskEnd")
            }

        })
    }

    private fun downloadOrignImage(model: ImageModel) {
        val url = model.url
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = FileUtils.getParentFile(this);
        val task = DownloadTask.Builder(url, file)
            .setFilename(fileName)
            .setMinIntervalMillisCallbackProcess(30)
            .setPassIfAlreadyCompleted(false)
            .build()

        task?.enqueue4WithSpeed(
            onTaskStart = { },
            onConnectStart = { _, blockIndex, _ ->
                val status = "Connect End $blockIndex"
                Log.e(TAG, "Connect End status:$status")
            }
        ) { task, cause, realCause, taskSpeed ->
            // remove mark
            val file = task.file
            task.tag = null
            if (cause == EndCause.COMPLETED) {
                Log.e(TAG, "download completed")
                FileUtils.saveFileToAlbum(
                    this@ImagePreviewActivity,
                    file?.absolutePath,
                    file?.name,
                    FileUtils.getExtensionName(file?.name)
                )

            }
            realCause?.let {
                Log.e(TAG, "download error", it)
            }
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

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}