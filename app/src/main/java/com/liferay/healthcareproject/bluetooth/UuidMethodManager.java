package com.liferay.healthcareproject.bluetooth;

import java.util.List;

/**
 * Created by flatfisher on 10/6/15.
 */
public class UuidMethodManager {

    public final static int READ_METHOD = 0;

    public final static int WRITE_METHOD = 1;

    public final static int NOTIFY_METHOD = 2;

    private boolean isFinish = false;

    private List<UuidMethod> uuidMethodList;

    private int maxMethodValue;

    private int currentMethodValue;

    private int currentMethodType;

    public UuidMethodManager(List<UuidMethod> uuidMethodList){

        this.uuidMethodList = uuidMethodList;

        maxMethodValue = uuidMethodList.size();

        currentMethodValue = -1;

    }

    public boolean isCheckFinish(){
        return this.isFinish;
    }

    public UuidMethod getUuidMethod(){

        currentMethodValue++;

        if (currentMethodValue>=maxMethodValue){

            isFinish = true;

        }

        UuidMethod uuidMethod = uuidMethodList.get(currentMethodValue);

        return uuidMethod;
    }

}
