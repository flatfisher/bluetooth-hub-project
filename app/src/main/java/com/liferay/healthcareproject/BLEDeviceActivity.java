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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.liferay.healthcareproject.bluetooth.BluetoothGattManager;
import com.liferay.healthcareproject.bluetooth.BlueToothGattListener;
import com.liferay.healthcareproject.bluetooth.DeviceMethodObject;
import com.liferay.healthcareproject.bluetooth.UuidConstants;
import com.liferay.healthcareproject.bluetooth.UuidMethod;
import com.liferay.healthcareproject.bluetooth.UuidMethodManager;
import com.liferay.healthcareproject.network.GetManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BLEDeviceActivity extends BaseActivity
        implements BlueToothGattListener,
        View.OnClickListener,
        CharacteristicFragment.CallBackToActivityListener {

    private String deviceName;

    private TextView deviceNameText;

    private Spinner uuidMethodSpinner;

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

    private DeviceMethodObject deviceMethodObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bledevice);

        deviceNameText = (TextView) findViewById(R.id.device_name_text);

        uuidMethodSpinner = (Spinner) findViewById(R.id.uuid_method_spinner);

        uuidMethodButton = (Button) findViewById(R.id.uuid_method_button);

        uuidMethodButton.setOnClickListener(this);

        valueText = (TextView) findViewById(R.id.value_text);

        containerLayout = (LinearLayout) findViewById(R.id.container_linearLayout);

        setToolbar();

        deviceName = getIntent().getStringExtra(DEVICE_NAME_INTENT);

        deviceNameText.setText(deviceName);

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

        findUuidMethod();

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {

        if (characteristicFragment != null) {
            characteristicFragment.onReadCharacteristicResult(gatt, characteristic, status);
        }

        doUuidMethodForGattListener(characteristic);

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {

        if (characteristicFragment != null) {
            characteristicFragment.onNotifyResult(gatt, characteristic);
        }


        doUuidMethodForGattListener(characteristic);

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {

        doUuidMethodForGattListener(characteristic);

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

    private void findUuidMethod() {

        final String requestUrl = Constants.REQUEST_URL + Constants.UUID_METHOD_PATH;

        GetManager getManager = new GetManager(this, requestUrl) {

            @Override
            public void onResponse(JSONObject response) {
                deviceMethodObject = new DeviceMethodObject(response,deviceName);
                setUuidMethodToSpinner();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onErrorResponse", "" + error);
            }

        };
    }

    private void setUuidMethodToSpinner() {

        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        List<String> methods = deviceMethodObject.getMethodList();

        for (String method:methods){

            adapter.add(method);

        }

        uuidMethodSpinner.setAdapter(adapter);

        uuidMethodButton.setEnabled(true);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.uuid_method_button) {

            String selectedMethod = (String) uuidMethodSpinner.getSelectedItem();

            List<UuidMethod> uuidMethodList = deviceMethodObject.getUuidMethodList(selectedMethod);

            setUuidMethodManager(uuidMethodList);

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

    private void setUuidMethodManager(List<UuidMethod> uuidMethodList) {

        uuidMethodManager = new UuidMethodManager(uuidMethodList);

        doUuidMethodForFirst();

    }

    private void doUuidMethodForFirst() {
        switchUuidMethod(null);
    }

    private void doUuidMethodForGattListener(BluetoothGattCharacteristic characteristic) {
        switchUuidMethod(characteristic);
    }

    private void switchUuidMethod(BluetoothGattCharacteristic characteristic) {
        if (uuidMethodManager != null) {

            UuidMethod uuidMethod = uuidMethodManager.getUuidMethod();

            switch (uuidMethod.getMethodType()) {

                case UuidMethod.READ:
                    doUuidMethodRead(uuidMethod);
                    break;

                case UuidMethod.WRITE:
                    doUuidMethodWrite(uuidMethod);
                    break;

                case UuidMethod.NOTIFY:
                    doUuidMethodNotify(uuidMethod);
                    break;

                case UuidMethod.FINISH:
                    doUuidMethodFinish(characteristic);
                    break;
                default:
                    break;
            }

        }
    }


    private void doUuidMethodRead(UuidMethod uuidMethod) {

        String uuid = uuidMethod.getUuid();

        BluetoothGattCharacteristic characteristic = getCharacteristicFromCharacteristicList(uuid);

        bluetoothGattManager.readCharacteristic(characteristic);

    }

    private void doUuidMethodWrite(UuidMethod uuidMethod) {

        String uuid = uuidMethod.getUuid();

        String value = uuidMethod.getValue();

        BluetoothGattCharacteristic characteristic = getCharacteristicFromCharacteristicList(uuid);

        bluetoothGattManager.writeCharacteristic(characteristic, value);

    }

    private void doUuidMethodNotify(UuidMethod uuidMethod) {

        String uuid = uuidMethod.getUuid();

        BluetoothGattCharacteristic characteristic = getCharacteristicFromCharacteristicList(uuid);

        bluetoothGattManager.notifyCharacteristic(characteristic, true);

    }

    private void doUuidMethodFinish(BluetoothGattCharacteristic characteristic) {

        String value = BluetoothGattManager.getCharacteristicValue(characteristic);

        ViewThreadHandler.setText(valueText, value);

    }

    private BluetoothGattCharacteristic getCharacteristicFromCharacteristicList(String uuid) {
        int position = getUuidPosition(uuid);

        BluetoothGattCharacteristic characteristic = allCharacteristicList.get(position);

        return characteristic;
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