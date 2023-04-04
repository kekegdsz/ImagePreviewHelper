package com.kekegdsz.img.preview;

import android.app.Application;

import com.ky.android.photo.ImagePreviewHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImagePreviewHelper.init(this);
    }
}
