package com.liferay.healthcareproject;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v4.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {

    public abstract void onBluetoothGattCharacteristic(
                        BluetoothGattCharacteristic characteristic);

    public abstract void onReadCharacteristicResult(
                        BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    public abstract void onNotifyResult(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic);

}
