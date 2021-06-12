package com.example.ryan.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author devonwong
 */
public class UrlBean implements Parcelable {

    private String stringUrl;
    private byte[] byteUrl;
    private int flag;

    public UrlBean(String stringUrl){
        this.stringUrl = stringUrl;
        this.flag = 0;
    }

    public UrlBean(byte[] byteUrl){
        this.byteUrl = byteUrl;
        this.flag = 1;
    }

    protected UrlBean(Parcel in) {
        stringUrl = in.readString();
        byteUrl = in.createByteArray();
        flag = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stringUrl);
        dest.writeByteArray(byteUrl);
        dest.writeInt(flag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UrlBean> CREATOR = new Creator<UrlBean>() {
        @Override
        public UrlBean createFromParcel(Parcel in) {
            return new UrlBean(in);
        }

        @Override
        public UrlBean[] newArray(int size) {
            return new UrlBean[size];
        }
    };

    public String getStringUrl() {
        return stringUrl;
    }

    public void setStringUrl(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    public byte[] getByteUrl() {
        return byteUrl;
    }

    public void setByteUrl(byte[] byteUrl) {
        this.byteUrl = byteUrl;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
