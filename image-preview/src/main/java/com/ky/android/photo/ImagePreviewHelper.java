package com.ky.android.photo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import com.ky.android.photo.bean.ImageModel;
import com.ky.android.photo.config.ContentViewOriginModel;
import com.ky.android.photo.config.ImagePreviewConfig;

import java.util.List;

import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

public class ImagePreviewHelper {

    private List<ImageModel> models;
    private ContentViewOriginModel originModel;
    private int position;


    public static ImagePreviewHelper with() {
        return new ImagePreviewHelper();
    }

    public static void init(Application app) {
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用ExoPlayer解码
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                .build());
    }


    public ImagePreviewHelper setModels(List<ImageModel> models) {
        this.models = models;
        return this;
    }

    public ImagePreviewHelper view(View view) {
        int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = view.getContext().getResources().getDisplayMetrics().heightPixels;
        originModel = new ContentViewOriginModel();
        if (view == null) {
            originModel.left = screenWidth / 2;
            originModel.top = screenHeight / 2;
            originModel.width = 0;
            originModel.height = 0;
        } else {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            originModel.left = location[0];
            originModel.top = location[1];
            originModel.width = view.getWidth();
            originModel.height = view.getHeight();
        }
        return this;
    }

    public void start(Context context) {
        ImagePreviewConfig config = new ImagePreviewConfig();
        config.setModels(models);
        config.setOriginModel(originModel);
        config.setPosition(position);
        ImagePreviewActivity.start(scanForActivity(context), config);
    }

    Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public ImagePreviewHelper position(int position) {
        this.position = position;
        return this;
    }
}
