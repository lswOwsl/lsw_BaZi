package lsw;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lsw.library.R;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/12/2016.
 */
public class PhotoImagesFullSizeFragment extends Fragment {

    private static final String ARG_SOURCE_IMAGES = "ARG_SOURCE_IMAGES";
    private static final String ARG_START_INDEX = "ARG_START_INDEX";

    private ViewPager mViewPager;
    private PhotoImagesFullSizeAdapter mViewPageAdapter;

    public static PhotoImagesFullSizeFragment createFragment(ArrayList<SourceImage> images, int startIndex) {
        PhotoImagesFullSizeFragment f = new PhotoImagesFullSizeFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_SOURCE_IMAGES, images);
        args.putSerializable(ARG_START_INDEX, startIndex);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_photo_images_full_size, null);

        mViewPager = (ViewPager)v.findViewById(R.id.lp_vp_images);

        configureView();

        return v;
    }


    public void setCurrentFragmentManager(FragmentManager currentFragmentManager) {
        this.currentFragmentManager = currentFragmentManager;
    }

    private FragmentManager currentFragmentManager;

    private void configureView() {
        ArrayList<SourceImage> images = (ArrayList<SourceImage>)getArguments().getSerializable(ARG_SOURCE_IMAGES);
        int startIndex = getArguments().getInt(ARG_START_INDEX);

        mViewPageAdapter = new PhotoImagesFullSizeAdapter(currentFragmentManager, images);
        mViewPager.setAdapter(mViewPageAdapter);
        mViewPager.setCurrentItem(startIndex);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}

