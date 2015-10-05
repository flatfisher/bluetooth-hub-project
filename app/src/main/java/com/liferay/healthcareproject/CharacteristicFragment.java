package com.liferay.healthcareproject;

import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liferay.healthcareproject.bluetooth.BluetoothGattManager;

/**
 * Created by flatfisher on 9/25/15.
 */

public class CharacteristicFragment extends BaseDialogFragment
        implements View.OnClickListener{

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

    public interface CallBackToActivityListener {
        public void onReadSubmit(BluetoothGattCharacteristic characteristic);
        public void onWriteSubmit(BluetoothGattCharacteristic characteristic,byte[] value);
        public void onNotifySubmit(BluetoothGattCharacteristic characteristic,boolean enable);
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
        if (getActivity() instanceof CallBackToActivityListener){
            callBackToActivityListener = (CallBackToActivityListener)getActivity();
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

        if (enterCode!=null) {

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

    private void notifyCharacteristic(){

        boolean enable = true;

        String applyStatus = notifyButton.getText().toString();

        if (applyStatus.equals(getString(R.string.start_notification))){

            enable = true;

            notifyButton.setText(getString(R.string.stop_notification));

        }else if(applyStatus.equals(getString(R.string.stop_notification))){

            enable = false;

            notifyButton.setText(getString(R.string.start_notification));

        }

        if (callBackToActivityListener != null) {

            callBackToActivityListener.onNotifySubmit(bluetoothGattCharacteristic,enable);

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

            String value = characteristic.
                    getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString();

            HandlerManager.setText(readResultText, value);

        }

    }

    @Override
    public void onNotifyResult(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            String value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString();

            HandlerManager.setText(notifyResultText, value);

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

        } else {

            notifyResultText.setEnabled(false);

            notifyButton.setEnabled(false);

        }
    }

}
