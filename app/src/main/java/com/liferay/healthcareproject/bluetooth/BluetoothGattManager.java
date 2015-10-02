package com.liferay.healthcareproject.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

public class BluetoothGattManager extends BluetoothGattCallback {

    private OnBlueToothGattListener onBlueToothGattListener;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;

    public BluetoothGattManager(BluetoothDevice bluetoothDevice,OnBlueToothGattListener listener) {
        super();

        this.bluetoothDevice = bluetoothDevice;

        this.onBlueToothGattListener = listener;

    }

    public void connectGatt(Context context, boolean autoConnect) {
        bluetoothDevice.connectGatt(context, autoConnect, this);
    }

    public void readCharacteristic(String serviceUuid, String characteristicUuid) {

        Log.i("readCharacteristic", "passed");

        if (bluetoothGatt != null) {

            BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUuid));

            if (service != null) {

                BluetoothGattCharacteristic characteristic = service
                                            .getCharacteristic(UUID.fromString(characteristicUuid));

                if (characteristic != null) {
                    bluetoothGatt.readCharacteristic(characteristic);
                }
            }
        }

    }

    public void writeCharacteristic(byte[] value, String serviceUuid, String characteristicUuid) {

        Log.i("writeCharacteristic", "passed");

        if (bluetoothGatt != null) {

            BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUuid));

            if (service != null) {

                BluetoothGattCharacteristic characteristic = service
                                            .getCharacteristic(UUID.fromString(characteristicUuid));

                if (characteristic != null) {
                    characteristic.setValue(value);
                    boolean status = bluetoothGatt.writeCharacteristic(characteristic);
                    Log.i("writeStatus", "" + status);
                }

            }
        }

    }

    public void notifyCharacteristic(String serviceUuid, String characteristicUuid, boolean enable) {

        Log.i("notifyCharacteristic", "passed");

        if (bluetoothGatt != null) {

            BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUuid));

            if (service != null) {

                BluetoothGattCharacteristic characteristic =
                        service.getCharacteristic(UUID.fromString(characteristicUuid));

                if (characteristic != null) {
                    boolean notify = bluetoothGatt
                                        .setCharacteristicNotification(characteristic, enable);

                    if (notify) {
                        for (BluetoothGattDescriptor bgd : characteristic.getDescriptors()) {

                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                    UUID.fromString(bgd.getUuid().toString()));

                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                            bluetoothGatt.writeDescriptor(descriptor);
                        }

                    } else {
                        Log.i("notify disable", "passed");
                    }
                }
            }
        }

    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        onBlueToothGattListener.onServicesDiscovered(gatt, status);
        bluetoothGatt = gatt;
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {

        onBlueToothGattListener.onCharacteristicRead(gatt, characteristic, status);

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);

        Log.i("onCharacteristicWrite", "passed");

        onBlueToothGattListener.onCharacteristicWrite(gatt, characteristic, status);

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        Log.i("onCharacteristicChanged", "passed");

        onBlueToothGattListener.onCharacteristicChanged(gatt, characteristic);

    }

}
