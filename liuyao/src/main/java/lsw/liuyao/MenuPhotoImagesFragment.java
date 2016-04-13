package lsw.liuyao;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import lsw.PhotoImagesAdapter;
import lsw.liuyao.model.ImageAttachment;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.ImageSourceImagesListener;
import lsw.utility.Image.PushFragmentInterface;
import lsw.utility.Image.SourceFolder;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/13/2016.
 */
public class MenuPhotoImagesFragment extends Fragment {

    private static final String ARG_SOURCE_IMAGES = "ARG_SOURCE_IMAGES";

    private GridView mGridView;
    private PhotoImagesAdapter mPhotoImagesAdapter;
    private Activity mActivity;

    private ProgressBar mProgressBar;

    private PushFragmentInterface pushFragmentInterface;

    public void setPushFragmentInterface(PushFragmentInterface pushFragmentInterface)
    {
        this.pushFragmentInterface = pushFragmentInterface;
    }

    public static MenuPhotoImagesFragment createFragment(ArrayList<SourceImage> images) {
        MenuPhotoImagesFragment f = new MenuPhotoImagesFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_SOURCE_IMAGES, images);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(lsw.library.R.layout.common_photo_images, null);

        mGridView = (GridView)v.findViewById(lsw.library.R.id.gvImages);
        mProgressBar = (ProgressBar) v.findViewById(lsw.library.R.id.lp_progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mActivity = getActivity();

        configureView();

        return v;
    }

    private void configureView() {
        final DeviceImageSource imageSource = DeviceImageSource.getInstance();
        ArrayList<SourceImage> images = (ArrayList<SourceImage>)getArguments().getSerializable(ARG_SOURCE_IMAGES);
        mPhotoImagesAdapter = new PhotoImagesAdapter(mActivity, imageSource, images);
        mGridView.setAdapter(mPhotoImagesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (pushFragmentInterface != null) {
                    pushFragmentInterface.invoke((ArrayList<SourceImage>) mPhotoImagesAdapter.getSourceImages(), position);
                }
            }
        });
    }

}
