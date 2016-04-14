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
import android.widget.TextView;

import java.util.ArrayList;

import lsw.PhotoImagesAdapter;
import lsw.liuyao.data.Database;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.ImageSelectListener;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/13/2016.
 */
public class MenuPhotoImagesFragment extends Fragment {

    private static final String ARG_SOURCE_IMAGES = "ARG_SOURCE_IMAGES";

    private TextView tvTitle;
    private TextView tvRemoveAll;
    private GridView mGridView;
    private PhotoImagesAdapter mPhotoImagesAdapter;
    private Activity mActivity;

    private ProgressBar mProgressBar;

    private ImageSelectListener imageSelectListener;

    private static boolean isMenuUsing;

    public void setImageSelectListener(ImageSelectListener imageSelectListener)
    {
        this.imageSelectListener = imageSelectListener;
    }

    public static MenuPhotoImagesFragment createFragment(ArrayList<SourceImage> images, boolean isFromMenu) {

        isMenuUsing = isFromMenu;
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

        tvTitle = (TextView)v.findViewById(R.id.tvTitle);
        tvRemoveAll = (TextView)v.findViewById(lsw.library.R.id.tvRemoveAll);
        mActivity = getActivity();

        configureView();

        return v;
    }

    private View.OnClickListener onClickListener;
    public void setOnClickListener(View.OnClickListener clickListener)
    {
        onClickListener = clickListener;
    }

    private void configureView() {

        if(isMenuUsing)
        {
            tvTitle.setText("已选图片");
            tvRemoveAll.setVisibility(View.GONE);
        }
        else {
            tvRemoveAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPhotoImagesAdapter.clear();
                    mPhotoImagesAdapter.notifyDataSetChanged();
                    if (onClickListener != null)
                        onClickListener.onClick(view);
                }
            });
        }

        final DeviceImageSource imageSource = DeviceImageSource.getInstance();
        ArrayList<SourceImage> images = (ArrayList<SourceImage>)getArguments().getSerializable(ARG_SOURCE_IMAGES);
        mPhotoImagesAdapter = new PhotoImagesAdapter(mActivity, imageSource, images);
        mGridView.setAdapter(mPhotoImagesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (imageSelectListener != null) {
                    imageSelectListener.invoke((ArrayList<SourceImage>) mPhotoImagesAdapter.getSourceImages(), position);
                }
            }
        });
    }

}
