package lsw.liuyao;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.ImageSourceFoldersListener;
import lsw.utility.Image.SourceFolder;

/**
 * Created by swli on 4/7/2016.
 */
public class PhotoAlbumsFragment extends Fragment {

    private static final String ARG_IMAGE_SOURCE = "ARG_IMAGE_SOURCE";

    private ListView mListView;

    private PhotoAlbumsAdapter mPhotoAlbumsAdapter;

    private ProgressBar mProgressBar;

    private Activity mActivity;

    public static PhotoAlbumsFragment createFragment() {
        PhotoAlbumsFragment f = new PhotoAlbumsFragment();
        Bundle args = new Bundle();

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_photo_albums, null);

        mListView = (ListView)v.findViewById(R.id.lvAlbums);

        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        mActivity = getActivity();

        configureView();

        setRetainInstance(true);

        return v;
    }

    private void configureView() {

        final DeviceImageSource imageSource = new DeviceImageSource();

        if (imageSource != null) {
            imageSource.fetchNextFolders(mActivity, true, new ImageSourceFoldersListener() {
                @Override
                public void didComplete(List<SourceFolder> folders, Exception ex) {

                    if (folders == null) {
                        folders = new ArrayList<SourceFolder>();
                    }

                    mPhotoAlbumsAdapter = new PhotoAlbumsAdapter(mActivity, imageSource, folders);
                    mListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mListView.setAdapter(mPhotoAlbumsAdapter);
                        }
                    });

                }
            });
        } else {
            mPhotoAlbumsAdapter = new PhotoAlbumsAdapter(mActivity, imageSource, new ArrayList<SourceFolder>());
            mListView.setAdapter(mPhotoAlbumsAdapter);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mPhotoAlbumsAdapter.getItemViewType(position) == 0) {
                    SourceFolder folder = (SourceFolder) mPhotoAlbumsAdapter.getItem(position);
//                    OrderActivity activity = (OrderActivity) mActivity;
//
//                    PhotoImagesFragment f = PhotoImagesFragment.createFragment(imageSource, folder);
//                    activity.pushFragment(f);
                } else {
                    // more
                    if (imageSource != null) {
                        imageSource.fetchNextFolders(mActivity, false, new ImageSourceFoldersListener() {
                            @Override
                            public void didComplete(final List<SourceFolder> folders, Exception ex) {
                                if (folders != null && ex == null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPhotoAlbumsAdapter.addAll(folders);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
