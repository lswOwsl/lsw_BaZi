package lsw;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lsw.library.R;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.ImageSourceImagesListener;
import lsw.utility.Image.ImageSelectListener;
import lsw.utility.Image.SourceFolder;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/11/2016.
 */
public class PhotoImagesFragment extends Fragment {
    private static final String ARG_IMAGE_SOURCE = "ARG_IMAGE_SOURCE";
    private static final String ARG_FOLDER = "ARG_FOLDER";

    private GridView mGridView;

    private PhotoImagesAdapter mPhotoImagesAdapter;
    private SourceFolder mSourceFolder;

    private ProgressBar mProgressBar;

    private Activity mActivity;

    TextView tvRemoveAll;

    private ImageSelectListener imageSelectListener;

    public void setImageSelectListener(ImageSelectListener imageSelectListener)
    {
        this.imageSelectListener = imageSelectListener;
    }

    public static PhotoImagesFragment createFragment(DeviceImageSource imageSource, SourceFolder folder) {
        PhotoImagesFragment f = new PhotoImagesFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_FOLDER, folder);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_photo_images, null);

        mGridView = (GridView)v.findViewById(R.id.gvImages);

        //TextView tvTitle = (TextView)v.findViewById(R.id.tvTitle);
        tvRemoveAll = (TextView)v.findViewById(R.id.tvRemoveAll);
        tvRemoveAll.setVisibility(View.GONE);

        mSourceFolder = (SourceFolder)getArguments().getSerializable(ARG_FOLDER);

        mProgressBar = (ProgressBar)v.findViewById(R.id.lp_progress_bar);

        mActivity = getActivity();

        configureView();

        return v;
    }

    private void configureView() {
        final DeviceImageSource imageSource = DeviceImageSource.getInstance();

        mPhotoImagesAdapter = new PhotoImagesAdapter(mActivity, imageSource, new ArrayList<SourceImage>());
        mGridView.setAdapter(mPhotoImagesAdapter);

        if (imageSource != null) {
            imageSource.fetchNextImagesForFolder(mActivity, mSourceFolder, true, new ImageSourceImagesListener() {
                @Override
                public void didComplete(SourceFolder folder, final List<SourceImage> images, Exception ex) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mPhotoImagesAdapter.clear();
                            for(SourceImage image : images)
                            {
                                mPhotoImagesAdapter.add(image);
                            }
                        }
                    });
                }
            });
        }

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPhotoImagesAdapter.getItemViewType(position) == 0) {
                    SourceImage image = mPhotoImagesAdapter.getItem(position);



                    return true;
                }

                return false;
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mPhotoImagesAdapter.getItemViewType(position) == 0) {

                    SourceImage image = mPhotoImagesAdapter.getItem(position);

                    if (imageSelectListener != null) {
                        imageSelectListener.invoke((ArrayList<SourceImage>) mPhotoImagesAdapter.getSourceImages(), position);
                    }

                    //mPhotoImagesAdapter.notifyDataSetChanged();
                } else {
                    // more
                    if (imageSource != null) {
                        imageSource.fetchNextImagesForFolder(mActivity, mSourceFolder, false, new ImageSourceImagesListener() {
                            @Override
                            public void didComplete(SourceFolder folder, final List<SourceImage> images, Exception ex) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPhotoImagesAdapter.addAll(images);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    public SourceFolder getSourceFolder() {
        return (SourceFolder)getArguments().getSerializable(ARG_FOLDER);
    }



}

