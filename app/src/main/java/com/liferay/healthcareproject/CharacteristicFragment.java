package com.liferay.healthcareproject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by flatfisher on 9/25/15.
 */

public class CharacteristicFragment extends DialogFragment {

    private TextView uuidNameText;

    private TextView readResultText;

    private EditText writeEditText;

    private TextView notifyResultText;

    private Button readButton;

    private Button writeButton;

    private Button notifyButton;

    private TextView readProperty;

    private TextView writeProperty;

    private TextView notifyProperty;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_characteristic);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setViewById(dialog);

        return dialog;

    }

    private void setViewById(Dialog dialog){

        uuidNameText = (TextView)dialog.findViewById(R.id.uuid_name_text);

        readResultText = (TextView)dialog.findViewById(R.id.read_text);

        writeEditText = (EditText)dialog.findViewById(R.id.writ_edit);

        notifyResultText = (TextView)dialog.findViewById(R.id.notify_text);

        readButton = (Button)dialog.findViewById(R.id.read_button);

        writeButton = (Button)dialog.findViewById(R.id.write_button);

        readProperty = (TextView)dialog.findViewById(R.id.property_read);

        writeProperty = (TextView)dialog.findViewById(R.id.property_write);

        notifyProperty = (TextView)dialog.findViewById(R.id.property_notify);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Dialog dialog = getDialog();

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int dialogWidth = (int) (metrics.widthPixels * 0.8);

        int dialogHeight = (int) (metrics.heightPixels * 0.8);

        layoutParams.width = dialogWidth;

        layoutParams.height = dialogHeight;

        dialog.getWindow().setAttributes(layoutParams);

    }
    
}
