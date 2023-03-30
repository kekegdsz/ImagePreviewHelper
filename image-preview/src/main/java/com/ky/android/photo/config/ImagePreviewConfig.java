package com.ky.android.photo.config;

import android.os.Parcel;
import android.os.Parcelable;

public class ImagePreviewConfig implements Parcelable {

    private String imgUrl;

    private ContentViewOriginModel originModel;

    public ImagePreviewConfig() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ContentViewOriginModel getOriginModel() {
        return originModel;
    }

    public void setOriginModel(ContentViewOriginModel originModel) {
        this.originModel = originModel;
    }

    protected ImagePreviewConfig(Parcel in) {
        imgUrl = in.readString();
        originModel = in.readParcelable(ContentViewOriginModel.class.getClassLoader());
    }

    public static final Creator<ImagePreviewConfig> CREATOR = new Creator<ImagePreviewConfig>() {
        @Override
        public ImagePreviewConfig createFromParcel(Parcel in) {
            return new ImagePreviewConfig(in);
        }

        @Override
        public ImagePreviewConfig[] newArray(int size) {
            return new ImagePreviewConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imgUrl);
        parcel.writeParcelable(originModel, i);
    }
}
