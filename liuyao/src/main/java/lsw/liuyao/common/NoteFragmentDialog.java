package lsw.liuyao.common;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lsw.PhotoAlbumsFragment;
import lsw.PhotoImagesFragment;
import lsw.PhotoImagesFullSizeFragment;
import lsw.liuyao.R;
import lsw.liuyao.data.Database;
import lsw.liuyao.model.HexagramRow;
import lsw.liuyao.model.ImageAttachment;
import lsw.utility.FileHelper;
import lsw.utility.Image.AlbumSelectListener;
import lsw.utility.Image.Common;
import lsw.utility.Image.DeviceImageSource;
import lsw.utility.Image.ImageSelectListener;
import lsw.utility.Image.SourceFolder;
import lsw.utility.Image.SourceImage;

/**
 * Created by swli on 4/8/2016.
 */
public class NoteFragmentDialog extends DialogFragment {

    private static final String Param_Hexagram_Row = "param1";
    HexagramRow hexagramRow;
    private Database database;

    public interface OnSaveListener
    {
        void afterSave();
    }

    private OnSaveListener onSaveListener;

    public void setOnSaveListener(OnSaveListener saveListener)
    {
        onSaveListener = saveListener;
    }

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

    final List<SourceImage> selectedImages = new ArrayList<SourceImage>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View noteView = inflater.inflate(R.layout.common_hexagram_note, null);

        final EditText etNote = (EditText)noteView.findViewById(R.id.editText);

        TextView tvSave = (TextView)noteView.findViewById(R.id.btnSaveNote);
        TextView btnImageNote = (TextView)noteView.findViewById(R.id.btnImageNote);

        etNote.setText(hexagramRow.getNote());

        final DialogFragment currentFragment = this;

        final String dir = Environment.getExternalStorageDirectory() +"/"+
                MyApplication.getInstance().getResources().getString(R.string.externalSavingFolder);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hexagramRow.setNote(etNote.getText().toString());
                database.updateHexagram(hexagramRow);

                if(selectedImages.size() > 0)
                {
                    List<ImageAttachment> attachments = new ArrayList<ImageAttachment>();
                    for(SourceImage image : selectedImages) {

                        FileHelper.createFolder(dir);
                        String newFile = dir+"/"+hexagramRow.getDate()+"<"+hexagramRow.getOriginalName() +"|"+ hexagramRow.getChangedName()+">"+ selectedImages.indexOf(image) + ".jpg";
                        try {
                            copy(new File(image.getFullUrl()),new File(newFile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ImageAttachment model = new ImageAttachment(hexagramRow.getId(),newFile);
                        attachments.add(model);
                    }
                    database.insertImageAttachment(attachments);
                }

                currentFragment.dismiss();

                if(onSaveListener !=null)
                    onSaveListener.afterSave();

                Toast.makeText(getActivity(), "更新备注记录成功", Toast.LENGTH_SHORT).show();
            }
        });

        btnImageNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedImages.clear();

                //album list
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                PhotoAlbumsFragment photoAlbumsFragment = PhotoAlbumsFragment.createFragment();
                photoAlbumsFragment.setPushFragmentInterface(new AlbumSelectListener() {
                    @Override
                    public void invoke(DeviceImageSource imageSource, SourceFolder sourceFolder) {
                        //image gridview
                        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                        PhotoImagesFragment f = PhotoImagesFragment.createFragment(imageSource, sourceFolder);
                        f.setImageSelectListener(new ImageSelectListener() {
                            @Override
                            public void invoke(ArrayList<SourceImage> sourceImages, int index) {
                                //full image view
                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                PhotoImagesFullSizeFragment f = PhotoImagesFullSizeFragment.createFragment(sourceImages, index);
                                f.setCurrentFragmentManager(getChildFragmentManager());
                                f.setImageClickListener(new PhotoImagesFullSizeFragment.OnImageClickListener() {
                                    @Override
                                    public void onClick(View view, SourceImage image) {
                                        ImageView imageView = (ImageView) view.findViewById(lsw.library.R.id.lp_img_check_mask);
                                        ImageView imageViewDefault = (ImageView) view.findViewById(lsw.library.R.id.lp_img_check_default);

                                        if (selectedImages.contains(image)) {
                                            selectedImages.remove(image);
                                            imageViewDefault.setBackgroundColor(getResources().getColor(lsw.library.R.color.gray_light));
                                        } else {
                                            selectedImages.add(image);
                                            imageView.setImageDrawable(Common.getDrawableSelect(getActivity(), lsw.library.R.drawable.image_select_mask, lsw.library.R.color.color_for_draw));
                                            imageViewDefault.setBackgroundColor(getResources().getColor(lsw.library.R.color.transparent));
                                        }
                                    }
                                });

                                ft.replace(R.id.fl_Image_Select, f);
                                ft.commit();
                            }
                        });

                        ft.replace(R.id.fl_Image_Select, f);
                        ft.commit();
                    }
                });

                ft.replace(R.id.fl_Image_Select, photoAlbumsFragment, null);
                ft.commit();

            }
        });

        return noteView;
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
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
