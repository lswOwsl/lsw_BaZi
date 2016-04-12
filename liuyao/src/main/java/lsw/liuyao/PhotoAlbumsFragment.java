package lsw.liuyao;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public interface PushFragmentInterface
    {
        void invoke(DeviceImageSource imageSource, SourceFolder sourceFolder);
    }

    private PushFragmentInterface pushFragmentInterface;

    public void setPushFragmentInterface(PushFragmentInterface pushFragmentInterface)
    {
        this.pushFragmentInterface = pushFragmentInterface;
    }

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

                    if(pushFragmentInterface != null)
                    {
                        pushFragmentInterface.invoke(imageSource,folder);
                    }
//                    PhotoImagesFragment f = PhotoImagesFragment.createFragment(imageSource, folder);
//                    FragmentTransaction ft = fragmentManager.beginTransaction();
//                    ft.replace(R.id.fl_Image_Select, f);
//                    ft.commit();

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
