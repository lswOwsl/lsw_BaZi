package lsw.utility.Image;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 4/7/2016.
 */
public class LoadAlbumsTask extends AsyncTask<Void, Void, List<SourceFolder>>  {

    private Context context;
    private ImageSourceFoldersListener listener;
    private List<SourceFolder> mFolders;

    public LoadAlbumsTask(Context c, List<SourceFolder> folders, ImageSourceFoldersListener l) {
        context = c;
        listener = l;
        mFolders = folders;
    }

    @Override
    protected List<SourceFolder> doInBackground(Void... params) {
        String bucketFields[] = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,
                "COUNT(*)"
        };

        List<SourceFolder> folders = new ArrayList<SourceFolder>();
        Cursor imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, bucketFields, "1) GROUP BY 1,(2", null, "MAX(datetaken) DESC");

        if (imageCursor != null) {
            while (imageCursor.moveToNext()) {
                SourceFolder folder = new SourceFolder();

                folder.setName(imageCursor.getString(1));
                folder.setFolderId(imageCursor.getString(0));
                folder.setPhotoCount(imageCursor.getInt(3));
                folder.setThumbnailUrl(imageCursor.getString(2));
                folder.setFullUrl(imageCursor.getString(2));

                if (!folder.getName().equals("files")) {
                    folders.add(folder);
                }
            }
        }
        return folders;
    }

    @Override
    public void onPostExecute(List<SourceFolder> result) {
        mFolders.clear();
        mFolders.addAll(result);

        listener.didComplete(mFolders, null);
    }
}
