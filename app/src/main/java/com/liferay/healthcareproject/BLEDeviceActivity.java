package com.liferay.healthcareproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liferay.healthcareproject.bluetooth.BluetoothGattManager;
import com.liferay.healthcareproject.bluetooth.OnBlueToothGattListener;

import java.util.List;

public class BLEDeviceActivity extends BaseActivity
        implements OnBlueToothGattListener,
                    View.OnClickListener{

    private LinearLayout containerLayout;

    private Toolbar toolbar;

    private ProgressBar progressBar;

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

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

        bluetoothGattManager = new BluetoothGattManager(bluetoothDevice, this);

        bluetoothGattManager.connectGatt(this, false);

        showProgressBar();

    }

    private void showProgressBar(){
        HandlerManager.setVisibility(progressBar, View.VISIBLE);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        Log.i("onServicesDiscovered", "passed");

        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService bgs : services) {

            addUuid(bgs.getUuid().toString(), bgs.getCharacteristics());

        }

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {

    }

    private void addUuid(String uuid, List<BluetoothGattCharacteristic> characteristics) {

        setServiceUuid(uuid);

        setCharacteristicUuid(characteristics);

    }

    private void setServiceUuid(String uuid) {

        TextView textView = new TextView(this);

        textView.setText(uuid);

        HandlerManager.addView(containerLayout, textView);

    }

    private void setCharacteristicUuid(List<BluetoothGattCharacteristic> characteristics) {

        for (BluetoothGattCharacteristic characteristic : characteristics) {

            Button button = new Button(this);

            characteristic.getPermissions();

            String uuid = characteristic.getUuid().toString();

            button.setText(uuid);

            button.setTag(characteristic);

            button.setOnClickListener(this);

            HandlerManager.addView(containerLayout, button);

        }

        stopProgressBar();

    }

    private void stopProgressBar(){
        HandlerManager.setVisibility(progressBar, View.GONE);
    }

    @Override
    public void onClick(View view) {

        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic)view.getTag();

        showCharacteristicDialog(characteristic);

    }

    private void showCharacteristicDialog(BluetoothGattCharacteristic characteristic) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        characteristicFragment = new CharacteristicFragment();

        characteristicFragment.show(fragmentManager, Constants.CHARACTERISTIC_DIALOG);

        characteristicFragment.onBluetoothGattCharacteristic(characteristic);

    }

}