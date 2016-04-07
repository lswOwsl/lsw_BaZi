package lsw.utility.Image;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by swli on 4/7/2016.
 */
public class SourceFolder implements Serializable, Parcelable  {

    private String mFolderId;
    private String mName;
    private int mPhotoCount;
    private String updatedDate;
    private String thumbnailUrl;
    private String fullUrl;

    public SourceFolder() {

    }

    public String getFolderId() {
        return mFolderId;
    }

    public void setFolderId(String folderId) {
        mFolderId = folderId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPhotoCount() {
        return mPhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        mPhotoCount = photoCount;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFolderId);
        dest.writeString(mName);
        dest.writeInt(mPhotoCount);
        dest.writeString(updatedDate);
        dest.writeString(thumbnailUrl);
        dest.writeString(fullUrl);
    }

    public SourceFolder(Parcel in) {
        mFolderId = in.readString();
        mName = in.readString();
        mPhotoCount = in.readInt();
        updatedDate = in.readString();
        thumbnailUrl = in.readString();
        fullUrl = in.readString();
    }

    public static final Parcelable.Creator<SourceFolder> CREATOR = new Parcelable.Creator<SourceFolder>() {

        public SourceFolder createFromParcel(Parcel in) {
            return new SourceFolder(in);
        }

        public SourceFolder[] newArray(int size) {
            return new SourceFolder[size];
        }
    };
}
