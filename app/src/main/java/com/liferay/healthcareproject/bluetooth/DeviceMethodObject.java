package com.liferay.healthcareproject.bluetooth;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flatfisher on 10/7/15.
 */
public class DeviceMethodObject {

    private JSONObject uuidMethodObject;

    private String deviceName;

    private List<String> deviceMethodList;

    private List<UuidMethod> uuidMethodList;

    private JSONArray methodJsonArray;

    public DeviceMethodObject(JSONObject uuidMethodObject, String deviceName) {

        this.uuidMethodObject = uuidMethodObject;

        this.deviceName = deviceName;

        deviceMethodList = new ArrayList<String>();

        uuidMethodList = new ArrayList<UuidMethod>();

        parseJsonObject();

    }

    public List<String> getMethodList() {
        return deviceMethodList;
    }

    public List<UuidMethod> getUuidMethodList(String methodName) {

        int length = methodJsonArray.length();

        List<UuidMethod> uuidMethodList = new ArrayList<UuidMethod>();

        for (int i = 0; i < length; i++) {
            try {

               if(methodJsonArray.getJSONObject(i).getString("name").equals(methodName)){

                   JSONArray process = methodJsonArray.getJSONObject(i).getJSONArray("process");

                   int processMaxValue = process.length();

                   for (int j = 0; j < processMaxValue;j++){

                       UuidMethod uuidMethod = new UuidMethod();

                       uuidMethod.setMethodType(process.getJSONObject(j).getInt("method"));

                       uuidMethod.setUuid(process.getJSONObject(j).getString("uuid"));

                       uuidMethod.setValue(process.getJSONObject(j).getString("value"));

                       uuidMethodList.add(uuidMethod);

                   }

               }

            } catch (JSONException jsonException) {

            }
        }
        return uuidMethodList;
    }

    private void parseJsonObject() {
        try {
            JSONArray devices = uuidMethodObject.getJSONArray("device");

            int length = devices.length();

            for (int i = 0; i < length; i++) {

                String device = devices.getJSONObject(i).getString("name");

                if (deviceName.equals(device)) {

                    // uuid methods

                    methodJsonArray = devices.getJSONObject(i).getJSONArray("method");

                    parseMethodObject(methodJsonArray);

                }
            }


        } catch (JSONException jsonexception) {

        }
    }

    private void parseMethodObject(JSONArray jsonArray) {

        int size = jsonArray.length();

        Log.i("method size", "" + size);

        for (int i = 0; i < size; i++) {

            try {

                deviceMethodList.add(jsonArray.getJSONObject(i).getString("name"));

            } catch (JSONException jsonException) {

            }

        }
    }


}
