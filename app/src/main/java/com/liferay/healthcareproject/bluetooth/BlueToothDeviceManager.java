package com.liferay.healthcareproject.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)

public class BlueToothDeviceManager implements BluetoothAdapter.LeScanCallback {

    private static final int SCAN_PERIOD = 10000;

    private Context context;

    private BlueToothDeviceListener onDeviceListener;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private Handler handler;

    private List<BluetoothDevice> deviceList;

    public BlueToothDeviceManager(Context context,BlueToothDeviceListener listener) {

        this.context = context;

        onDeviceListener = listener;

        handler = new Handler();

        deviceList = new ArrayList<BluetoothDevice>();

        setUpBlueTooth();

    }

    public void startScan() {

        if (isBlueToothEnable()) {
            scanBlueToothDevices(true);
        }

    }

    public void stopScan() {
        scanBlueToothDevices(false);
    }

    private void setUpBlueTooth() {

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

    }

    public boolean isBlueToothEnable() {

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

    }

    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            onDeviceListener.onResult(callbackType, result);

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private void scanBlueToothDevices(final boolean enable) {

        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopBlueToothDevices();
                }
            }, SCAN_PERIOD);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothLeScanner.startScan(scanCallback);
            } else {
                bluetoothAdapter.startLeScan(BlueToothDeviceManager.this);
            }

        } else {
            stopBlueToothDevices();
        }

    }

    private void stopBlueToothDevices(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner.stopScan(scanCallback);
        } else {
            bluetoothAdapter.stopLeScan(this);
        }

        if (onDeviceListener !=null){
            onDeviceListener.onFinish();
        }

    }
}
