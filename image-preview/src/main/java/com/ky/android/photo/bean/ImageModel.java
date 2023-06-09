package com.ky.android.photo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.ky.android.photo.config.ImageType;

public class ImageModel implements Parcelable {

    private String url;
    private String thumb;
    private int type = ImageType.IMAGE;


    public ImageModel(String url, int type) {
        this.url = url;
        this.type = type;
    }

    public ImageModel(String url, String coverImgUrl, int type) {
        this.url = url;
        this.thumb = coverImgUrl;
        this.type = type;
    }

    protected ImageModel(Parcel in) {
        url = in.readString();
        thumb = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(thumb);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
