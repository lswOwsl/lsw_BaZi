package lsw.utility.Image;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 4/7/2016.
 */
public class LoadImagesTask extends AsyncTask<Void, Void, List<SourceImage>> {

    private Context context;
    private ImageSourceImagesListener listener;
    private SourceFolder folder;
    private List<SourceImage> mPhotos;

    public LoadImagesTask(Context c, SourceFolder f, List<SourceImage> photos, ImageSourceImagesListener l) {
        context = c;
        listener = l;
        folder = f;
        mPhotos = photos;
    }

    @Override
    protected List<SourceImage> doInBackground(Void... params) {
        String bucketFields[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
        };

        String sortOrder = String.format("%s DESC", MediaStore.Images.Media.DATE_TAKEN);

        List<SourceImage> images = new ArrayList<SourceImage>();
        Cursor imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, bucketFields, "bucket_id = " + folder.getFolderId(), null, sortOrder);

        while (imageCursor.moveToNext()) {
            SourceImage image = new SourceImage();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(imageCursor.getString(1), options);

            //image.setImageSourceType(getType());
            image.setImageId(imageCursor.getString(0));
            image.setFullUrl(imageCursor.getString(1));
            image.setFullImageWidth(options.outWidth);
            image.setFullImageHeight(options.outHeight);

            images.add(image);
        }

        return images;
    }

    @Override
    public void onPostExecute(List<SourceImage> result) {

        mPhotos.clear();
        mPhotos.addAll(result);
        listener.didComplete(folder, mPhotos, null);
    }

}
