package com.ky.android.photo;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import com.ky.android.photo.config.ContentViewOriginModel;
import com.ky.android.photo.config.ImagePreviewConfig;

public class ImagePreviewHelper {

    private String url;
    private ContentViewOriginModel originModel;


    public static ImagePreviewHelper with() {
        return new ImagePreviewHelper();
    }


    public ImagePreviewHelper url(String url) {
        this.url = url;
        return this;
    }

    public ImagePreviewHelper view(View view) {
        originModel = new ContentViewOriginModel();
        if (view == null) {
            originModel.left = 0;
            originModel.top = 0;
            originModel.width = 0;
            originModel.height = 0;
        } else {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            originModel.left = location[0];
            originModel.top = location[1];
//            originModel.width = view.getWidth();
//            originModel.height = view.getHeight();
        }
        return this;
    }

    public void start(Context context) {
        ImagePreviewConfig config = new ImagePreviewConfig();
        config.setImgUrl(url);
        config.setOriginModel(originModel);
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
}
