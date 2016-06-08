package lsw.utility.Image;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by swli on 4/7/2016.
 */
public class SourceImage implements Serializable, Parcelable {

    private String mImageId;
    private String mThumbnailUrl;
    private String mFullUrl;
    private int mFullImageWidth;
    private int mFullImageHeight;
    private SourceFolder mFolder;

    public SourceImage() {

    }

    @Override
    public boolean equals(Object rhs) {
        return getImageId().equals(((SourceImage)rhs).getImageId());
    }

    public String getImageId() {
        return mImageId;
    }

    public void setImageId(String imageId) {
        mImageId = imageId;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getFullUrl() {
        return mFullUrl;
    }

    public void setFullUrl(String fullUrl) {
        mFullUrl = fullUrl;
    }

    public int getFullImageWidth() {
        return mFullImageWidth;
    }

    public void setFullImageWidth(int fullImageWidth) {
        mFullImageWidth = fullImageWidth;
    }

    public int getFullImageHeight() {
        return mFullImageHeight;
    }

    public void setFullImageHeight(int fullImageHeight) {
        mFullImageHeight = fullImageHeight;
    }

    public SourceFolder getFolder() {
        return mFolder;
    }

    public void setFolder(SourceFolder folder) {
        mFolder = folder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mThumbnailUrl);
        dest.writeString(mFullUrl);
        dest.writeInt(mFullImageWidth);
        dest.writeInt(mFullImageHeight);
        dest.writeParcelable(mFolder, flags);
    }

    public SourceImage(Parcel in) {
        mImageId = in.readString();
        mThumbnailUrl = in.readString();
        mFullUrl = in.readString();
        mFullImageWidth = in.readInt();
        mFullImageHeight = in.readInt();
        mFolder = in.readParcelable(SourceFolder.class.getClassLoader());
    }

    public static final Parcelable.Creator<SourceImage> CREATOR = new Parcelable.Creator<SourceImage>() {
        public SourceImage createFromParcel(Parcel in) {
            return new SourceImage(in);
        }

        public SourceImage[] newArray(int size) {
            return new SourceImage[size];
        }
    };

    public SourceImage deepClone() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (SourceImage)oi.readObject();
        }
        catch (Exception ex)
        {
            Log.e("Clone SourceImage", ex.getMessage());
        }
        return null;
    }

}
