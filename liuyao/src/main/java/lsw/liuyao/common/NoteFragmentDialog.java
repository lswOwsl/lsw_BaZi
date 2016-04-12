package lsw.liuyao.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lsw.library.DateExt;
import lsw.liuyao.PhotoAlbumsFragment;
import lsw.liuyao.PhotoImagesFragment;
import lsw.liuyao.PhotoImagesFullSizeFragment;
import lsw.liuyao.R;
import lsw.liuyao.data.Database;
import lsw.liuyao.model.HexagramRow;
import lsw.model.Hexagram;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.SourceFolder;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/8/2016.
 */
public class NoteFragmentDialog extends DialogFragment {

    private static final String Param_Hexagram_Row = "param1";
    HexagramRow hexagramRow;
    private Database database;

    public static NoteFragmentDialog newInstance(HexagramRow hexagramRow) {

        NoteFragmentDialog fragment = new NoteFragmentDialog();
        Bundle args = new Bundle();
        args.putSerializable(Param_Hexagram_Row, hexagramRow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hexagramRow = (HexagramRow)getArguments().getSerializable(Param_Hexagram_Row);
        }

        database = new Database(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View noteView = inflater.inflate(R.layout.common_hexagram_note, null);

        final EditText etNote = (EditText)noteView.findViewById(R.id.editText);

        TextView tvSave = (TextView)noteView.findViewById(R.id.btnSaveNote);
        TextView btnImageNote = (TextView)noteView.findViewById(R.id.btnImageNote);

        etNote.setText(hexagramRow.getNote());

        final DialogFragment currentFragment = this;

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hexagramRow.setNote(etNote.getText().toString());
                database.updateHexagram(hexagramRow);
                currentFragment.dismiss();
                Toast.makeText(getActivity(), "更新备注记录成功", Toast.LENGTH_SHORT).show();
            }
        });


        btnImageNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                PhotoAlbumsFragment photoAlbumsFragment = PhotoAlbumsFragment.createFragment();

                photoAlbumsFragment.setPushFragmentInterface(new PhotoAlbumsFragment.PushFragmentInterface() {
                    @Override
                    public void invoke(DeviceImageSource imageSource, SourceFolder sourceFolder) {
                        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                        PhotoImagesFragment f = PhotoImagesFragment.createFragment(imageSource, sourceFolder);

                        f.setPushFragmentInterface(new PhotoImagesFragment.PushFragmentInterface() {
                            @Override
                            public void invoke(ArrayList<SourceImage> sourceImages, int index) {

                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                PhotoImagesFullSizeFragment f = PhotoImagesFullSizeFragment.createFragment(sourceImages, index);
                                f.setCurrentFragmentManager(getChildFragmentManager());

                                ft.replace(R.id.fl_Image_Select, f);
                                ft.commit();
                            }
                        });

                        ft.replace(R.id.fl_Image_Select, f);
                        ft.commit();
                    }
                });

                // quantify
//                FullSizePhotoImagesFragment f = FullSizePhotoImagesFragment.createFragment((ArrayList<SourceImage>) mPhotoImagesAdapter.getSourceImages(), position);
//                ((OrderActivity) mActivity).pushFragment(f);
//                ((OrderActivity) mActivity).showActionBar(false);




                ft.replace(R.id.fl_Image_Select, photoAlbumsFragment, null);
                ft.commit();

            }
        });

        return noteView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View noteView = inflater.inflate(R.layout.common_hexagram_note, null);
        dialog.setContentView(noteView);

        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window win = dialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        win.setLayout(dm.widthPixels / 3 * 2, dm.heightPixels / 6 * 5);

        return dialog;
    }
}
