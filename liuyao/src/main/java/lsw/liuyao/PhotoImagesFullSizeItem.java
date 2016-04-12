package lsw.liuyao;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.ImageSourceImageListener;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/12/2016.
 */
public class PhotoImagesFullSizeItem extends Fragment{

    private static final String ARG_IMAGE = "ARG_IMAGE";

    private SquareImageView mImageView;

    public static PhotoImagesFullSizeItem createFragment(SourceImage image) {
        PhotoImagesFullSizeItem f = new PhotoImagesFullSizeItem();
        Bundle args = new Bundle();

        args.putSerializable(ARG_IMAGE, image);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_photo_images_full_size_item, null);

        mImageView = (SquareImageView)v.findViewById(R.id.lp_img_image);
        //mImageView = (ImageView)v.findViewById(R.id.lp_img_image);

        configureView();

        return v;
    }

    private void configureView() {
        SourceImage image = (SourceImage)getArguments().getSerializable(ARG_IMAGE);
        DeviceImageSource imageSource = DeviceImageSource.getInstance();

        imageSource.fetchFullImageForImage(getActivity(), image, new ImageSourceImageListener() {
            @Override
            public void didComplete(SourceImage image, Drawable drawable, Exception ex) {
                mImageView.setImageDrawable(drawable);
            }
        });
    }

}
