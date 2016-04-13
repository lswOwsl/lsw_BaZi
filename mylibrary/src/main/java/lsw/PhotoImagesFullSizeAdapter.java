package lsw;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/12/2016.
 */
public class PhotoImagesFullSizeAdapter extends FragmentStatePagerAdapter {
    private List<SourceImage> mImages;

    public PhotoImagesFullSizeAdapter(FragmentManager fm, List<SourceImage> images) {
        super(fm);

        mImages = images;
    }

    @Override
    public Fragment getItem(int position) {
        PhotoImagesFullSizeItem f = PhotoImagesFullSizeItem.createFragment(mImages.get(position));
        return f;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

}
