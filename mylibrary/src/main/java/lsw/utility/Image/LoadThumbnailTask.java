package lsw.utility.Image;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * Created by swli on 4/7/2016.
 */
public class LoadThumbnailTask extends AsyncTask<String, Void, Drawable> {
    private Context mContext;
    private SourceImage mImage;
    private ImageView mImageView;
    private LruCache mMemoryCache;

    public LoadThumbnailTask(Context context, SourceImage image, ImageView imageView, LruCache lruCache) {
        super();

        mContext = context;
        mImage = image;
        mImageView = imageView;
        mMemoryCache = lruCache;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        String idString = params[0];
        boolean isLongType = TextUtils.isDigitsOnly(params[0]);
        Bitmap bitmap = null;
        // check cache before load
        Cursor cursor = null;
        if (isLongType) {
            long id = Long.parseLong(params[0]);
            cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(mContext.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);

            String uri = null;


            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            }

            if (uri != null) {
                // caches
                bitmap = BitmapFactory.decodeFile(uri);
            } else {
                // fresh load
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }

        } else {
            //.replace("file://", "")
            Bitmap fullSizeImage = BitmapFactory.decodeFile(mImage.getFullUrl());
            //MINI_KIND: 512 x 384
            //MICRO_KIND: 96 x 96
            bitmap = ThumbnailUtils.extractThumbnail(fullSizeImage, 512, 384);
        }
        // add to cache
        try {
            mMemoryCache.put(idString, bitmap);
        } catch (Exception e) {
            Log.e("LP", "Could not load image cache", e);
        }

        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);

        return bitmapDrawable;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);

        if (mImage.getImageId().equals(mImageView.getTag())) {
            mImageView.setImageDrawable(drawable);
        }
    }
}
