package com.ky.android.photo.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.ky.android.photo.bean.ImageModel;

import java.util.List;

public class ImagePreviewConfig implements Parcelable {

    private List<ImageModel> models;

    private ContentViewOriginModel originModel;

    private int position;

    protected ImagePreviewConfig(Parcel in) {
        models = in.createTypedArrayList(ImageModel.CREATOR);
        originModel = in.readParcelable(ContentViewOriginModel.class.getClassLoader());
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(models);
        dest.writeParcelable(originModel, flags);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImagePreviewConfig() {
    }

    public ContentViewOriginModel getOriginModel() {
        return originModel;
    }

    public void setOriginModel(ContentViewOriginModel originModel) {
        this.originModel = originModel;
    }
    public List<ImageModel> getModels() {
        return models;
    }

    public void setModels(List<ImageModel> models) {
        this.models = models;
    }
}
