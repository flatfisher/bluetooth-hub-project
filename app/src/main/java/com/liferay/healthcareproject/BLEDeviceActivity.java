package com.liferay.healthcareproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.liferay.healthcareproject.bluetooth.BluetoothGattManager;
import com.liferay.healthcareproject.bluetooth.BlueToothGattListener;

import java.util.List;

public class BLEDeviceActivity extends BaseActivity
        implements BlueToothGattListener,
        View.OnClickListener,
        CharacteristicFragment.CallBackToActivityListener {

    private LinearLayout containerLayout;

    private Toolbar toolbar;

    private ProgressBar progressBar;

    private BluetoothDevice bluetoothDevice;

    private BluetoothGattManager bluetoothGattManager;

    private BaseDialogFragment characteristicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bledevice);

        containerLayout = (LinearLayout) findViewById(R.id.container_linearLayout);

        setToolbar();

        String address = getIntent().getStringExtra(ADDRESS_INTENT);

        connect(address);
    }

    private void setToolbar() {

        progressBar = new ProgressBar(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        toolbar.addView(progressBar);

        progressBar.setVisibility(View.GONE);

    }

    private void connect(String address) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

        bluetoothGattManager = new BluetoothGattManager(bluetoothDevice, this);

        bluetoothGattManager.connectGatt(this, false);

        showProgressBar();

    }

    private void showProgressBar() {
        ViewThreadHandler.setVisibility(progressBar, View.VISIBLE);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        List<BluetoothGattService> services = gatt.getServices();

        int size = services.size();

        for (int i = 0; i < size; i++) {

            addCharacteristicUuid(services.get(i).getCharacteristics());

        }

        stopProgressBar();

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {

        characteristicFragment.onReadCharacteristicResult(gatt, characteristic, status);

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {

        characteristicFragment.onNotifyResult(gatt, characteristic);

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {

    }

    private void addCharacteristicUuid(List<BluetoothGattCharacteristic> characteristics) {

        int size = characteristics.size();

        for (int i = 0; i < size; i++) {

            Button button = new Button(this);

            characteristics.get(i).getPermissions();

            String uuid = characteristics.get(i).getUuid().toString();

            button.setText(uuid);

            button.setTag(characteristics.get(i));

            button.setOnClickListener(this);

            ViewThreadHandler.addView(containerLayout, button);

        }

    }

    private void stopProgressBar() {
        ViewThreadHandler.setVisibility(progressBar, View.GONE);
    }

    @Override
    public void onClick(View view) {

        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) view.getTag();

        showCharacteristicDialog(characteristic);

    }

    private void showCharacteristicDialog(BluetoothGattCharacteristic characteristic) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        characteristicFragment = new CharacteristicFragment();

        characteristicFragment.show(fragmentManager, Constants.CHARACTERISTIC_DIALOG);

        characteristicFragment.onBluetoothGattCharacteristic(characteristic);

    }

    @Override
    public void onReadSubmit(BluetoothGattCharacteristic characteristic) {
        bluetoothGattManager.readCharacteristic(characteristic);
    }

    @Override
    public void onWriteSubmit(BluetoothGattCharacteristic characteristic, byte[] value) {
        bluetoothGattManager.writeCharacteristic(characteristic, value);
    }

    @Override
    public void onNotifySubmit(BluetoothGattCharacteristic characteristic, boolean enable) {
        bluetoothGattManager.notifyCharacteristic(characteristic, enable);
    }
}