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
import com.liferay.healthcareproject.bluetooth.BlueToothGattListener;
import com.liferay.healthcareproject.bluetooth.UuidConstants;
import com.liferay.healthcareproject.bluetooth.UuidMethod;
import com.liferay.healthcareproject.bluetooth.UuidMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class BLEDeviceActivity extends BaseActivity
        implements BlueToothGattListener,
        View.OnClickListener,
        CharacteristicFragment.CallBackToActivityListener {

    private TextView deviceNameText;

    private Button uuidMethodButton;

    private TextView valueText;

    private LinearLayout containerLayout;

    private Toolbar toolbar;

    private ProgressBar progressBar;

    private BluetoothDevice bluetoothDevice;

    private BluetoothGattManager bluetoothGattManager;

    private List<BluetoothGattCharacteristic> allCharacteristicList;

    private BaseDialogFragment characteristicFragment;

    private UuidMethodManager uuidMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bledevice);

        deviceNameText = (TextView) findViewById(R.id.device_name_text);

        uuidMethodButton = (Button) findViewById(R.id.uuid_method_button);

        uuidMethodButton.setOnClickListener(this);

        valueText = (TextView) findViewById(R.id.value_text);

        containerLayout = (LinearLayout) findViewById(R.id.container_linearLayout);

        setToolbar();

        String name = getIntent().getStringExtra(DEVICE_NAME_INTENT);

        deviceNameText.setText(name);

        String address = getIntent().getStringExtra(ADDRESS_INTENT);

        allCharacteristicList = new ArrayList<BluetoothGattCharacteristic>();

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

        if (characteristicFragment != null) {
            characteristicFragment.onReadCharacteristicResult(gatt, characteristic, status);
        }

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {

        if (characteristicFragment != null) {
            characteristicFragment.onNotifyResult(gatt, characteristic);
        }

        if (uuidMethodManager!=null){
            if (uuidMethodManager.isCheckFinish()){

                String value = BluetoothGattManager.getCharacteristicValue(characteristic);

                ViewThreadHandler.setText(valueText,value);
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {

        Log.i("onChara",""+uuidMethodManager.getUuidMethod().getMethodType());
        if (uuidMethodManager!=null){
            if (uuidMethodManager.isCheckFinish()){
                if (uuidMethodManager.getUuidMethod().getMethodType()==2){
                    bluetoothGattManager.notifyCharacteristic(allCharacteristicList.
                            get(uuidMethodManager.getUuidMethod().getMethodType()),true);
                }
            }
        }

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

            allCharacteristicList.add(characteristics.get(i));

        }

    }

    private void stopProgressBar() {

        ViewThreadHandler.setVisibility(progressBar, View.GONE);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.uuid_method_button) {

            doUuidMethod();

        } else {

            BluetoothGattCharacteristic characteristic =
                    (BluetoothGattCharacteristic) view.getTag();

            showCharacteristicDialog(characteristic);

        }

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
    public void onWriteSubmit(BluetoothGattCharacteristic characteristic, String value) {
        bluetoothGattManager.writeCharacteristic(characteristic, value);
    }

    @Override
    public void onNotifySubmit(BluetoothGattCharacteristic characteristic, boolean enable) {
        bluetoothGattManager.notifyCharacteristic(characteristic, enable);
    }

    private void doUuidMethod() {
        List<UuidMethod> uuidMethods = new ArrayList<UuidMethod>();
        UuidMethod uuidMethod = new UuidMethod();
        uuidMethod.setUuid(UuidConstants.ACC_ENABLE);
        uuidMethod.setMethodType(1);
        uuidMethod.setValue("05");
        uuidMethods.add(uuidMethod);

        UuidMethod uuidMethod2 = new UuidMethod();
        uuidMethod2.setUuid(UuidConstants.ACC_GEN_CFG);
        uuidMethod2.setMethodType(2);
        uuidMethod2.setValue("05");
        uuidMethods.add(uuidMethod2);

        uuidMethodManager = new UuidMethodManager(uuidMethods);
        if (!uuidMethodManager.isCheckFinish()) {
            if (uuidMethodManager.getUuidMethod().getMethodType() == 1) {
                bluetoothGattManager.writeCharacteristic(allCharacteristicList
                                .get(getUuidPosition(uuidMethod.getUuid())),
                        uuidMethodManager.getUuidMethod().getValue());
            }
        }
    }

    private Integer getUuidPosition(String uuid) {

        int size = allCharacteristicList.size();

        for (int i = 0; i < size; i++) {

            if (allCharacteristicList.get(i).getUuid().toString().equals(uuid)) {

                return i;
            }

        }

        return null;

    }

}