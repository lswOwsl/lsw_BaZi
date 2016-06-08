package lsw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import lsw.library.R;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.SourceFolder;

/**
 * Created by swli on 4/7/2016.
 */
public class PhotoAlbumsAdapter extends ArrayAdapter<SourceFolder> {
    private LayoutInflater mInflater;
    private DeviceImageSource mImageSource;
    private List<SourceFolder> mSourceFolders;

    public PhotoAlbumsAdapter(Context context, DeviceImageSource imageSource, List<SourceFolder> folders) {
        super(context, 0, folders);

        mInflater = LayoutInflater.from(context);
        mSourceFolders = folders;
        mImageSource = imageSource;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == mSourceFolders.size()) {
            return 1;
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == 0) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.common_photo_albums_item,null);

                viewHolder = new ViewHolder();
                viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.lp_img_icon);
                viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.lp_txt_title);
                viewHolder.photoCountTextView = (TextView)convertView.findViewById(R.id.lp_txt_count);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SourceFolder folder = getItem(position);

            mImageSource.fetchThumbnailForFolder(getContext(), folder, viewHolder.iconImageView);
            viewHolder.titleTextView.setText(folder.getName());
            viewHolder.photoCountTextView.setText(String.valueOf(folder.getPhotoCount()));
        } else {
            if (convertView == null) {
                //convertView = mInflater.inflate(ResourceUtils.getLayout(getContext(), "lp_list_item_photo_folder_more"), null);
            }
        }
        return convertView;
    }

    public List<SourceFolder> getSourceFolders() {
        return mSourceFolders;
    }

    static class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView photoCountTextView;
    }
}

