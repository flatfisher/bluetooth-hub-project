package com.liferay.healthcareproject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by flatfisher on 9/25/15.
 */

public class CharacteristicFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater)getActivity()
                                         .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fragment_characteristic, null);

        builder.setView(view);

        return builder.create();

    }

    private void finish(){
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
