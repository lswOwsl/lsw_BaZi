package lsw;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lsw.library.R;
import lsw.utility.Image.Common;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/11/2016.
 */
public class PhotoImagesAdapter extends ArrayAdapter<SourceImage> {
    private LayoutInflater mInflater;
    private DeviceImageSource mImageSource;
    private List<SourceImage> mSourceImages;

    public PhotoImagesAdapter(Context context, DeviceImageSource imageSource, List<SourceImage> images) {
        super(context, 0, images);

        mInflater = LayoutInflater.from(context);
        mImageSource = imageSource;
        mSourceImages = images;
    }

    public PhotoImagesAdapter(Context context, DeviceImageSource imageSource, List<SourceImage> images, List<SourceImage> moreImages) {
        super(context, 0, images);

        mInflater = LayoutInflater.from(context);
        mImageSource = imageSource;
        mSourceImages = images;

        for (SourceImage iSourceImage : moreImages) {
            mSourceImages.add(iSourceImage);
        }
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(getItemViewType(position) == 0) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.common_photo_images_item, null);

                viewHolder = new ViewHolder();
                viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.lp_img_photo);
                viewHolder.iconImageMaskView = (ImageView) convertView.findViewById(R.id.lp_img_check_mask);
                viewHolder.checkLayout = (ViewGroup) convertView.findViewById(R.id.lp_layout_check);
                viewHolder.resourceId = R.drawable.image_select_mask;


                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SourceImage image = getItem(position);

            if (viewHolder.iconImageView.getTag() == null || !viewHolder.iconImageView.getTag().equals(image.getImageId())) {
                viewHolder.iconImageView.setTag(image.getImageId());
                //viewHolder.iconImageMaskView.setImageDrawable(Common.getDrawableSelect(getContext(),viewHolder.resourceId, R.color.color_for_draw));


                if (image.getThumbnailUrl() != null && !image.getThumbnailUrl().isEmpty()) {
                    Picasso.with(getContext()).load(image.getThumbnailUrl()).into(viewHolder.iconImageView);
                } else {
                    mImageSource.fetchThumbnailForImage(getContext(), image, viewHolder.iconImageView, true);
                }
            }


            viewHolder.checkLayout.setVisibility( View.GONE );
        }

        return  convertView;
    }

    public List<SourceImage> getSourceImages() {
        return mSourceImages;
    }

    static class ViewHolder {
        ImageView iconImageView;
        ImageView iconImageMaskView;
        ViewGroup checkLayout;
        int resourceId;
    }

}
