package lsw;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/12/2016.
 */
public class PhotoImagesFullSizeAdapter extends FragmentStatePagerAdapter {
    private List<SourceImage> mImages;

    public void setImageClickListener(PhotoImagesFullSizeFragment.OnImageClickListener listener)
    {
        onClickListener = listener;
    }

    private PhotoImagesFullSizeFragment.OnImageClickListener onClickListener;

    public PhotoImagesFullSizeAdapter(FragmentManager fm, List<SourceImage> images) {
        super(fm);

        mImages = images;
    }

    @Override
    public Fragment getItem(int position) {
        PhotoImagesFullSizeItem f = PhotoImagesFullSizeItem.createFragment(mImages.get(position));
        f.setImageClickListener(onClickListener);
        return f;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

}
