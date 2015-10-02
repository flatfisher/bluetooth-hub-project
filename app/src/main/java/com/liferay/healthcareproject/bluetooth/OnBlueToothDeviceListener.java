package com.liferay.healthcareproject.bluetooth;

import android.bluetooth.le.ScanResult;


/**
 * Created by flatfisher on 9/21/15.
 */

public interface OnBlueToothDeviceListener {

    public void onResult(int callbackType, ScanResult result);

    public void onFinish();

}
