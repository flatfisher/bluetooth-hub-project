package com.liferay.healthcareproject;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v4.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {
    public abstract void onBluetoothGattCharacteristic(BluetoothGattCharacteristic characteristic);

}
