package com.liferay.healthcareproject.bluetooth;

import java.util.List;

/**
 * Created by flatfisher on 10/6/15.
 */
public class UuidMethodManager {

    private List<UuidMethod> uuidMethodList;

    private int maxMethodCount;

    private int currentMethodCount;

    public UuidMethodManager(List<UuidMethod> uuidMethodList) {

        this.uuidMethodList = uuidMethodList;

        maxMethodCount = uuidMethodList.size();

        currentMethodCount = uuidMethodList.size() - uuidMethodList.size();

    }

    public UuidMethod getUuidMethod() {

        UuidMethod uuidMethod;

        if (currentMethodCount==maxMethodCount){

           uuidMethod = uuidMethodList.get(currentMethodCount-1);

        }else{

            uuidMethod = uuidMethodList.get(currentMethodCount);

            currentMethodCount++;

        }

        return uuidMethod;

    }

}
