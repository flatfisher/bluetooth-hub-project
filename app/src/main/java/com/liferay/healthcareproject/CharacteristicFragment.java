package com.liferay.healthcareproject;

import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.liferay.healthcareproject.bluetooth.BluetoothGattManager;
import com.liferay.healthcareproject.network.PostManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by flatfisher on 9/25/15.
 */

public class CharacteristicFragment extends BaseDialogFragment
        implements View.OnClickListener {

    private BluetoothGattCharacteristic bluetoothGattCharacteristic;

    private TextView uuidNameText;

    private TextView readResultText;

    private EditText writeEditText;

    private TextView notifyResultText;

    private Button readButton;

    private Button writeButton;

    private Button notifyButton;

    private TextView readProperty;

    private TextView writeProperty;

    private TextView notifyProperty;

    private LoggingTimerTask loggingTimerTask;

    private Timer timer;

    public final static String INTERVAL_10 = "10 sec.";

    public final static String INTERVAL_20 = "20 sec.";

    public final static String INTERVAL_30 = "30 sec.";

    private Spinner loggingIntervalSpinner;

    public interface CallBackToActivityListener {

        public void onReadSubmit(BluetoothGattCharacteristic characteristic);

        public void onWriteSubmit(BluetoothGattCharacteristic characteristic, byte[] value);

        public void onNotifySubmit(BluetoothGattCharacteristic characteristic, boolean enable);

    }

    private CallBackToActivityListener callBackToActivityListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        dialog.setContentView(R.layout.fragment_characteristic);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initializeLayout(dialog);

        setValuesOnLayout();

        return dialog;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof CallBackToActivityListener) {
            callBackToActivityListener = (CallBackToActivityListener) getActivity();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Dialog dialog = getDialog();

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int dialogWidth = (int) (metrics.widthPixels * 0.8);

        int dialogHeight = (int) (metrics.heightPixels * 0.8);

        layoutParams.width = dialogWidth;

        layoutParams.height = dialogHeight;

        dialog.getWindow().setAttributes(layoutParams);

    }

    private void initializeLayout(Dialog dialog) {

        uuidNameText = (TextView) dialog.findViewById(R.id.uuid_name_text);

        readResultText = (TextView) dialog.findViewById(R.id.read_text);

        writeEditText = (EditText) dialog.findViewById(R.id.writ_edit);

        notifyResultText = (TextView) dialog.findViewById(R.id.notify_text);

        readButton = (Button) dialog.findViewById(R.id.read_button);

        readButton.setOnClickListener(this);

        writeButton = (Button) dialog.findViewById(R.id.write_button);

        writeButton.setOnClickListener(this);

        notifyButton = (Button) dialog.findViewById(R.id.notify_button);

        notifyButton.setOnClickListener(this);

        readProperty = (TextView) dialog.findViewById(R.id.property_read);

        writeProperty = (TextView) dialog.findViewById(R.id.property_write);

        notifyProperty = (TextView) dialog.findViewById(R.id.property_notify);

        loggingIntervalSpinner = (Spinner) dialog.findViewById(R.id.logging_interval);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.read_button:
                readCharacteristic();
                break;

            case R.id.write_button:
                writeCharacteristic();
                break;

            case R.id.notify_button:
                notifyCharacteristic();
                break;
        }
    }

    private void readCharacteristic() {

        if (callBackToActivityListener != null) {

            callBackToActivityListener.onReadSubmit(bluetoothGattCharacteristic);

        }

    }

    private void writeCharacteristic() {

        String enterCode = writeEditText.getText().toString();

        if (enterCode != null) {

            byte[] value = hexStringToByteArray(enterCode);

            if (callBackToActivityListener != null) {

                callBackToActivityListener.onWriteSubmit(bluetoothGattCharacteristic, value);

            }

        }

    }

    private byte[] hexStringToByteArray(String s) {

        int len = s.length();

        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {

            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));

        }

        return data;

    }

    private void notifyCharacteristic() {

        boolean enable = true;

        String applyStatus = notifyButton.getText().toString();

        if (applyStatus.equals(getString(R.string.start_notification))) {

            enable = true;

            notifyButton.setText(getString(R.string.stop_notification));

        } else if (applyStatus.equals(getString(R.string.stop_notification))) {

            enable = false;

            notifyButton.setText(getString(R.string.start_notification));

        }

        if (callBackToActivityListener != null) {

            callBackToActivityListener.onNotifySubmit(bluetoothGattCharacteristic, enable);

            if (enable) {

                startLogging();

            } else {

                stopLogging();

            }

        }

    }

    private void startLogging() {

        String selectInterval = (String) loggingIntervalSpinner.getSelectedItem();

        if (selectInterval.equals(INTERVAL_10)) {

            setLoggingTimer(10000);

        } else if (selectInterval.equals(INTERVAL_20)) {

            setLoggingTimer(20000);

        } else if (selectInterval.equals(INTERVAL_30)) {

            setLoggingTimer(30000);

        }
    }

    private void setLoggingTimer(long delay) {

        loggingTimerTask = new LoggingTimerTask();

        timer = new Timer(true);

        timer.schedule(loggingTimerTask, delay, delay);

    }

    private void stopLogging() {

        if (timer != null) {

            timer.cancel();

            timer = null;

        }

    }

    @Override
    public void onBluetoothGattCharacteristic(BluetoothGattCharacteristic characteristic) {

        bluetoothGattCharacteristic = characteristic;

    }

    @Override
    public void onReadCharacteristicResult(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic, int status) {

        if (status == BluetoothGatt.GATT_SUCCESS) {

            byte[] resultData = characteristic.getValue();

            final StringBuilder stringBuilder = new StringBuilder(resultData.length);

            if (resultData != null && resultData.length > 0) {

                for (byte byteChar : resultData) {

                    stringBuilder.append(String.format("%02X", byteChar));

                }
            }

            HandlerManager.setText(readResultText, stringBuilder.toString());

        }

    }

    @Override
    public void onNotifyResult(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        byte[] resultData = characteristic.getValue();

        final StringBuilder stringBuilder = new StringBuilder(resultData.length);

        if (resultData != null && resultData.length > 0) {

            for (byte byteChar : resultData) {

                stringBuilder.append(String.format("%02X", byteChar));

            }
        }

        HandlerManager.setText(notifyResultText, stringBuilder.toString());

    }

    private void setValuesOnLayout() {

        String uuid = bluetoothGattCharacteristic.getUuid().toString();

        boolean readable = BluetoothGattManager
                .isCharacteristicReadable(bluetoothGattCharacteristic);

        boolean writable = BluetoothGattManager
                .isCharacteristicWritable(bluetoothGattCharacteristic);

        boolean notifiable = BluetoothGattManager
                .isCharacteristicNotifiable(bluetoothGattCharacteristic);

        uuidNameText.setText(uuid);

        setReadPropertyLayout(readable);

        setWritePropertyLayout(writable);

        setNotifyPropertyLayout(notifiable);

        setUpIntervalSpinner();

    }

    private void setReadPropertyLayout(boolean readable) {

        if (readable) {

            readProperty.setVisibility(View.VISIBLE);

        } else {

            readResultText.setEnabled(false);

            readButton.setEnabled(false);
        }
    }

    private void setWritePropertyLayout(boolean writable) {

        if (writable) {

            writeProperty.setVisibility(View.VISIBLE);

        } else {

            writeEditText.setEnabled(false);

            writeButton.setEnabled(false);

        }
    }

    private void setNotifyPropertyLayout(boolean notifiable) {

        if (notifiable) {

            notifyProperty.setVisibility(View.VISIBLE);

            loggingIntervalSpinner.setVisibility(View.VISIBLE);


        } else {

            notifyResultText.setEnabled(false);

            notifyButton.setEnabled(false);

        }
    }

    private void setUpIntervalSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.add("No log.");

        adapter.add(INTERVAL_10);

        adapter.add(INTERVAL_20);

        adapter.add(INTERVAL_30);

        loggingIntervalSpinner.setAdapter(adapter);

    }

    class LoggingTimerTask extends TimerTask implements PostManager.ResponseListener{

        private PostManager postManager;

        public LoggingTimerTask(){

            postManager =  new PostManager(getActivity(),Constants.REQUEST_URL,this);

        }

        @Override
        public void run() {

            loggingToServer();

        }

        @Override
        public Response.Listener<String> onResponse(String response) {
            return null;
        }

        @Override
        public Response.ErrorListener onErrorResponse() {
            return null;
        }

        private void loggingToServer(){

            Map<String,String> params = new HashMap<String, String>();

            String value = notifyResultText.getText().toString();

            if (value!=null){

                params.put("value",value);

                postManager.connect(params);

            }

        }

    }
}
