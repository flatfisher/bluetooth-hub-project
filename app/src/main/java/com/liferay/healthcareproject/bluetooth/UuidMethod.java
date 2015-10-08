package com.liferay.healthcareproject.bluetooth;

public class UuidMethod {

    public final static int READ = 0;

    public final static int WRITE = 1;

    public final static int NOTIFY = 2;

    public final static int FINISH = 3;

    private String name;

    private String uuid;

    private int methodType;

    private String value;

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setValue(String value){
        this.value = value;
    }

    public void setMethodType(int methodType) {
        this.methodType = methodType;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMethodType() {
        return methodType;
    }

    public String getValue() {
        return value;
    }
}