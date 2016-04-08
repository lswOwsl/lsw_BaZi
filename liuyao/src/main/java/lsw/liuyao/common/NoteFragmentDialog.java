package lsw.liuyao.common;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

import lsw.liuyao.PhotoAlbumsFragment;
import lsw.liuyao.R;

/**
 * Created by swli on 4/8/2016.
 */
public class NoteFragmentDialog extends DialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View noteView = inflater.inflate(R.layout.common_hexagram_note, null);

        FragmentTransaction ft =  getChildFragmentManager().beginTransaction();
        PhotoAlbumsFragment photoAlbumsFragment = PhotoAlbumsFragment.createFragment();
        ft.replace(R.id.fl_Image_Select, photoAlbumsFragment, null);
        ft.commit();

        return noteView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View noteView = inflater.inflate(R.layout.common_hexagram_note, null);
        dialog.setContentView(noteView);
        final EditText etNote = (EditText)noteView.findViewById(R.id.editText);

        TextView tvSave = (TextView)noteView.findViewById(R.id.btnSaveNote);

        //etNote.setText(hexagramRow.getNote());

        final DialogFragment currentFragment = this;
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hexagramRow.setNote(etNote.getText().toString());
                //database.updateHexagram(hexagramRow);
                currentFragment.dismiss();
                Toast.makeText(getActivity(), "更新备注记录成功", Toast.LENGTH_SHORT).show();
            }
        });



        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window win = dialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        win.setLayout(dm.widthPixels / 3 * 2, dm.heightPixels / 3 * 2);

        return dialog;
    }
}
