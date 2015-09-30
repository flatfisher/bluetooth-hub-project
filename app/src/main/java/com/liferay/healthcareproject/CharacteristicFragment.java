package com.liferay.healthcareproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by flatfisher on 9/25/15.
 */
public class CharacteristicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristic, container, false);
        setOnKeyListener(view);
        return view;
    }

    private void setOnKeyListener(View view) {
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void finish(){
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
