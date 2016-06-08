package lsw.utility.Image;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by swli on 4/7/2016.
 */
public class DeviceImageSource {

    List<SourceFolder> mFolders = new ArrayList<SourceFolder>();

    List<SourceImage> mPhotos = new ArrayList<SourceImage>();

    private static final int MAX_CACHE_SIZE = 64000000;

    private HashMap<ImageView, LoadThumbnailTask> mThumbnailTasks;
    private LruCache<String,Bitmap> mMemoryCache;

    private static DeviceImageSource sInstance = null;

    public static synchronized DeviceImageSource getInstance() {
        if (sInstance == null) {
            sInstance = new DeviceImageSource();
        }

        return sInstance;
    }

    public DeviceImageSource() {

        mThumbnailTasks = new HashMap<ImageView, LoadThumbnailTask>();
        mMemoryCache = new LruCache(MAX_CACHE_SIZE);
    }

    public String getName(Context context) {
        return "Gallery";
    }

    public Drawable getIcon(Context context) {
        //return context.getResources().getDrawable();
        return null;
    }

    public void fetchNextFolders(Activity context, boolean reset, ImageSourceFoldersListener listener) {
        mFolders.clear();
        new LoadAlbumsTask(context, mFolders, listener).execute();

    }

    public void fetchNextImagesForFolder(Activity context, SourceFolder folder, boolean reset, ImageSourceImagesListener listener) {

        mPhotos.clear();
        new LoadImagesTask(context, folder, mPhotos, listener).execute();

    }

    public void fetchThumbnailForFolder(Context context, SourceFolder folder, ImageView imageView) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(folder.getThumbnailUrl(), opts);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);

        imageView.setImageDrawable(bitmapDrawable);
    }

    public void fetchThumbnailForImage(Context context, SourceImage image, ImageView imageView, boolean confirmDestination) {
        if (mThumbnailTasks.get(imageView) != null) {
            mThumbnailTasks.get(imageView).cancel(true);
        }

        Bitmap cachedBitmap = null;
        String id = image.getImageId();

        if(id != null) {
            cachedBitmap = mMemoryCache.get(id);
        }

        if (cachedBitmap != null) {
            // cached
            BitmapDrawable bd = new BitmapDrawable(context.getResources(), cachedBitmap);
            imageView.setImageDrawable(bd);
        } else {
            // not cached
            LoadThumbnailTask task = new LoadThumbnailTask(context, image, imageView,  mMemoryCache);
            mThumbnailTasks.put(imageView, task);

            task.execute(image.getImageId());
        }
    }

    public void fetchFullImageForImage(Context context, SourceImage image, ImageSourceImageListener listener) {
        String bucketFields[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
        };

        String path = image.getFullUrl();
        //path = path.replace("/", "");
        //path = path.replace("file:", "");
        Bitmap bitmap = BitmapFactory.decodeFile(path);


        if (bitmap != null) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);

            if (listener != null) {
                listener.didComplete(image, bitmapDrawable, null);
            }
        } else {
            if (listener != null) {
                listener.didComplete(image, null, new Exception("Couldn't load."));
            }
        }
    }
}
